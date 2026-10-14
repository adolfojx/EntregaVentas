package co.edu.poli.proyecto;

import co.edu.poli.proyecto.modelo.Vendedor;
import co.edu.poli.proyecto.util.GeneradorDatosUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Primera clase ejecutable del proyecto (main #1).
 * <p>
 * Genera los archivos planos pseudoaleatorios de entrada: productos, vendedores
 * y un archivo de ventas por vendedor (algunos vendedores tienen dos archivos).
 * No solicita ningun dato al usuario.
 * </p>
 */
public class GenerateInfoFiles {

    /** Cantidad de productos a generar. */
    private static final int CANTIDAD_PRODUCTOS = 15;

    /** Cantidad de vendedores a generar. */
    private static final int CANTIDAD_VENDEDORES = 10;

    /** Cantidad de vendedores que reciben un segundo archivo de ventas. */
    private static final int VENDEDORES_CON_DOS_ARCHIVOS = 2;

    /**
     * Punto de entrada: genera todos los archivos y muestra un mensaje de exito
     * o de error.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        try {
            Random azar = new Random();
            limpiarArchivosAnteriores();
            GeneradorDatosUtil.createProductsFile(CANTIDAD_PRODUCTOS);
            List<Vendedor> vendedores = GeneradorDatosUtil.createSalesManInfoFile(CANTIDAD_VENDEDORES);
            GeneradorDatosUtil.registrarVendedores(vendedores);
            int archivosVentas = 0;
            for (int i = 0; i < vendedores.size(); i++) {
                Vendedor vendedor = vendedores.get(i);
                int repeticiones = i < VENDEDORES_CON_DOS_ARCHIVOS ? 2 : 1;
                for (int r = 0; r < repeticiones; r++) {
                    int cantidadVentas = 5 + azar.nextInt(16); // entre 5 y 20
                    GeneradorDatosUtil.createSalesMenFile(cantidadVentas, vendedor.getNombres(),
                            vendedor.getNumeroDocumento());
                    archivosVentas++;
                }
            }
            System.out.println("Generacion de archivos completada exitosamente.");
            System.out.println("Productos generados: " + CANTIDAD_PRODUCTOS);
            System.out.println("Vendedores generados: " + CANTIDAD_VENDEDORES);
            System.out.println("Archivos de ventas generados: " + archivosVentas);
            System.out.println("Los archivos se guardaron en la carpeta '"
                    + GeneradorDatosUtil.CARPETA_SALIDA + "'.");
        } catch (IOException excepcion) {
            System.err.println("Error al generar los archivos: " + excepcion.getMessage());
        } catch (RuntimeException excepcion) {
            System.err.println("Error inesperado al generar los archivos: " + excepcion.getMessage());
        }
    }

    /**
     * Borra los archivos de ventas de ejecuciones anteriores para que no se
     * mezclen vendedores viejos con los nuevos.
     */
    private static void limpiarArchivosAnteriores() {
        File carpeta = new File(GeneradorDatosUtil.CARPETA_SALIDA);
        File[] archivos = carpeta.listFiles();
        if (archivos == null) {
            return;
        }
        for (File archivo : archivos) {
            String nombre = archivo.getName();
            if (nombre.startsWith(GeneradorDatosUtil.PREFIJO_VENTAS) && nombre.endsWith(".txt")) {
                archivo.delete();
            }
        }
    }
}
