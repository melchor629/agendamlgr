package service;

import java.io.Serializable;

/**
 * When you try to access a REST API endpoint that requires the user to be authenticated
 * @author Melchor Alejo Garau Madrigal
 */
public class NotAuthenticatedException extends Exception implements Serializable {

    private int errorId;

    private NotAuthenticatedException(String message, int errorId) {
        super(message);
        this.errorId = errorId;
    }

    private NotAuthenticatedException(String message, Throwable cause, int errorId) {
        super(message, cause);
        this.errorId = errorId;
    }

    public static NotAuthenticatedException noAutenticado() {
        return new NotAuthenticatedException("Debe haber un usuario autenticado para usar este servicio", 20);
    }

    public static NotAuthenticatedException expirado(Throwable t) {
        return new NotAuthenticatedException("La sesión del usuario ha expirado", t, 21);
    }

    public int getErrorId() {
        return errorId;
    }
}
