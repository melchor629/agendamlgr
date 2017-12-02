package app.exception;

import java.io.Serializable;

/**
 * When you try to do something and one parameter doesn't exist in the DB
 * @author Melchor Alejo Garau Madrigal
 */
public class AgendamlgNotFoundException extends Exception implements Serializable {

    public AgendamlgNotFoundException(String message) {
        super(message);
    }

    public AgendamlgNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AgendamlgNotFoundException(Throwable cause) {
        super(cause);
    }

}
