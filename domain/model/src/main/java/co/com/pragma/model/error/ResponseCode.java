package co.com.pragma.model.error;

public enum ResponseCode {
    MSSO000("Ocurrió un error inesperado, por favor intenta mas tarde."),
    MSSO001("Operación exitosa."),
    MSSO002("Campos no son validos."),
    MSSO003("La entidad a registrar ya existe en la app."),
    MSSO004("No se encontraron registros con los datos ingresados.");

    private final String message;

    ResponseCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}