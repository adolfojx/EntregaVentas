# EntregaVentas – Generación y clasificación de datos

Proyecto en Java 8 del módulo *Conceptos Fundamentales de Programación* (Politécnico Grancolombiano).
Autor: Adolfo Jiménez Ortiz · Profesor: Ing. Edwin Ospina Penna

El proyecto tiene **dos (y solo dos) clases con método `main`**; ninguna pide datos al usuario:

| Orden | Clase | Qué hace |
|---|---|---|
| 1 | `GenerateInfoFiles` | Genera en `archivos_generados/` los archivos `productos.txt`, `vendedores.txt` y `ventas_<nombre>_<id>.txt`. |
| 2 | `main` | Lee esos archivos y crea `reporte_vendedores.csv` (dinero recaudado) y `reporte_productos.csv` (por cantidad vendida), de mayor a menor. |

## Cómo ejecutarlo (necesita Java JDK 8 o superior)
- **Windows:** doble clic en `ejecutar.bat`.
- **Mac / Linux:** en la terminal, `bash ejecutar.sh`.
- **Eclipse:** File → Import → Existing Projects into Workspace; clic derecho en `GenerateInfoFiles.java` → Run As → Java Application; después lo mismo con `main.java`.

Siempre se ejecuta **primero** `GenerateInfoFiles` y **después** `main`.

## Estructura
```
src/co/edu/poli/proyecto/
├── GenerateInfoFiles.java   (main #1)
├── main.java                (main #2)
├── modelo/  Producto.java, Vendedor.java
└── util/    GeneradorDatosUtil.java
docs/            Javadoc en HTML (abrir docs/index.html)
conclusion.txt   Lo aprendido, aplicaciones y dificultades
```

## Validación de datos
`main` ignora y reporta como ADVERTENCIA las líneas con formato erróneo, producto o vendedor inexistente, y cantidades o precios negativos.
