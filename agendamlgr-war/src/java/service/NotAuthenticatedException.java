package service;

import java.io.Serializable;

/**
 * When you try to access a REST API endpoint that requires the user to be authenticated
 * @author Melchor Alejo Garau Madrigal
 */
public class NotAuthenticatedException extends Exception implements Serializable {

    public NotAuthenticatedException(String message) {
        super(message);
    }
}
