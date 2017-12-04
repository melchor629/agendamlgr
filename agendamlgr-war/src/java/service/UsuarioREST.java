package service;

import app.ejb.UsuarioFacade;
import app.entity.Usuario;
import app.exception.AgendamlgNotFoundException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;

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
        if(usuario == null) throw new AgendamlgNotFoundException("Usuario con id " + id + " no existe");
        return new UsuarioProxy(usuario);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public UsuarioProxy obtenerUsuarioDeLaSesion(@HeaderParam("bearer") String token) throws NotAuthenticatedException {
        String id = TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token));
        return new UsuarioProxy(usuarioFacade.find(id));
    }


    public static class UsuarioProxy implements Serializable {

        public String id;
        public short tipo;
        public String nombre;
        public String apellidos;
        public String email;

        UsuarioProxy(Usuario usuario) {
            this.id = usuario.getId();
            this.tipo = usuario.getTipo();
            this.nombre = usuario.getNombre();
            this.apellidos = usuario.getApellidos();
            this.email = usuario.getEmail();
        }

    }

}
