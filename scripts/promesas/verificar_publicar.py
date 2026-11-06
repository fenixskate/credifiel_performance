from pathlib import Path
from datetime import datetime, date
import json, shutil, sys
import openpyxl

root=Path.cwd()
if len(sys.argv) != 2:
    raise ValueError('Uso: verificar_publicar.py directorio_trabajo')
work=Path(sys.argv[1])
m=json.loads((work/'manifest.json').read_text(encoding='utf-8'))
staged=work/'generados'
all_ids=[]
def normalized(value):
    if isinstance(value,(datetime,date)):
        return value.isoformat()
    return value

for index,name in enumerate(m['names']):
    old=openpyxl.load_workbook(Path(m['backup'])/name,data_only=False)
    new=openpyxl.load_workbook(staged/name,data_only=False)
    assert old.sheetnames==new.sheetnames, f'Cambiaron hojas: {name}'
    a,b=old.worksheets[0],new.worksheets[0]
    assert b.max_row==501 and b.max_column==8, f'Dimensiones incorrectas: {name}'
    assert [c.value for c in b[1]]==[c.value for c in a[1]], f'Encabezados alterados: {name}'
    expected=m['credits'][index*500:(index+1)*500]
    ids=[str(b.cell(r,1).value) for r in range(2,502)]
    assert ids==expected, f'Créditos incorrectos: {name}'
    all_ids.extend(ids)
    for row in range(2,502):
        for col in range(2,9):
            assert normalized(a.cell(row,col).value)==normalized(b.cell(row,col).value), f'Cambió {name} celda {row},{col}'
            assert a.cell(row,col).number_format==b.cell(row,col).number_format, f'Cambió formato {name} celda {row},{col}'
    assert a.freeze_panes==b.freeze_panes, f'Cambiaron paneles: {name}'
    assert set(map(str,a.merged_cells.ranges))==set(map(str,b.merged_cells.ranges)), f'Cambiaron celdas combinadas: {name}'
    old.close();new.close()
expected_total=len(m['names'])*500
assert len(all_ids)==expected_total and len(set(all_ids))==expected_total, 'Hay créditos duplicados'
for name in m['names']:
    destination=Path(m['directory'])/name
    temporary=destination.with_name(destination.stem+'.actualizando.xlsx')
    shutil.copy2(staged/name,temporary)
    temporary.replace(destination)
report={'resultado':'PASS','archivos':len(m['names']),'registros_por_archivo':500,'activos_unicos':expected_total,
        'sin_duplicados':True,'otras_columnas_conservadas':True,'respaldo':m['backup'],'reporte':m['source']}
(work/'verificacion.json').write_text(json.dumps(report,ensure_ascii=False,indent=2),encoding='utf-8')
print(json.dumps(report,ensure_ascii=False))
