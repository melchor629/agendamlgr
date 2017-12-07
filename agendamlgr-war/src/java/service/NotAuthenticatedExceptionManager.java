package service;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Converts the exception {@link NotAuthenticatedException} to JSON
 * automatically
 * @author Melchor Alejo Garau Madrigal
 * @see <a href="https://stackoverflow.com/questions/4687271/jax-rs-how-to-return-json-and-http-status-code-together">this</a>
 */
@Provider
public class NotAuthenticatedExceptionManager implements ExceptionMapper<NotAuthenticatedException> {

    @Override
    public Response toResponse(NotAuthenticatedException e) {
        String cause = (e.getCause() != null) ? ("\"" + e.getCause().getMessage() + "\"") : "null";
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": {\"message\": \"" + e.getMessage() + "\", \"otherMessage\": " + cause + "}}")
                .build();
    }

}
