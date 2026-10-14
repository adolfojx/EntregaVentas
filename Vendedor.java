package co.edu.poli.proyecto.modelo;

/**
 * Representa a un vendedor de la tienda.
 * <p>
 * El numero de documento se guarda como {@code long} para que coincida con la
 * firma {@code createSalesMenFile(int, String, long)} pedida en la guia.
 * </p>
 */
public class Vendedor {

    /** Tipo de documento: CC, CE, TI o PA. */
    private final String tipoDocumento;

    /** Numero de documento de identidad. */
    private final long numeroDocumento;

    /** Nombres del vendedor. */
    private final String nombres;

    /** Apellidos del vendedor. */
    private final String apellidos;

    /**
     * Crea un vendedor nuevo.
     *
     * @param tipoDocumento   tipo de documento (CC, CE, TI, PA)
     * @param numeroDocumento numero de documento
     * @param nombres         nombres
     * @param apellidos       apellidos
     */
    public Vendedor(String tipoDocumento, long numeroDocumento, String nombres, String apellidos) {
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }

    /** @return el tipo de documento */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /** @return el numero de documento */
    public long getNumeroDocumento() {
        return numeroDocumento;
    }

    /** @return los nombres */
    public String getNombres() {
        return nombres;
    }

    /** @return los apellidos */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Convierte el vendedor en una linea para {@code vendedores.txt}.
     *
     * @return texto con el formato {@code TipoDocumento;NumeroDocumento;Nombres;Apellidos}
     */
    public String toFileLine() {
        return tipoDocumento + ";" + numeroDocumento + ";" + nombres + ";" + apellidos;
    }
}
