/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.ejb;

import app.entity.Categoria;
import app.entity.Usuario;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author melchor9000
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "agendamlgr-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }

    @SuppressWarnings("unchecked")
    public List<Usuario> buscarUsuariosPreferencias(List<Categoria> categorias) {
        //select i from Item i join i.categoryList category where category in :categories
        if (categorias != null && !categorias.isEmpty()) {
            Query q = this.em.createQuery("select distinct u from Usuario u join u.categoriaList c where c in :categorias");
            q.setParameter("categorias", categorias);
            return (List<Usuario>) q.getResultList();
        }else{
            return (new ArrayList<>());
        }
    }
}
