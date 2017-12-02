package app.exception;

import java.io.Serializable;

/**
 * When you try to do something and one parameter doesn't exist in the DB
 * @author Melchor Alejo Garau Madrigal
 */
public class AgendamlgException extends Exception implements Serializable {

    private Throwable cause;

    public AgendamlgException(String mensajeError) {
        super(mensajeError);
    }

    public AgendamlgException(String mensajeError, Throwable t) {
        super(mensajeError, t);
        cause = t;
    }

    public AgendamlgException(Throwable t) {
        super(t.getMessage(), t);
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
