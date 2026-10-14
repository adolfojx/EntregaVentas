package co.edu.poli.proyecto.modelo;

/**
 * Representa un producto que vende la tienda.
 * <p>
 * Es una clase "modelo": solo guarda datos y sabe convertirlos a una linea
 * de texto con el formato del archivo {@code productos.txt}.
 * </p>
 */
public class Producto {

    /** Identificador unico del producto (no se repite). */
    private final int idProducto;

    /** Nombre del producto, por ejemplo "Camisa Ref.1". */
    private final String nombreProducto;

    /** Precio de una unidad del producto. */
    private final double precioPorUnidad;

    /**
     * Crea un producto nuevo.
     *
     * @param idProducto      identificador unico del producto
     * @param nombreProducto  nombre del producto
     * @param precioPorUnidad precio de una unidad
     */
    public Producto(int idProducto, String nombreProducto, double precioPorUnidad) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.precioPorUnidad = precioPorUnidad;
    }

    /** @return el identificador del producto */
    public int getIdProducto() {
        return idProducto;
    }

    /** @return el nombre del producto */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /** @return el precio de una unidad */
    public double getPrecioPorUnidad() {
        return precioPorUnidad;
    }

    /**
     * Convierte el producto en una linea para {@code productos.txt}.
     *
     * @return texto con el formato {@code IDProducto;NombreProducto;PrecioPorUnidad}
     */
    public String toFileLine() {
        return idProducto + ";" + nombreProducto + ";" + precioPorUnidad;
    }
}
