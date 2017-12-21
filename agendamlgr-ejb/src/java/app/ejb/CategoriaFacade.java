/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.ejb;

import app.entity.Categoria;
import app.entity.Evento;
import app.entity.Usuario;
import app.exception.AgendamlgNotFoundException;

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

    @PersistenceContext(unitName = "agendamlgr-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoriaFacade() {
        super(Categoria.class);
    }

    @SuppressWarnings("unchecked")
    public List<Categoria> buscarPreferenciasUsuario(Usuario usuario) {
        Query q = this.em.createQuery("select c from Categoria c where :usuario member of c.usuarioList");
        q.setParameter("usuario", usuario);
        return (List<Categoria>) q.getResultList();
    }

    @SuppressWarnings("unchecked")
    // Devuelve las categorias de un evento dado
    public List<Categoria> buscarCategoriasEvento(Evento evento){
        Query q = this.em.createQuery("select c from Categoria c where :evento member of c.eventoList");
        q.setParameter("evento", evento);
        return (List<Categoria>) q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public boolean eliminarPreferenciaUsuario(Usuario usuario, int preferencia) {
        Query q = this.em.createQuery("select c from Categoria c where :usuario member of c.usuarioList and c.id = :id");
        q.setParameter("usuario", usuario);
        q.setParameter("id", preferencia);
        List<Categoria> categoriaONo = (List<Categoria>) q.getResultList();
        if(!categoriaONo.isEmpty()) {
            categoriaONo.get(0).getUsuarioList().remove(usuario);
            usuario.getCategoriaList().remove(categoriaONo.get(0));
            edit(categoriaONo.get(0));
            return true;
        } else {
            return false;
        }
    }

    public void afegirPreferenciaUsuari(Usuario usuari, int preferencia) throws AgendamlgNotFoundException {
        Categoria categoria = find(preferencia);
        if(categoria == null) throw AgendamlgNotFoundException.categoriaNoExiste(preferencia);

        usuari.getCategoriaList().add(categoria);
        categoria.getUsuarioList().add(usuari);
        edit(categoria);
    }
}
