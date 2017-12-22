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
import app.exception.AgendamlgNotFoundException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author melchor9000
 */
@Stateless
public class EventoFacade extends AbstractFacade<Evento> {

    @EJB
    private CategoriaFacade categoriaFacade;

    @EJB
    private GmailBean gmailBean;

    @EJB
    private UsuarioFacade usuarioFacade;

    @PersistenceContext(unitName = "agendamlgr-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoFacade() {
        super(Evento.class);
    }

    private void enviarCorreoInteresados(Evento evento) {
        gmailBean.sendMail(usuarioFacade.buscarUsuariosPreferencias(evento.getCategoriaList()), "Hay un evento que te puede gustar", evento.getNombre() + " es un evento de tu preferencia");
    }

    private void enviarCorreoCreador(Evento evento, Usuario creador) {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(creador);
        gmailBean.sendMail(usuarios, "Tu evento ha sido publicado", "El evento " + evento.getNombre() + " ha sido publicado");
    }

    private void anadirCategoriaEvento(Evento evento, List<Categoria> categoriasEvento) {
        evento.setId(this.findLastId());
        evento.getCategoriaList().addAll(categoriasEvento);
        for (Categoria categoria : evento.getCategoriaList()) {
            categoria = categoriaFacade.find(categoria.getId());
            categoria.getEventoList().add(evento);
        }
    }

    public void crearEventoTipoUsuario(Evento evento, List<Categoria> categoriasEvento) throws AgendamlgException {
        try {
            Usuario usuario = evento.getCreador();
            if (evento.getTipo() < 1 || evento.getTipo() > 3) {
                throw AgendamlgException.tipoInvalido(evento.getTipo());
            }
            if (usuario.getTipo() == 1) {
                evento.setValidado((short) 0);
                this.create(evento);
                this.anadirCategoriaEvento(evento, categoriasEvento);
            } else if (usuario.getTipo() > 1) {
                evento.setValidado((short) 1);
                this.create(evento);
                this.anadirCategoriaEvento(evento, categoriasEvento);
                this.enviarCorreoInteresados(evento);
            }
            //Tenemos que coger el id del evento que se acaba de añadir (yo no sé si esto es thread safe)
        } catch (ConstraintViolationException e) {
            throw AgendamlgException.eventoCamposInvalidos(e);
        }
    }

