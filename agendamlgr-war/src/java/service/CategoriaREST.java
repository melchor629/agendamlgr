/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import app.ejb.CategoriaFacade;
import app.ejb.UsuarioFacade;
import app.entity.Categoria;
import app.entity.Usuario;
import app.exception.AgendamlgNotFoundException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author john
 */
@Stateless
@Path("categoria")
public class CategoriaREST{

    @EJB
    private UsuarioFacade usuarioFacade;

    @EJB
    private CategoriaFacade categoriaFacade;
    
    
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public CategoriaProxy buscarCategoria(@PathParam("id") Integer id) throws AgendamlgNotFoundException{
        Categoria categoria = categoriaFacade.find(id);
        if(categoria == null){
            throw AgendamlgNotFoundException.categoriaNoExiste(id);
        }else{
            return new CategoriaProxy(categoria);
        }
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<CategoriaProxy> buscarTodasLasCategorias(){
        return categoriaFacade.findAll().stream().map(CategoriaProxy::new).collect(Collectors.toList());
    }
    
    @GET
    @Path("/preferencias")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CategoriaProxy> buscarPreferenciasUsuario(@HeaderParam("bearer") String token) throws AgendamlgNotFoundException, NotAuthenticatedException{
        String idUsuario = TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token));
        Usuario usuario = usuarioFacade.find(idUsuario);
        if(usuario == null){
            throw AgendamlgNotFoundException.usuarioNoExiste();
        }
        return categoriaFacade.buscarPreferenciasUsuario(usuario).stream().map(CategoriaProxy::new).collect(Collectors.toList());
    }

    @DELETE
    @Path("/preferencias/{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public String eliminarPreferenciaUsuario(@HeaderParam("bearer") String token, @PathParam("id") int id) throws NotAuthenticatedException {
        String idUsuario = TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token));
        Usuario usuario = usuarioFacade.find(idUsuario);
        return "{\"deleted\": " + categoriaFacade.eliminarPreferenciaUsuario(usuario, id) + "}";
    }

    @POST
    @Path("/preferencias")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<CategoriaProxy> nuevaPreferenciaUsuario(CategoriaProxy categoria, @HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgNotFoundException {
        String idUsuario = TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token));
        Usuario usuario = usuarioFacade.find(idUsuario);
        categoriaFacade.afegirPreferenciaUsuari(usuario, categoria.id);
        return categoriaFacade.buscarPreferenciasUsuario(usuario).stream().map(CategoriaProxy::new).collect(Collectors.toList());
    }
    
    public static class CategoriaProxy implements Serializable{
        public int id;
        public String nombre;

        public CategoriaProxy(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public CategoriaProxy() {
        }



        CategoriaProxy(Categoria categoria){
            this.id = categoria.getId();
            this.nombre = categoria.getNombre();
        }
    }
    
}
