import fs from 'node:fs/promises';
import path from 'node:path';
import {createRequire} from 'node:module';
import {pathToFileURL} from 'node:url';
const [work, modules] = process.argv.slice(2);
if (!work || !modules) throw new Error('Uso: generar.mjs directorio_trabajo node_modules');
const requireRuntime = createRequire(path.join(modules, '..', 'promesas-resolver.cjs'));
const {FileBlob, SpreadsheetFile} = await import(pathToFileURL(requireRuntime.resolve('@oai/artifact-tool')).href);
const manifest=JSON.parse(await fs.readFile(path.join(work,'manifest.json'),'utf8'));
const staged=path.join(work,'generados');
await fs.mkdir(staged,{recursive:true});
await fs.mkdir(path.join(work,'previews'),{recursive:true});
for(let index=0;index<manifest.names.length;index++) {
  const name=manifest.names[index];
  const wb=await SpreadsheetFile.importXlsx(await FileBlob.load(path.join(manifest.backup,name)));
  const sheet=wb.worksheets.getItemAt(0);
  const credits=manifest.credits.slice(index*500,(index+1)*500);
  if(credits.length!==500) throw new Error('Lote incompleto: '+name);
  const original=sheet.getRange('A1:H501').values;
  sheet.getRange('A2:A501').values=credits.map(id=>[id]);
  wb.recalculate();
  const current=sheet.getRange('A1:H501').values;
  if(JSON.stringify(current[0])!==JSON.stringify(original[0])) throw new Error('Cambió el encabezado');
  for(let r=1;r<=500;r++) {
    if(current[r][0]!==credits[r-1]) throw new Error('Crédito incorrecto');
    if(JSON.stringify(current[r].slice(1))!==JSON.stringify(original[r].slice(1))) throw new Error('Cambió otra columna');
  }
  const image=await wb.render({sheetName:sheet.name,range:'A1:H6',scale:1,format:'png'});
  await fs.writeFile(path.join(work,'previews',String(index+1).padStart(2,'0')+'.png'),new Uint8Array(await image.arrayBuffer()));
  const output=await SpreadsheetFile.exportXlsx(wb);
  await output.save(path.join(staged,name));
  console.log(`PREPARADO ${index+1}/20 | ${name} | 500 créditos únicos`);
}
console.log('Los 20 archivos están preparados para la verificación independiente antes de reemplazar los originales.');