    private int findLastId() {
        Query q = this.em.createQuery("select max(e.id) from Evento e");
        return (int) q.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<Evento> buscarEventosUsuario(Usuario usuario, boolean todos) throws AgendamlgNotFoundException {
        if (usuario != null) {
            Query q;
            if(todos) {
                q = this.em.createQuery("select e from Evento e where e.creador.id=:id ORDER BY e.fecha ASC");
            } else {
                q = this.em.createQuery("select e from Evento e where e.creador.id=:id and e.validado=1 ORDER BY e.fecha ASC");
            }
            q.setParameter("id", usuario.getId());
            return (List) q.getResultList();
        } else {
            throw AgendamlgNotFoundException.usuarioNoExiste();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Evento> buscarEventosTipoUsuario(Usuario usuario) {
        Date ahora = new Date(System.currentTimeMillis());
        if (usuario != null && usuario.getTipo() == 3) {
            Query q = this.em.createQuery("select e from Evento e where e.fecha > :hoy ORDER BY e.fecha ASC");
            q.setParameter("hoy", ahora, TemporalType.TIMESTAMP);
            return (List) q.getResultList();
        } else {
            Query q = this.em.createQuery("select e from Evento e where e.fecha > :hoy and e.validado = 1 ORDER BY e.fecha ASC");
            q.setParameter("hoy", ahora, TemporalType.TIMESTAMP);
            return (List) q.getResultList();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Evento> buscarEventoCategorias(List<Categoria> categorias, Usuario usuario, boolean filtroCercania, double x, double y, double radio, String textoTitulo) {
        // Dado que una de las columnas es de tipo LONGVARCHAR, JPQL no permite usar
        // distinct para evitar obtener filas repetidas, de ahí que se
        // haga el procesamiento a mano para lograr esto
        
        // Si se decide filtrar ademas por nombre, se constriye aqui mismo el
        // trozo de consulta que permite hacer eso mismo
        String filtroNombre;
        boolean filtroNombreValido = textoTitulo != null && !textoTitulo.trim().isEmpty();
        if(filtroNombreValido){
            filtroNombre =  " and (0 < LOCATE(LOWER(:textoTitulo), LOWER(e.nombre))) ";
        }
        else{
            filtroNombre = " ";
        }
        
        List<Evento> listaEventos;

        Date ahora = new Date(System.currentTimeMillis());
        if (usuario != null && usuario.getTipo() == 3) {
            // Usuario con sesion iniciada y periodista
            Query q;
            if (categorias != null && categorias.size() > 0) {
                q = this.em.createQuery("select e from Evento e join e.categoriaList c where c in :categorias and (e.fecha > :hoy or e.tipo = 2 or e.tipo = 3)"+filtroNombre+"ORDER BY e.fecha ASC");
                q.setParameter("categorias", categorias);
            } else {
                q = em.createQuery("SELECT e FROM Evento e WHERE (e.fecha > :hoy or e.tipo = 2 or e.tipo = 3)"+filtroNombre+"ORDER BY e.fecha ASC");
            }
            q.setParameter("hoy", ahora, TemporalType.TIMESTAMP);
            
            // Si no se ha proporcionado un filtro de nombre nulo
            if(filtroNombreValido){
                q.setParameter("textoTitulo", textoTitulo);
            }
            
            listaEventos = q.getResultList();
        } else {
            // Usuario no logueado o no periodista
            Query q;
            if (categorias != null && categorias.size() > 0) {
                q = this.em.createQuery("select e from Evento e join e.categoriaList c where c in :categorias and (e.fecha > :hoy or e.tipo = 2 or e.tipo = 3) and e.validado = 1"+filtroNombre+"ORDER BY e.fecha ASC");
                q.setParameter("categorias", categorias);
            } else {
                q = em.createQuery("SELECT e FROM Evento e WHERE (e.fecha > :hoy or e.tipo = 2 or e.tipo = 3) AND e.validado = 1"+filtroNombre+"ORDER BY e.fecha ASC");
            }
            q.setParameter("hoy", ahora, TemporalType.TIMESTAMP);
            
            if(filtroNombreValido){
                q.setParameter("textoTitulo", textoTitulo);
            }
            
            listaEventos = q.getResultList();
        }

        // Remover duplicados
        List<Evento> duplicadosEliminados = new ArrayList<>();

        for (Evento evento : listaEventos) {
            if (!duplicadosEliminados.contains(evento)) {
                duplicadosEliminados.add(evento);
            }
        }

        // Llevar a cabo la ordenacion por distancia en la lista de duplicadosEliminados
        // si asi se ha solicitado en el cliente y en base a los parámetros aportados por este
        if (filtroCercania) {
            Comparator<Map.Entry<Double, Evento>> ordenarPorDistanciaBarraFecha = (p1, p2) -> {
                double diff = p1.getKey() - p2.getKey();
                if(Math.abs(diff) < 0.001) return (int) (p1.getValue().getFecha().getTime() - p2.getValue().getFecha().getTime());
                else return (int) Math.round(diff);
            };
            duplicadosEliminados = duplicadosEliminados.stream()
                    .filter(evento -> evento.getLatitud() != null && evento.getLongitud() != null)
                    .map(evento -> new Map.Entry<Double, Evento>() {
                        private final double distancia = distanciaAEvento(x, y, evento);
                        @Override public Double getKey() { return distancia; }
                        @Override public Evento getValue() { return evento; }
                        @Override public Evento setValue(Evento value) { return evento; }
                        @Override public boolean equals(Object o) { return o instanceof Map.Entry && ((Map.Entry<Double, Evento>) o).getKey().equals(getKey()); }
                        @Override public int hashCode() { return getKey().hashCode(); }
                    })
                    .filter(par -> par.getKey() <= radio)
                    .sorted(ordenarPorDistanciaBarraFecha)
                    .map(par -> par.getValue())
                    .collect(Collectors.toList());
        }

        return duplicadosEliminados;
    }

    public void validarEvento(Usuario usuario, int idEvento) throws AgendamlgException, AgendamlgNotFoundException {
        if (usuario.getTipo() == 3) {
            Evento evento = this.find(idEvento);
            if (evento != null) {
                if (evento.getValidado() == 0) {
                    evento.setValidado((short) 1);
                    enviarCorreoInteresados(evento);
                    enviarCorreoCreador(evento, evento.getCreador());
                } else {
                    throw AgendamlgException.eventoYaValidado();
                }
            } else {
                throw AgendamlgNotFoundException.eventoNoExiste(idEvento);
            }
        } else {
            throw AgendamlgException.sinPermisos(usuario.getNombre());
        }
    }

    // Métodos auxiliares
    // Dada una ubicacion (x,y) y un evento devuelve la distancia hasta ese evento
    private double distanciaAEvento(double x, double y, Evento evento) {

        double eventoX = evento.getLatitud().doubleValue();
        double eventoY = evento.getLongitud().doubleValue();
        return Haversine.distance(x, y, eventoX, eventoY);
    }

    public void borrarEvento(Usuario usuario, int idEvento) throws AgendamlgException, AgendamlgNotFoundException {
        if (usuario.getTipo() == 3) {
            Evento evento = find(idEvento);
            if (evento == null) {
                throw AgendamlgNotFoundException.eventoNoExiste(idEvento);
            }
            remove(evento);
        } else {
            throw AgendamlgException.sinPermisos(usuario.getNombre());
        }
    }

    // Este metodo permite actualizar un evento, dado el evento y una lista de categorias
    public void editarEventoTipoUsuario(Evento evento, List<Categoria> categoriasEvento, Usuario usuarioQueEdita) throws AgendamlgException, AgendamlgNotFoundException {
        try {
            // Se obtiene una lista de Categorias "de verdad"
            // No es necesario ya que sobre estos objetos no se invoca
            // una propiedad como la de getEventoList, de ser asi si habria que hacer este paso
            // como pasa en Evento, que el evento que llega desde el cliente tiene
            // categoriaList a null y es por ello que hay que obtener desde el entity manager
            // un evento "de verdad"
            if (evento.getTipo() < 1 || evento.getTipo() > 3) {
                throw AgendamlgException.tipoInvalido(evento.getTipo());
            }
            if (usuarioQueEdita.getTipo() == 3 || evento.getCreador().getId().equals(usuarioQueEdita.getId())) {
                this.actualizarCategoriaEvento(evento, categoriasEvento);
            } else {
                throw AgendamlgException.sinPermisos(usuarioQueEdita.getNombre());
            }
        } catch (ConstraintViolationException e) {
            throw AgendamlgException.eventoCamposInvalidos(e);
        }
    }

    // Este metodo permite actualizar las categorias del evento que se le ofrece
    private void actualizarCategoriaEvento(Evento evento, List<Categoria> categoriasEvento) throws AgendamlgNotFoundException {
        //No se puede modificar el creador ni el validado
        Evento original = find(evento.getId());
        if (original == null) {
            throw AgendamlgNotFoundException.eventoNoExiste(evento.getId());
        }
        evento.setCreador(original.getCreador());
        evento.setValidado(original.getValidado());
        List<Categoria> categoriasOriginal = original.getCategoriaList();

        evento.setCategoriaList(new ArrayList<>());
        evento.getCategoriaList().addAll(categoriasEvento);
        this.edit(evento);

        //Añadir las categorias nuevas
        categoriasEvento.stream()
                .map(categoria -> categoriaFacade.find(categoria.getId()))
                .filter(categoria -> !categoria.getEventoList().contains(evento))
                .forEach(categoria -> {
                    categoria.getEventoList().add(evento);
                    categoriaFacade.edit(categoria);
                });

        //Eliminar las viejas
        categoriasOriginal.stream()
                .filter(categoria -> !evento.getCategoriaList().contains(categoria))
                .forEach(categoria -> {
                    categoria.getEventoList().remove(evento);
                    categoriaFacade.edit(categoria);
                });
    }

    // Clase Java extraida de GitHub que permite calcular la distancia entre dos puntos
    // usando la formula del Haversine (suponemos que la tierra es esferica jeje)
    /**
     * Jason Winn http://jasonwinn.org Created July 10, 2013
     *
     * Description: Small class that provides approximate distance between two
     * points using the Haversine formula.
     *
     * Call in a static context: Haversine.distance(47.6788206, -122.3271205,
     * 47.6788206, -122.5271205) --> 14.973190481586224 [km]
     *
     */
    private static class Haversine {

        private static final int EARTH_RADIUS = 6371; // Approx Earth radius in KM

        static double distance(double startLat, double startLong,
                               double endLat, double endLong) {

            double dLat = Math.toRadians((endLat - startLat));
            double dLong = Math.toRadians((endLong - startLong));

            startLat = Math.toRadians(startLat);
            endLat = Math.toRadians(endLat);

            double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return EARTH_RADIUS * c; // <-- d
        }

        static double haversin(double val) {
            return Math.pow(Math.sin(val / 2), 2);
        }
    }
}
