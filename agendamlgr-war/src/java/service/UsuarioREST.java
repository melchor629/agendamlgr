package service;

import app.ejb.UsuarioFacade;
import app.entity.Usuario;
import app.exception.AgendamlgException;
import app.exception.AgendamlgNotFoundException;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.util.Map;

/**
 * @author Melchor Alejo Garau Madrigal
 */
@Stateless
@Path("usuario")
public class UsuarioREST {

    @EJB private UsuarioFacade usuarioFacade;

    @Path("{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public UsuarioProxy buscarUsuario(@PathParam("id") String id) throws AgendamlgNotFoundException {
        Usuario usuario = usuarioFacade.find(id);
        if(usuario == null) throw AgendamlgNotFoundException.usuarioNoExiste(id);
        return new UsuarioProxy(usuario);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public UsuarioProxy obtenerUsuarioDeLaSesion(@HeaderParam("bearer") String token) throws NotAuthenticatedException {
        DecodedJWT jwt = TokensUtils.decodeJwtToken(token);
        String id = TokensUtils.getUserIdFromJwtTokenOrThrow(jwt);
        return new UsuarioProxy(usuarioFacade.find(id));
    }

    @DELETE
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String borrarUsuario(@PathParam("id") String id, @HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgException, AgendamlgNotFoundException {
        String sesionId = TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token));
        Usuario usuarioSesion = usuarioFacade.find(sesionId);
        Usuario usuario = usuarioFacade.find(id);
        if(usuarioSesion.getTipo() != 3 && !usuarioSesion.equals(usuario)) throw AgendamlgException.sinPermisos(usuarioSesion.getNombre());
        if(usuario == null) throw AgendamlgNotFoundException.usuarioNoExiste(id);
        usuarioFacade.remove(usuario);
        return "{\"status\": \"ok\"}";
    }

    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public UsuarioProxy modificarTipoUsuario(@PathParam("id") String id, Map<String, Integer> datos, @HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgException, AgendamlgNotFoundException {
        String sesionId = TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token));
        Usuario usuarioSesion = usuarioFacade.find(sesionId);
        Usuario usuario = usuarioFacade.find(id);
        if(usuarioSesion.getTipo() != 3 || usuarioSesion.equals(usuario)) throw AgendamlgException.sinPermisos(usuarioSesion.getNombre());
        if(usuario == null) throw AgendamlgNotFoundException.usuarioNoExiste(id);
        short tipo = (short) (int) datos.getOrDefault("tipo", 1);
        if(tipo == 0 || tipo > 3) throw AgendamlgException.eventoCamposInvalidos();
        usuario.setTipo(tipo);
        usuarioFacade.edit(usuario);
        return new UsuarioProxy(usuarioFacade.find(id));
    }


    public static class UsuarioProxy implements Serializable {

        public final String id;
        public final short tipo;
        public final String nombre;
        public final String apellidos;
        public final String email;
        public String image;

        UsuarioProxy(Usuario usuario) {
            this.id = usuario.getId();
            this.tipo = usuario.getTipo();
            this.nombre = usuario.getNombre();
            this.apellidos = usuario.getApellidos();
            this.email = usuario.getEmail();
            this.image = usuario.getImagen();
        }

    }

}
