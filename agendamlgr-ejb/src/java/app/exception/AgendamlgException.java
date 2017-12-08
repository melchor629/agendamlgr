package app.exception;

import java.io.Serializable;

/**
 * When you try to do something and one parameter doesn't exist in the DB
 * @author Melchor Alejo Garau Madrigal
 */
public class AgendamlgException extends Exception implements Serializable {

    private Throwable cause;
    private int errorId;

    private AgendamlgException(String mensajeError, int errorId) {
        super(mensajeError);
        this.errorId = errorId;
    }

    private AgendamlgException(String mensajeError, int errorId, Throwable t) {
        super(mensajeError, t);
        cause = t;
        this.errorId = errorId;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    public int getErrorId() {
        return errorId;
    }

    public static AgendamlgException tipoInvalido(short tipo) {
        return new AgendamlgException("Tipo de evento inválido" + tipo, 1);
    }

    public static AgendamlgException eventoYaValidado() {
        return new AgendamlgException("El evento ya ha sido validado", 2);
    }

    public static AgendamlgException sinPermisos(String usuario) {
        return new AgendamlgException("El usuario '" + usuario + "' no tiene permisos para realizar esta acción", 3);
    }

    public static AgendamlgException eventoCamposInvalidos() {
        return new AgendamlgException("Hay campos vacíos en el evento", 4);
    }

    public static AgendamlgException eventoCamposInvalidos(Throwable t) {
        return new AgendamlgException("Hay campos inválidos en el evento", 4, t);
    }

    public static AgendamlgException otroError(String mensaje, Throwable t) {
        return new AgendamlgException(mensaje, 1000, t);
    }

}
