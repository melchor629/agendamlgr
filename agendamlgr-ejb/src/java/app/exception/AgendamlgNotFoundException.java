package app.exception;

import java.io.Serializable;

/**
 * When you try to do something and one parameter doesn't exist in the DB
 * @author Melchor Alejo Garau Madrigal
 */
public class AgendamlgNotFoundException extends Exception implements Serializable {

    private int errorId;

    private AgendamlgNotFoundException(String message, int errorId) {
        super(message);
        this.errorId = errorId;
    }

    public int getErrorId() {
        return errorId;
    }

    public static AgendamlgNotFoundException categoriaNoExiste(int id) {
        return new AgendamlgNotFoundException("No existe la categoria '" + id + "'", 10);
    }

    public static AgendamlgNotFoundException usuarioNoExiste(String id) {
        return new AgendamlgNotFoundException("No existe el usuario '" + id + "'", 11);
    }

    public static AgendamlgNotFoundException usuarioNoExiste() {
        return new AgendamlgNotFoundException("No existe el usuario", 11);
    }

    public static AgendamlgNotFoundException eventoNoExiste(int id) {
        return new AgendamlgNotFoundException("No existe el evento '" + id + "'", 12);
    }

}
