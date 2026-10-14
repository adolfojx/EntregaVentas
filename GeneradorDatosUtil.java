package co.edu.poli.proyecto.util;

import co.edu.poli.proyecto.modelo.Producto;
import co.edu.poli.proyecto.modelo.Vendedor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Caja de herramientas que genera los archivos planos pseudoaleatorios
 * que sirven de entrada al programa de reportes.
 * <p>
 * Contiene los tres metodos exigidos por la guia del proyecto:
 * {@link #createProductsFile(int)}, {@link #createSalesManInfoFile(int)} y
 * {@link #createSalesMenFile(int, String, long)}.
 * </p>
 */
public final class GeneradorDatosUtil {

    /** Nombre de la carpeta donde se guardan todos los archivos generados. */
    public static final String CARPETA_SALIDA = "archivos_generados";

    /** Nombre del archivo de productos. */
    public static final String ARCHIVO_PRODUCTOS = "productos.txt";

    /** Nombre del archivo de vendedores. */
    public static final String ARCHIVO_VENDEDORES = "vendedores.txt";

    /** Prefijo de los archivos de ventas. */
    public static final String PREFIJO_VENTAS = "ventas_";

    private static final String[] NOMBRES_PRODUCTOS = {
        "Camisa", "Pantalon", "Zapatos", "Silla", "Mesa", "Lampara", "Reloj",
        "Nevera", "Bicicleta", "Mochila", "Televisor", "Licuadora"
    };

    private static final String[] NOMBRES = {
        "Mateo", "Sofia", "Santiago", "Camila", "Juan", "Valentina", "Daniel",
        "Laura", "Andres", "Carolina", "Felipe", "Natalia"
    };

    private static final String[] APELLIDOS = {
        "Garcia", "Rodriguez", "Gomez", "Lopez", "Martinez", "Ramirez",
        "Torres", "Fernandez", "Diaz", "Herrera", "Castro", "Vargas"
    };

    private static final String[] TIPOS_DOCUMENTO = {"CC", "CE", "TI", "PA"};

    private static final Random GENERADOR = new Random();

    /** Identificadores de los productos creados (para generar ventas coherentes). */
    private static final List<Integer> IDS_PRODUCTOS = new ArrayList<Integer>();

    private GeneradorDatosUtil() {
        // Clase de utilidades: no se crean objetos de ella.
    }

    /**
     * Crea el archivo {@code productos.txt} con productos pseudoaleatorios.
     *
     * @param productsCount cantidad de productos a generar (mayor que cero)
     * @return la lista de productos generados
     * @throws IOException si no se puede escribir el archivo
     */
    public static List<Producto> createProductsFile(int productsCount) throws IOException {
        validarPositivo(productsCount, "La cantidad de productos");
        IDS_PRODUCTOS.clear();
        List<Producto> productos = new ArrayList<Producto>();
        BufferedWriter escritor = abrirEscritor(new File(carpetaSalida(), ARCHIVO_PRODUCTOS));
        try {
            for (int idProducto = 1; idProducto <= productsCount; idProducto++) {
                String nombre = elegirAleatorio(NOMBRES_PRODUCTOS) + " Ref." + idProducto;
                double precio = 1000 + GENERADOR.nextInt(99000);
                Producto producto = new Producto(idProducto, nombre, precio);
                escritor.write(producto.toFileLine());
                escritor.newLine();
                productos.add(producto);
                IDS_PRODUCTOS.add(Integer.valueOf(idProducto));
            }
        } finally {
            escritor.close();
        }
        return productos;
    }

    /**
     * Crea el archivo {@code vendedores.txt} con vendedores pseudoaleatorios.
     *
     * @param salesmanCount cantidad de vendedores a generar (mayor que cero)
     * @return la lista de vendedores generados
     * @throws IOException si no se puede escribir el archivo
     */
    public static List<Vendedor> createSalesManInfoFile(int salesmanCount) throws IOException {
        validarPositivo(salesmanCount, "La cantidad de vendedores");
        List<Vendedor> vendedores = new ArrayList<Vendedor>();
        List<Long> documentosUsados = new ArrayList<Long>();
        BufferedWriter escritor = abrirEscritor(new File(carpetaSalida(), ARCHIVO_VENDEDORES));
        try {
            for (int i = 0; i < salesmanCount; i++) {
                long documento;
                do {
                    documento = 10000000L + (long) (GENERADOR.nextDouble() * 1990000000L);
                } while (documentosUsados.contains(Long.valueOf(documento)));
                documentosUsados.add(Long.valueOf(documento));
                String apellidos = elegirAleatorio(APELLIDOS) + " " + elegirAleatorio(APELLIDOS);
                Vendedor vendedor = new Vendedor(elegirAleatorio(TIPOS_DOCUMENTO), documento,
                        elegirAleatorio(NOMBRES), apellidos);
                escritor.write(vendedor.toFileLine());
                escritor.newLine();
                vendedores.add(vendedor);
            }
        } finally {
            escritor.close();
        }
        return vendedores;
    }

    /**
     * Crea un archivo de ventas pseudoaleatorias para un vendedor.
     * <p>
     * Si el vendedor ya tiene un archivo de ventas, se crea uno adicional
     * ({@code ventas_nombre_id_2.txt}, {@code _3}, ...). Asi un mismo vendedor
     * puede tener varios archivos.
     * </p>
     *
     * @param randomSalesCount cantidad de ventas a generar (mayor que cero)
     * @param name             nombre del vendedor
     * @param id               numero de documento del vendedor
     * @throws IOException si no se puede escribir el archivo o si primero no
     *                     se generaron productos
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) throws IOException {
        validarPositivo(randomSalesCount, "La cantidad de ventas");
        if (IDS_PRODUCTOS.isEmpty()) {
            throw new IOException("Primero se deben generar los productos (createProductsFile).");
        }
        String base = PREFIJO_VENTAS + name + "_" + id;
        File archivo = new File(carpetaSalida(), base + ".txt");
        int numero = 2;
        while (archivo.exists() && esDeEstaEjecucion(archivo)) {
            archivo = new File(carpetaSalida(), base + "_" + numero + ".txt");
            numero++;
        }
        BufferedWriter escritor = abrirEscritor(archivo);
        try {
            // La primera linea es el encabezado: tipo y numero de documento.
            escritor.write(buscarTipoDocumento(id) + ";" + id);
            escritor.newLine();
            for (int i = 0; i < randomSalesCount; i++) {
                int idProducto = IDS_PRODUCTOS.get(GENERADOR.nextInt(IDS_PRODUCTOS.size())).intValue();
                int cantidad = 1 + GENERADOR.nextInt(20);
                escritor.write(idProducto + ";" + cantidad + ";");
                escritor.newLine();
            }
        } finally {
            escritor.close();
        }
    }

    // ----------------------------------------------------------------- privados

    /** Tipos de documento de los vendedores creados, para el encabezado de ventas. */
    private static final List<Vendedor> VENDEDORES_CREADOS = new ArrayList<Vendedor>();

    /**
     * Archivos de ventas que ya se crearon en esta ejecucion (los de ejecuciones
     * anteriores se sobrescriben).
     */
    private static final List<String> ARCHIVOS_CREADOS = new ArrayList<String>();

    private static boolean esDeEstaEjecucion(File archivo) {
        return ARCHIVOS_CREADOS.contains(archivo.getPath());
    }

    /**
     * Registra los vendedores generados para poder escribir su tipo de documento.
     *
     * @param vendedores lista de vendedores recien generados
     */
    public static void registrarVendedores(List<Vendedor> vendedores) {
        VENDEDORES_CREADOS.clear();
        VENDEDORES_CREADOS.addAll(vendedores);
    }

    private static String buscarTipoDocumento(long id) {
        for (Vendedor vendedor : VENDEDORES_CREADOS) {
            if (vendedor.getNumeroDocumento() == id) {
                return vendedor.getTipoDocumento();
            }
        }
        return "CC";
    }

    private static File carpetaSalida() throws IOException {
        File carpeta = new File(CARPETA_SALIDA);
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta '" + CARPETA_SALIDA + "'.");
        }
        return carpeta;
    }

    private static BufferedWriter abrirEscritor(File archivo) throws IOException {
        ARCHIVOS_CREADOS.add(archivo.getPath());
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8));
    }

    private static String elegirAleatorio(String[] opciones) {
        return opciones[GENERADOR.nextInt(opciones.length)];
    }

    private static void validarPositivo(int valor, String descripcion) {
        if (valor <= 0) {
            throw new IllegalArgumentException(descripcion + " debe ser mayor que cero.");
        }
    }
}
