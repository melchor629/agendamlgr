package app.exception;

public class AgendamlgException extends Exception {

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
