package co.edu.poli.proyecto;

import co.edu.poli.proyecto.modelo.Producto;
import co.edu.poli.proyecto.util.GeneradorDatosUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Segunda clase ejecutable del proyecto (main #2).
 * <p>
 * Lee los archivos generados por {@link GenerateInfoFiles} y crea dos reportes
 * CSV ordenados de mayor a menor:
 * </p>
 * <ul>
 *   <li>{@code reporte_vendedores.csv}: {@code NombreCompleto;DineroRecaudado}</li>
 *   <li>{@code reporte_productos.csv}: {@code NombreProducto;PrecioPorUnidad}
 *       (ordenado por cantidad vendida)</li>
 * </ul>
 * <p>
 * Valida los datos: las lineas incoherentes (formato erroneo, producto o
 * vendedor inexistente, cantidades o precios negativos) se ignoran y se
 * informan como advertencias. No solicita ningun dato al usuario.
 * </p>
 */
public class main {

    /** Nombre del reporte de vendedores. */
    private static final String REPORTE_VENDEDORES = "reporte_vendedores.csv";

    /** Nombre del reporte de productos. */
    private static final String REPORTE_PRODUCTOS = "reporte_productos.csv";

    /** Advertencias acumuladas durante la lectura. */
    private static final List<String> ADVERTENCIAS = new ArrayList<String>();

    /**
     * Punto de entrada: lee los archivos, calcula y escribe los reportes, y
     * muestra un mensaje de exito o de error.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        try {
            File carpeta = new File(GeneradorDatosUtil.CARPETA_SALIDA);
            Map<Integer, Producto> productos = leerProductos(
                    new File(carpeta, GeneradorDatosUtil.ARCHIVO_PRODUCTOS));
            Map<Long, String> vendedores = leerVendedores(
                    new File(carpeta, GeneradorDatosUtil.ARCHIVO_VENDEDORES));

            Map<Integer, Long> unidadesPorProducto = new LinkedHashMap<Integer, Long>();
            for (Integer id : productos.keySet()) {
                unidadesPorProducto.put(id, Long.valueOf(0));
            }
            Map<Long, Double> dineroPorVendedor = new LinkedHashMap<Long, Double>();
            for (Long documento : vendedores.keySet()) {
                dineroPorVendedor.put(documento, Double.valueOf(0));
            }

            int archivos = procesarVentas(carpeta, productos, vendedores, unidadesPorProducto, dineroPorVendedor);
            if (archivos == 0) {
                throw new IOException("No se encontraron archivos de ventas (" + GeneradorDatosUtil.PREFIJO_VENTAS
                        + "*.txt) en la carpeta '" + GeneradorDatosUtil.CARPETA_SALIDA
                        + "'. Ejecute primero GenerateInfoFiles.");
            }

            escribirReporteVendedores(vendedores, dineroPorVendedor);
            escribirReporteProductos(productos, unidadesPorProducto);

            for (String advertencia : ADVERTENCIAS) {
                System.out.println("ADVERTENCIA: " + advertencia);
            }
            System.out.println("Reportes generados exitosamente: " + REPORTE_VENDEDORES + " y " + REPORTE_PRODUCTOS);
            System.out.println("Archivos de ventas procesados: " + archivos
                    + " | Lineas ignoradas por datos incoherentes: " + ADVERTENCIAS.size());
        } catch (IOException excepcion) {
            System.err.println("Error al generar los reportes: " + excepcion.getMessage());
        } catch (RuntimeException excepcion) {
            System.err.println("Error inesperado al generar los reportes: " + excepcion);
        }
    }

    /**
     * Lee {@code productos.txt} y lo guarda en un mapa por identificador.
     *
     * @param archivo archivo de productos
     * @return mapa idProducto a Producto
     * @throws IOException si el archivo no existe o no se puede leer
     */
    private static Map<Integer, Producto> leerProductos(File archivo) throws IOException {
        Map<Integer, Producto> productos = new LinkedHashMap<Integer, Producto>();
        BufferedReader lector = abrirLector(archivo, "productos");
        try {
            String linea;
            int numero = 0;
            while ((linea = lector.readLine()) != null) {
                numero++;
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linea.split(";");
                try {
                    if (partes.length < 3) {
                        throw new NumberFormatException("faltan campos");
                    }
                    int id = Integer.parseInt(partes[0].trim());
                    double precio = Double.parseDouble(partes[2].trim());
                    if (precio < 0) {
                        advertir(archivo, numero, "precio negativo");
                    } else if (productos.containsKey(Integer.valueOf(id))) {
                        advertir(archivo, numero, "producto repetido " + id);
                    } else {
                        productos.put(Integer.valueOf(id), new Producto(id, partes[1].trim(), precio));
                    }
                } catch (NumberFormatException e) {
                    advertir(archivo, numero, "formato erroneo");
                }
            }
        } finally {
            lector.close();
        }
        if (productos.isEmpty()) {
            throw new IOException("El archivo " + archivo.getName() + " no contiene productos validos.");
        }
        return productos;
    }

    /**
     * Lee {@code vendedores.txt} y lo guarda en un mapa por documento.
     *
     * @param archivo archivo de vendedores
     * @return mapa numeroDocumento a nombre completo
     * @throws IOException si el archivo no existe o no se puede leer
     */
    private static Map<Long, String> leerVendedores(File archivo) throws IOException {
        Map<Long, String> vendedores = new LinkedHashMap<Long, String>();
        BufferedReader lector = abrirLector(archivo, "vendedores");
        try {
            String linea;
            int numero = 0;
            while ((linea = lector.readLine()) != null) {
                numero++;
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linea.split(";");
                try {
                    if (partes.length < 4) {
                        throw new NumberFormatException("faltan campos");
                    }
                    long documento = Long.parseLong(partes[1].trim());
                    vendedores.put(Long.valueOf(documento), partes[2].trim() + " " + partes[3].trim());
                } catch (NumberFormatException e) {
                    advertir(archivo, numero, "formato erroneo");
                }
            }
        } finally {
            lector.close();
        }
        if (vendedores.isEmpty()) {
            throw new IOException("El archivo " + archivo.getName() + " no contiene vendedores validos.");
        }
        return vendedores;
    }

    /**
     * Recorre la carpeta y procesa todos los archivos {@code ventas_*.txt}.
     * Como la cantidad de vendedores es desconocida no hay nombres fijos.
     * Si un vendedor tiene varios archivos, sus valores se suman.
     *
     * @return cantidad de archivos de ventas procesados
     * @throws IOException si un archivo no se puede leer
     */
    private static int procesarVentas(File carpeta, Map<Integer, Producto> productos,
            Map<Long, String> vendedores, Map<Integer, Long> unidadesPorProducto,
            Map<Long, Double> dineroPorVendedor) throws IOException {
        File[] archivos = carpeta.listFiles();
        if (archivos == null) {
            throw new IOException("No existe la carpeta '" + carpeta.getName()
                    + "'. Ejecute primero GenerateInfoFiles.");
        }
        int procesados = 0;
        for (File archivo : archivos) {
            String nombre = archivo.getName();
            if (archivo.isFile() && nombre.startsWith(GeneradorDatosUtil.PREFIJO_VENTAS)
                    && nombre.endsWith(".txt")) {
                procesarArchivoVentas(archivo, productos, vendedores, unidadesPorProducto, dineroPorVendedor);
                procesados++;
            }
        }
        return procesados;
    }

    /** Procesa un unico archivo de ventas. */
    private static void procesarArchivoVentas(File archivo, Map<Integer, Producto> productos,
            Map<Long, String> vendedores, Map<Integer, Long> unidadesPorProducto,
            Map<Long, Double> dineroPorVendedor) throws IOException {
        BufferedReader lector = abrirLector(archivo, "ventas");
        try {
            String encabezado = lector.readLine();
            Long documento = leerDocumentoEncabezado(encabezado);
            if (documento == null || !vendedores.containsKey(documento)) {
                advertir(archivo, 1, "encabezado invalido o vendedor inexistente; se ignora el archivo");
                return;
            }
            String linea;
            int numero = 1;
            while ((linea = lector.readLine()) != null) {
                numero++;
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linea.split(";");
                try {
                    if (partes.length < 2) {
                        throw new NumberFormatException("faltan campos");
                    }
                    Integer idProducto = Integer.valueOf(Integer.parseInt(partes[0].trim()));
                    int cantidad = Integer.parseInt(partes[1].trim());
                    Producto producto = productos.get(idProducto);
                    if (producto == null) {
                        advertir(archivo, numero, "producto inexistente " + idProducto);
                    } else if (cantidad <= 0) {
                        advertir(archivo, numero, "cantidad no valida " + cantidad);
                    } else {
                        unidadesPorProducto.put(idProducto,
                                Long.valueOf(unidadesPorProducto.get(idProducto).longValue() + cantidad));
                        double valor = cantidad * producto.getPrecioPorUnidad();
                        dineroPorVendedor.put(documento,
                                Double.valueOf(dineroPorVendedor.get(documento).doubleValue() + valor));
                    }
                } catch (NumberFormatException e) {
                    advertir(archivo, numero, "formato erroneo");
                }
            }
        } finally {
            lector.close();
        }
    }

    /** Extrae el numero de documento de la primera linea ({@code Tipo;Numero}). */
    private static Long leerDocumentoEncabezado(String encabezado) {
        if (encabezado == null) {
            return null;
        }
        String[] partes = encabezado.split(";");
        if (partes.length < 2) {
            return null;
        }
        try {
            return Long.valueOf(Long.parseLong(partes[1].trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Escribe {@code reporte_vendedores.csv}, de mayor a menor dinero recaudado. */
    private static void escribirReporteVendedores(Map<Long, String> vendedores,
            Map<Long, Double> dineroPorVendedor) throws IOException {
        List<Map.Entry<Long, Double>> filas = new ArrayList<Map.Entry<Long, Double>>(dineroPorVendedor.entrySet());
        Collections.sort(filas, new Comparator<Map.Entry<Long, Double>>() {
            @Override
            public int compare(Map.Entry<Long, Double> a, Map.Entry<Long, Double> b) {
                return Double.compare(b.getValue().doubleValue(), a.getValue().doubleValue()); // mayor a menor
            }
        });
        BufferedWriter escritor = abrirEscritor(REPORTE_VENDEDORES);
        try {
            for (Map.Entry<Long, Double> fila : filas) {
                escritor.write(vendedores.get(fila.getKey()) + ";"
                        + String.format(Locale.US, "%.2f", fila.getValue()));
                escritor.newLine();
            }
        } finally {
            escritor.close();
        }
    }

    /** Escribe {@code reporte_productos.csv}, de mayor a menor cantidad vendida. */
    private static void escribirReporteProductos(Map<Integer, Producto> productos,
            Map<Integer, Long> unidadesPorProducto) throws IOException {
        List<Map.Entry<Integer, Long>> filas = new ArrayList<Map.Entry<Integer, Long>>(unidadesPorProducto.entrySet());
        Collections.sort(filas, new Comparator<Map.Entry<Integer, Long>>() {
            @Override
            public int compare(Map.Entry<Integer, Long> a, Map.Entry<Integer, Long> b) {
                return Long.compare(b.getValue().longValue(), a.getValue().longValue()); // mayor a menor
            }
        });
        BufferedWriter escritor = abrirEscritor(REPORTE_PRODUCTOS);
        try {
            for (Map.Entry<Integer, Long> fila : filas) {
                Producto producto = productos.get(fila.getKey());
                escritor.write(producto.getNombreProducto() + ";"
                        + String.format(Locale.US, "%.2f", producto.getPrecioPorUnidad()));
                escritor.newLine();
            }
        } finally {
            escritor.close();
        }
    }

    /** Abre un archivo para lectura con un mensaje claro si no existe. */
    private static BufferedReader abrirLector(File archivo, String descripcion) throws IOException {
        if (!archivo.isFile()) {
            throw new IOException("No se encontro el archivo de " + descripcion + " ("
                    + archivo.getPath() + "). Ejecute primero GenerateInfoFiles.");
        }
        return new BufferedReader(new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8));
    }

    /** Abre un archivo para escritura (UTF-8). */
    private static BufferedWriter abrirEscritor(String nombre) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nombre), StandardCharsets.UTF_8));
    }

    /** Guarda una advertencia con el archivo y la linea donde ocurrio. */
    private static void advertir(File archivo, int linea, String motivo) {
        ADVERTENCIAS.add(archivo.getName() + " linea " + linea + ": " + motivo);
    }
}
