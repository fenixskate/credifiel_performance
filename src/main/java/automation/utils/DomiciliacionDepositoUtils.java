package automation.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.regex.*;

public final class DomiciliacionDepositoUtils {
    private DomiciliacionDepositoUtils() {}

    public static Path generarSantander(Path origen, Path carpeta) throws IOException {
        String contenido = new String(Files.readAllBytes(origen), StandardCharsets.ISO_8859_1);
        String[] lineas = contenido.split("\\r?\\n", -1);
        if (lineas.length < 2) throw new IllegalArgumentException("Falta la fila 2 de Santander");
        String[] campos = lineas[1].split(",", -1);
        if (campos.length < 5) throw new IllegalArgumentException("Falta E2 de Santander");
        int inicio = contenido.indexOf(lineas[1]);
        for (int i = 0; i < 4; i++) inicio += campos[i].length() + 1;
        String descripcion = campos[4].stripTrailing();
        Files.createDirectories(carpeta);
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (;;) {
            String numero = Integer.toString(1_000_000 + random.nextInt(9_000_000));
            String nueva = descripcion + "_" + numero;
            String copia = contenido.substring(0, inicio) + nueva
                    + " ".repeat(Math.max(0, campos[4].length() - nueva.length()))
                    + contenido.substring(inicio + campos[4].length());
            Path destino = carpeta.resolve("santander_9783_" + numero + ".csv");
            try {
                Files.write(destino, copia.getBytes(StandardCharsets.ISO_8859_1), StandardOpenOption.CREATE_NEW);
                System.out.println("[DEPOSITO SANTANDER] Descripción=" + nueva + " | " + destino);
                return destino;
            } catch (FileAlreadyExistsException siguiente) { /* Generar otra descripción. */ }
        }
    }

    public static Path generarBanamex(Path origen, Path carpeta) throws IOException {
        String contenido = new String(Files.readAllBytes(origen), StandardCharsets.ISO_8859_1);
        String[] lineas = contenido.split("\\r?\\n", -1);
        if (lineas.length < 14) throw new IllegalArgumentException("Falta la fila 14 de Banamex");
        // B14 termina en la siguiente coma; el importe posterior tiene sus propias comillas CSV.
        int primeraComa = lineas[13].indexOf(',');
        int segundaComa = lineas[13].indexOf(',', primeraComa + 1);
        String descripcion = lineas[13].substring(primeraComa + 1, segundaComa);
        Matcher numero = Pattern.compile("(Referencia N[^:]*merica:\\s*)(\\d{7})").matcher(descripcion);
        if (!numero.find()) throw new IllegalArgumentException("No se encontró la referencia numérica en B14");
        Files.createDirectories(carpeta);
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (;;) {
            String nuevo = Integer.toString(1_000_000 + random.nextInt(9_000_000));
            if (nuevo.equals(numero.group(2))) continue;
            int filaInicio = contenido.indexOf(lineas[13]);
            int inicio = filaInicio + primeraComa + 1 + numero.start(2);
            int fin = filaInicio + primeraComa + 1 + numero.end(2);
            String copia = contenido.substring(0, inicio) + nuevo + contenido.substring(fin);
            Path destino = carpeta.resolve("banamex_5794_" + nuevo + ".csv");
            try {
                Files.write(destino, copia.getBytes(StandardCharsets.ISO_8859_1), StandardOpenOption.CREATE_NEW);
                System.out.println("[DEPOSITO BANAMEX] Referencia=" + nuevo + " | " + destino);
                return destino;
            } catch (FileAlreadyExistsException siguiente) { /* Generar otra referencia. */ }
        }
    }

    public static Path generar(Path origen, Path carpeta) throws IOException {
        String contenido = new String(Files.readAllBytes(origen), StandardCharsets.ISO_8859_1);
        Matcher campo = Pattern.compile("DOMICILIACION(?:_(\\d+))? *").matcher(contenido);
        if (!campo.find()) throw new IllegalArgumentException("El CSV no contiene DOMICILIACION");
        int inicio = campo.start(), fin = campo.end();
        int consecutivo = campo.group(1) == null ? 1 : Integer.parseInt(campo.group(1)) + 1;
        Files.createDirectories(carpeta);
        for (int numero = consecutivo; ; numero++) {
            String referencia = "DOMICILIACION_" + numero;
            if (referencia.length() > fin - inicio) throw new IllegalArgumentException("Referencia demasiado larga para el layout");
            String copia = contenido.substring(0, inicio) + referencia
                    + " ".repeat(fin - inicio - referencia.length()) + contenido.substring(fin);
            Path destino = carpeta.resolve("bbva_historico_5481_" + numero + ".csv");
            try {
                Files.write(destino, copia.getBytes(StandardCharsets.ISO_8859_1), StandardOpenOption.CREATE_NEW);
                System.out.println("[DEPOSITO] " + referencia + " | " + destino);
                return destino;
            } catch (FileAlreadyExistsException siguiente) { /* Reservar el siguiente consecutivo. */ }
        }
    }
}
