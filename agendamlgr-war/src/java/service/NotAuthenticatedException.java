package service;

import java.io.Serializable;

/**
 * When you try to access a REST API endpoint that requires the user to be authenticated
 * @author Melchor Alejo Garau Madrigal
 */
public class NotAuthenticatedException extends Exception implements Serializable {

    private int errorId;

    private NotAuthenticatedException() {
        super("Debe haber un usuario autenticado para usar este servicio");
        this.errorId = 20;
    }

    private NotAuthenticatedException(Throwable cause) {
        super("La sesión del usuario ha expirado", cause);
        this.errorId = 21;
    }

    public static NotAuthenticatedException noAutenticado() {
        return new NotAuthenticatedException();
    }

    public static NotAuthenticatedException expirado(Throwable t) {
        return new NotAuthenticatedException(t);
    }

    public int getErrorId() {
        return errorId;
    }
}
