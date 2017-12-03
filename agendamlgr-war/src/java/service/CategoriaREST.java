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
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
            throw new AgendamlgNotFoundException("Categoria no encontrado");
        }else{
            CategoriaProxy categoriaProxy = new CategoriaProxy(categoria);
            return categoriaProxy;
        }
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<CategoriaProxy> buscarTodasLasCategorias(){
        List<CategoriaProxy> categorias = categoriaFacade.findAll().stream().map(CategoriaProxy::new).collect(Collectors.toList());
        return categorias;
    }
    
    @GET
    @Path("/preferencias/{idUsuario}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CategoriaProxy> buscarPreferenciasUsuario(@PathParam("idUsuario") String idUsuario) throws AgendamlgException, AgendamlgNotFoundException{
        Usuario usuario = usuarioFacade.find(idUsuario);
        if(usuario == null){
            throw new AgendamlgNotFoundException("Usuario no existe");
        }
        List<CategoriaProxy> preferencias = categoriaFacade.buscarPreferenciasUsuario(usuario).stream().map(CategoriaProxy::new).collect(Collectors.toList());
        return preferencias;
    }
    
    @GET
    @Path("/categoriasEvento/{idEvento}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CategoriaProxy> buscarCategoriasEvento(@PathParam("idEvento") int idEvento) throws AgendamlgException, AgendamlgNotFoundException{
        Evento evento = eventoFacade.find(idEvento);
        if(evento == null){
            throw new AgendamlgNotFoundException("Evento no existe");
        }
        List<CategoriaProxy> categoriasEvento = categoriaFacade.buscarCategoriasEvento(evento).stream().map(CategoriaProxy::new).collect(Collectors.toList());
        return categoriasEvento;
    }
    
    public static class CategoriaProxy implements Serializable{
        public int id;
        public String nombre;
        public CategoriaProxy(Categoria categoria){
            this.id = categoria.getId();
            this.nombre = categoria.getNombre();
        }
    }
    
}
