from pathlib import Path
import json, shutil, datetime, hashlib, sys, zipfile, io
import openpyxl

root = Path.cwd()
if len(sys.argv) not in (3,4):
    raise ValueError('Uso: preparar.py reporte_descargado directorio_trabajo [cantidad_archivos]')
file_count=int(sys.argv[3]) if len(sys.argv)==4 else 40
if file_count<1:raise ValueError('La cantidad de archivos debe ser positiva')
target_count=file_count*500
work = Path(sys.argv[2])
work.mkdir(parents=True, exist_ok=True)
report = Path(sys.argv[1])
if not report.is_file():
    raise FileNotFoundError(report)
books = []
if report.suffix.lower() == '.zip':
    with zipfile.ZipFile(report) as archive:
        for member in archive.namelist():
            if member.lower().endswith('.xlsx'):
                books.append(openpyxl.load_workbook(io.BytesIO(archive.read(member)), read_only=True, data_only=True))
else:
    books.append(openpyxl.load_workbook(report, read_only=True, data_only=True))
if not books:
    raise ValueError('La descarga no contiene hojas Excel')
credits, seen = [], set()
for sheet in [sheet for book in books for sheet in book]:
    rows = sheet.iter_rows(values_only=True)
    header = next(rows)
    if len(header) < 21 or str(header[20]).strip().casefold() != 'estado':
        raise ValueError('La columna U no es Estado')
    for row in rows:
        if str(row[20] or '').strip().casefold() != 'activo':
            continue
        credit = str(row[0] or '').strip()
        if not credit or credit in seen:
            continue
        seen.add(credit)
        credits.append(credit)
        if len(credits) == target_count:
            break
    if len(credits) == target_count:
        break
for book in books:
    book.close()
if len(credits) != target_count:
    raise ValueError(f'Créditos activos únicos insuficientes: {len(credits)}')

base = root / 'Documentos/Insumos Flujo 4/Template/Generados'
names = ['Template de Carga de Promesas v2' + ('' if i == 1 else f'_{i}') + '.xlsx' for i in range(1,file_count+1)]
stamp = datetime.datetime.now().strftime('%Y%m%d_%H%M%S_%f')
backup = root / 'Documentos/Insumos Flujo 4/Template/Respaldos' / stamp
backup.mkdir(parents=True, exist_ok=False)
template_sources={}
for index,name in enumerate(names):
    source=base/name
    if not source.is_file():
        seed=index%20+1
        source=base/('Template de Carga de Promesas v2'+('' if seed==1 else f'_{seed}')+'.xlsx')
    if not source.is_file():raise FileNotFoundError(source)
    template_sources[name]=str(source)
    template = openpyxl.load_workbook(source, read_only=False, data_only=False)
    if template.worksheets[0].max_row != 501 or template.worksheets[0]['A1'].value != 'num_credito':
        raise ValueError('Template incompatible: ' + name)
    template.close()
    shutil.copy2(source, backup / name)
destination = report
manifest = {'source':str(destination), 'download':str(report), 'backup':str(backup),
            'directory':str(base),'names':names,'credits':credits,'file_count':file_count,'records_per_file':500,'template_sources':template_sources,'source_sha256':hashlib.sha256(destination.read_bytes()).hexdigest()}
(work/'manifest.json').write_text(json.dumps(manifest,ensure_ascii=False),encoding='utf-8')
print(json.dumps({'activos_unicos':len(credits),'archivos':len(names),'por_archivo':500,'reporte':str(destination),'respaldo':str(backup)},ensure_ascii=False))
