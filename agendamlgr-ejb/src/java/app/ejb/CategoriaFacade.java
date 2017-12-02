/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.ejb;

import app.entity.Categoria;
import app.entity.Evento;
import app.entity.Usuario;
import app.exception.AgendamlgException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 *
 * @author melchor9000
 */
@Stateless
public class CategoriaFacade extends AbstractFacade<Categoria> {

    @PersistenceContext(unitName = "agendamlg-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoriaFacade() {
        super(Categoria.class);
    }
    
    public List<Categoria> buscarPreferenciasUsuario(Usuario usuario) throws AgendamlgException {
        if(usuario != null) {
            Query q = this.em.createQuery("select c from Categoria c where :usuario member of c.usuarioList");
            q.setParameter("usuario", usuario);
            return (List) q.getResultList();
        } else {
            throw new AgendamlgException("El usuario anónimo no tiene preferencias");
        }
    }
    
    // Devuelve las categorias de un evento dado
    public List<Categoria> buscarCategoriasEvento(Evento evento){
        Query q = this.em.createQuery("select c from Categoria c where :evento member of c.eventoList");
        q.setParameter("evento", evento);
        return q.getResultList();
    }
}
