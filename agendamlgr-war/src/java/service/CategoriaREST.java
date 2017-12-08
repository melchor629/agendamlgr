/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import app.ejb.CategoriaFacade;
import app.ejb.EventoFacade;
import app.ejb.UsuarioFacade;
import app.entity.Categoria;
import app.entity.Evento;
import app.entity.Usuario;
import app.exception.AgendamlgException;
import app.exception.AgendamlgNotFoundException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.HeaderParam;

/**
 *
 * @author john
 */
@Stateless
@Path("categoria")
public class CategoriaREST{

    @EJB
    private EventoFacade eventoFacade;

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
    public List<CategoriaProxy> buscarPreferenciasUsuario(@HeaderParam("bearer") String token) throws AgendamlgException, AgendamlgNotFoundException, NotAuthenticatedException{
        String idUsuario = TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token));
        Usuario usuario = usuarioFacade.find(idUsuario);
        if(usuario == null){
            throw AgendamlgNotFoundException.usuarioNoExiste();
        }
        return categoriaFacade.buscarPreferenciasUsuario(usuario).stream().map(CategoriaProxy::new).collect(Collectors.toList());
    }
    
    @GET
    @Path("/categoriasEvento/{idEvento}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CategoriaProxy> buscarCategoriasEvento(@PathParam("idEvento") int idEvento) throws AgendamlgNotFoundException{
        Evento evento = eventoFacade.find(idEvento);
        if(evento == null){
            throw AgendamlgNotFoundException.eventoNoExiste(idEvento);
        }
        return categoriaFacade.buscarCategoriasEvento(evento).stream().map(CategoriaProxy::new).collect(Collectors.toList());
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
