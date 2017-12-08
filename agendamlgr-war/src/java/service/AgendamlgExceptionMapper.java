package service;

import app.exception.AgendamlgException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Converts the exception {@link AgendamlgException} to JSON
 * automatically
 * @author Melchor Alejo Garau Madrigal
 * @see <a href="https://stackoverflow.com/questions/4687271/jax-rs-how-to-return-json-and-http-status-code-together">this</a>
 */
@Provider
public class AgendamlgExceptionMapper implements ExceptionMapper<AgendamlgException> {

    @Override
    public Response toResponse(AgendamlgException e) {
        String cause = (e.getCause() != null) ? ("\"" + e.getCause().getMessage() + "\"") : "null";
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": {\"message\": \"" + e.getMessage() + "\", \"otherMessage\": " + cause + ", \"error_id\": " + e.getErrorId() + "}}")
                .build();
    }

}
