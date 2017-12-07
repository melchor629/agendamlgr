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
                throw new AgendamlgException("Tipo inválido: " + evento.getTipo());
            }
            if (usuario == null) {
                throw new AgendamlgException("Usuario anónimo no puede crear eventos");
            } else if (usuario.getTipo() == 1) {
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
            throw new AgendamlgException("Hay campos invalidos", e);
        }
    }

    private int findLastId() {
        Query q = this.em.createQuery("select max(e.id) from Evento e");
        return (int) q.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<Evento> buscarEventosUsuario(Usuario usuario) throws AgendamlgNotFoundException {
        if (usuario != null) {
            Query q = this.em.createQuery("select e from Evento e where e.creador.id=:id");
            q.setParameter("id", usuario.getId());
            return (List) q.getResultList();
        } else {
            throw new AgendamlgNotFoundException("No existe ese usuario");
        }
    }

    @SuppressWarnings("unchecked")
    public List<Evento> buscarEventosTipoUsuario(Usuario usuario) {
        Date ahora = new Date(System.currentTimeMillis());
        if (usuario != null && usuario.getTipo() == 3) {
            Query q = this.em.createQuery("select e from Evento e where e.fecha > :hoy");
            q.setParameter("hoy", ahora, TemporalType.TIMESTAMP);
            return (List) q.getResultList();
        } else {
            Query q = this.em.createQuery("select e from Evento e where e.fecha > :hoy and e.validado = 1");
            q.setParameter("hoy", ahora, TemporalType.TIMESTAMP);
            return (List) q.getResultList();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Evento> buscarEventoCategorias(List<Categoria> categorias, Usuario usuario, boolean filtroCercania, double x, double y, double radio) {
        // Dado que una de las columnas es de tipo LONGVARCHAR, JPQL no permite usar
        // distinct para evitar obtener filas repetidas, de ahí que se
        // haga el procesamiento a mano para lograr esto
        List<Evento> listaEventos;

        Date ahora = new Date(System.currentTimeMillis());
        if (usuario != null && usuario.getTipo() == 3) {
            Query q;
            if (categorias != null && categorias.size() > 0) {
                q = this.em.createQuery("select e from Evento e join e.categoriaList c where c in :categorias and e.fecha > :hoy ORDER BY e.fecha ASC");
                q.setParameter("categorias", categorias);
            } else {
                q = em.createQuery("SELECT e FROM Evento e WHERE e.fecha > :hoy ORDER BY e.fecha ASC");
            }
            q.setParameter("hoy", ahora, TemporalType.TIMESTAMP);
            listaEventos = q.getResultList();
        } else {
            Query q;
            if (categorias != null && categorias.size() > 0) {
                q = this.em.createQuery("select e from Evento e join e.categoriaList c where c in :categorias and e.fecha > :hoy and e.validado = 1 ORDER BY e.fecha ASC");
                q.setParameter("categorias", categorias);
            } else {
                q = em.createQuery("SELECT e FROM Evento e WHERE e.fecha > :hoy AND e.validado = 1 ORDER BY e.fecha ASC");
            }
            q.setParameter("hoy", ahora, TemporalType.TIMESTAMP);
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
            // Se almacenan los eventos ordenados por distancia (de cercano a lejano)
            // La clave del mapa es la que se usa para la ordenacion
            Map<Double, Evento> mapa = new TreeMap<>();

            // Se procede a rellenar el mapa
            for (Evento evento : duplicadosEliminados) {
                double distAEvento = distanciaAEvento(x, y, evento);

                // Solo se meten en el mapa aquellos eventos que esten dentro del radio
                if (distAEvento <= radio) {
                    mapa.put(distAEvento, evento);
                }
            }

            // A continuacion se obtiene una lista ordenada de los eventos que se han introducido en el map
            // Se vacia la lista de eventos
            duplicadosEliminados.clear();

            for (Map.Entry<Double, Evento> entrada : mapa.entrySet()) {
                duplicadosEliminados.add(entrada.getValue());
            }
        }

        return duplicadosEliminados;
    }

    public void validarEvento(Usuario usuario, int idEvento) throws AgendamlgException, AgendamlgNotFoundException {
        if (usuario == null) {
            throw new AgendamlgException("Un usuario anónimo no puede validar eventos");
        } else if (usuario.getTipo() == 3) {
            Evento evento = this.find(idEvento);
            if (evento != null) {
                if (evento.getValidado() == 0) {
                    evento.setValidado((short) 1);
                    enviarCorreoInteresados(evento);
                    enviarCorreoCreador(evento, evento.getCreador());
                } else {
                    throw new AgendamlgException("El evento ya ha sido validado");
                }
            } else {
                throw new AgendamlgNotFoundException("No existe un evento con el identificador " + idEvento);
            }
        } else {
            throw new AgendamlgException("El usuario " + usuario.getId() + " no tiene permisos para realizar esta acción");
        }
    }

    // Métodos auxiliares
    // Dada una ubicacion (x,y) y un evento devuelve la distancia hasta ese evento
    private double distanciaAEvento(double x, double y, Evento evento) {

        double eventoX = evento.getLatitud().doubleValue();
        double eventoY = evento.getLongitud().doubleValue();
        return Haversine.distance(x, y, eventoX, eventoY);
    }

    /*
    Esto ya no sirve, hemos pasado de vivir en un mundo plano a vivir en uno
    esferico
    // Dados dos punto de la forma (x1,y1),(x2,y2) calcula la distancia entre ambos
    private double distanciaEntreDosPuntos(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }
    */
    public void borrarEvento(Usuario usuario, int idEvento) throws AgendamlgException, AgendamlgNotFoundException {
        if (usuario == null) {
            throw new AgendamlgException("Un usuario anónimo no puede borrar eventos");
        } else if (usuario.getTipo() == 3) {
            Evento evento = find(idEvento);
            if (evento == null) {
                throw new AgendamlgNotFoundException("No existe el evento con identificador " + idEvento);
            }
            remove(evento);
        } else {
            throw new AgendamlgException("El usuario '" + usuario.getId() + "' no tiene permisos para borrar eventos");
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
                throw new AgendamlgException("Tipo inválido: " + evento.getTipo());
            }
            if (usuarioQueEdita == null) {
                throw new AgendamlgException("Usuario anónimo no puede editar eventos");
            } else if (usuarioQueEdita.getTipo() == 3) {
                this.actualizarCategoriaEvento(evento, categoriasEvento);
            } else {
                throw new AgendamlgException("El usuario '" + usuarioQueEdita.getId() + "' no tiene permisos para editar eventos");
            }
        } catch (ConstraintViolationException e) {
            throw new AgendamlgException("Hay campos invalidos", e);
        }
    }

    // Este metodo permite actualizar las categorias del evento que se le ofrece
    private void actualizarCategoriaEvento(Evento evento, List<Categoria> categoriasEvento) throws AgendamlgNotFoundException {
        //No se puede modificar el creador ni el validado
        Evento original = find(evento.getId());
        if (original == null) {
            throw new AgendamlgNotFoundException("El evento original debe existir");
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
    public static class Haversine {

        private static final int EARTH_RADIUS = 6371; // Approx Earth radius in KM

        public static double distance(double startLat, double startLong,
                double endLat, double endLong) {

            double dLat = Math.toRadians((endLat - startLat));
            double dLong = Math.toRadians((endLong - startLong));

            startLat = Math.toRadians(startLat);
            endLat = Math.toRadians(endLat);

            double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return EARTH_RADIUS * c; // <-- d
        }

        public static double haversin(double val) {
            return Math.pow(Math.sin(val / 2), 2);
        }
    }
}
