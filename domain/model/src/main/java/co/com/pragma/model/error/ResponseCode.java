package co.com.pragma.model.error;

public enum ResponseCode {
    MSSO000(500, "Ocurrió un error inesperado, por favor intenta mas tarde."),
    MSSO001(200, "Operación exitosa."),
    MSSO002(400, "Campos no son validos."),
    MSSO003(400, "La entidad a registrar ya existe en la app."),
    MSSO004(404, "No se encontraron registros con los datos ingresados.");

    private final int status;
    private final String htmlMessage;

    ResponseCode(int status, String htmlMessage) {
        this.status = status;
        this.htmlMessage = htmlMessage;
    }

    public int getStatus() {
        return this.status;
    }

    public String getHtmlMessage() {
        return this.htmlMessage;
    }
}