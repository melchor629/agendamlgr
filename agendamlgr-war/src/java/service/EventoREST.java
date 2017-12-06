package service;

import app.ejb.CategoriaFacade;
import app.ejb.EventoFacade;
import app.ejb.UsuarioFacade;
import app.entity.Categoria;
import app.entity.Evento;
import app.entity.Usuario;
import app.exception.AgendamlgException;
import app.exception.AgendamlgNotFoundException;
import geolocation.Geolocation;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
@Path("evento")
public class EventoREST {

    // Fachada de eventos para poder trabajar con estos
    @EJB
    private EventoFacade eventoFacade;

    // Fachada de usuarios para poder trabajar con estos
    @EJB
    private UsuarioFacade usuarioFacade;

    // Fachada de categorias para trabajar con estas
    @EJB
    private CategoriaFacade categoriaFacade;

    // Obtener todos los eventos creados por un usuario
    // Esta ruta necesita autenticacion
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("usuario")
    public List<EventoProxy> buscarEventosUsuario(@HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgNotFoundException {
        Usuario usuario = usuarioFacade.find(TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token)));

        List<Evento> listaEventos = eventoFacade.buscarEventosUsuario(usuario);

        List<EventoProxy> retorno = new ArrayList<>();

        for (Evento evento : listaEventos) {
            retorno.add(new EventoProxy(evento));
        }

        return retorno;
    }

    // Obtener todos los eventos existentes en el sistema
    /* Esta RUTA necesita autenticacion(jwt token) en el caso que el evento a obtener no este validado! */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("")
    public List<EventoProxy> buscarEventos(@HeaderParam("bearer") String token) throws AgendamlgNotFoundException, NotAuthenticatedException {

        // Usuario que podria tener la sesion iniciada
        Usuario usuarioSesion = usuarioDesdeToken(token);

        List<Evento> eventos = eventoFacade.buscarEventosTipoUsuario(usuarioSesion);

        List<EventoProxy> listaEventos = new ArrayList<>();

        for (Evento evento : eventos) {
            listaEventos.add(new EventoProxy(evento));
        }

        return listaEventos;
    }

    // Obtener un evento dada una id, pasada por URL
    /* Esta RUTA necesita autenticacion(jwt token) en el caso que el evento a obtener no este validado! */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("{id}")
    public EventoProxy buscarEvento(@PathParam("id") int id, @HeaderParam("bearer") String token) throws AgendamlgNotFoundException, NotAuthenticatedException {

        // Usuario que podria tener la sesion iniciada
        Usuario usuarioSesion = usuarioDesdeToken(token);
        boolean periodista = usuarioSesion != null && usuarioSesion.getTipo() == 3;

        Evento evento = eventoFacade.find(id);

        if (!(evento != null && (periodista || evento.getValidado() == 1))) {
            throw new AgendamlgNotFoundException("Evento con id " + id + " no existe");
        }

        return new EventoProxy(evento);
    }

    // Crear un Evento, POST. Se recibe un JSON con las caracteristicas del evento a crear
    /*Esta RUTA necesita autenticacion(jwt token) para crear un evento, y evidentemente para que
      este sea creado a validado o no
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Path("")
    public EventoProxy crearEvento(EventoProxy evento, @HeaderParam("bearer") String token) throws AgendamlgException, AgendamlgNotFoundException, NotAuthenticatedException {
        // Implementacion muy similar a la actualizacion de un evento

        Usuario usuario = usuarioFacade.find(TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token)));

        // Si cualquiera de los campos del evento recibido es nulo, se devuelve
        // una excepcion
        if (evento == null || evento.categoriaList == null || evento.descripcion == null || evento.direccion == null || evento.fecha == null || evento.nombre == null || evento.tipo == null) {
            throw new AgendamlgException("Hay parametros a null a la hora de crear el evento");
        }

        // Evento que sera persistido
        Evento eventoDB = new Evento();

        // Trasladar los campos del evento que se ha recibido en el json al
        // evento que se quiere crear. Considerar que no todos los campos
        // de la entidad son modificados
        /*
            Los campos no actualizables de un evento son:
            id y campos de flickr
         */
        // Actualizar categorias. Crear lista de categorias
        List<Categoria> listaCategorias = new ArrayList<>();

        for (CategoriaREST.CategoriaProxy categoria : evento.categoriaList) {
            listaCategorias.add(categoriaFacade.find(categoria.id));
        }

        // eventoDB.setCategoriaList(listaCategorias);
        // Est se fija en el servidor
        eventoDB.setDescripcion(evento.descripcion);
        eventoDB.setDireccion(evento.direccion);
        eventoDB.setFecha(evento.fecha);
        eventoDB.setNombre(evento.nombre);
        eventoDB.setPrecio(evento.precio);
        eventoDB.setTipo(evento.tipo);

        // Fijar el usuario creador del evento
        eventoDB.setCreador(usuario);

        // El evento se establece como validado o no dependiendo del usuario que
        // lo haya creado
        eventoDB.setValidado(usuario.getTipo() == 3 ? Short.parseShort("1") : Short.parseShort("0"));

        // Rellenar latitud y longitud del evento
        // Obtener lat y long
        String coordenadas = buscarCoordenadas(evento.direccion);

        // Separar coordenadas devueltas en latitud y longitud
        String[] coords = coordenadas.split(",");

        eventoDB.setLatitud(new BigDecimal(coords[0]));
        eventoDB.setLongitud(new BigDecimal(coords[1]));

        // Proceder a borrar el evento
        eventoFacade.crearEventoTipoUsuario(eventoDB, listaCategorias);

        // Se devuelve el evento que se acaba de actualizar
        return new EventoProxy(eventoDB);
    }

    // Actualizar un evento. Dado un evento se actualiza este en el servidor
    /* Esta RUTA necesita autenticacion(jwt token), dado que se va a hacer una
       operacion de actualizacion. Ademas un evento solo puede ser editado
       por un periodista.
    
       Cuando un evento es actualizado se devuelve el evento ya con la actualizacion
       reflejada.
    
       A tener en cuenta que no todos los campos son actualizables, como por ejemplo
       id.
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Path("")
    public EventoProxy actualizarEvento(EventoProxy evento, @HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgException, AgendamlgNotFoundException {

        Usuario usuario = usuarioFacade.find(TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token)));

        // Si el usuario no es periodista lanzar excepcion
        if (usuario.getTipo() != 3) {
            throw new AgendamlgException("Solo un periodista puede editar eventos");
        }

        // Si cualquiera de los campos del evento recibido es nulo, se devuelve
        // una excepcion
        if (evento == null || evento.id == null || evento.categoriaList == null || evento.descripcion == null || evento.direccion == null || evento.fecha == null || evento.nombre == null || evento.tipo == null) {
            throw new AgendamlgException("Hay parametros a null a la hora de editar el evento");
        }

        Evento eventoDB = eventoFacade.find(evento.id);

        // Comprobar si se ha encontrado el evento o lanzar excepcion
        if (eventoDB == null) {
            throw new AgendamlgNotFoundException("Evento a editar no encontrado");
        }

        // Trasladar los campos del evento que se ha recibido en el json al
        // evento de la base de datos (solo campos editables). Considerar que no
        // todos los campos de la entidad son modificados
        /*
            Los campos no actualizables de un evento son:
            id, validado (tendra su propio endpoint para hacer esta accion) , 
            creador, latitud, longitud y campos de flickr
         */
        // Actualizar categorias. Crear lista de categorias
        List<Categoria> listaCategorias = new ArrayList<>();

        for (CategoriaREST.CategoriaProxy categoria : evento.categoriaList) {
            listaCategorias.add(categoriaFacade.find(categoria.id));
        }

        // El metodo al que se llama del bean es el encargado de actualizar la
        // lista de categorias
        eventoDB.setDescripcion(evento.descripcion);
        eventoDB.setDireccion(evento.direccion);
        eventoDB.setFecha(evento.fecha);
        eventoDB.setNombre(evento.nombre);
        eventoDB.setPrecio(evento.precio);
        eventoDB.setTipo(evento.tipo);

        // Proceder a editar el evento
        eventoFacade.editarEventoTipoUsuario(eventoDB, listaCategorias, usuario);

        // Se devuelve el evento que se acaba de actualizar
        return new EventoProxy(eventoDB);
    }

    // Eliminar un evento. Dada una id de evento se procede a la eliminacion de este
    /* Esta RUTA necesita autenticacion(jwt token), dado que se va a hacer una
       operacion de eliminacion, ademas se ha de comprobar que el usuario logueado
       es periodista, que es quien puede borrar eventos
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("{id}")
    public String borrarEvento(@PathParam("id") int id, @HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgException, AgendamlgNotFoundException {
        Usuario usuario = usuarioFacade.find(TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token)));

        eventoFacade.borrarEvento(usuario, id);

        // Codigo de exito
        return "{\"status\": \"ok\"}";
    }

    // Establecer un evento como validado, esto solo puede hacerlo un periodista
    /*
       Esta RUTA necesita autenticacion(jwt token), dado que se va a hacer una
       operacion de validar un evento, que solo puede hacerlo un periodista
       Devuelve el evento que acaba de ser validado
     */
    @PUT
    @Path("validar/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public EventoProxy validarEvento(@PathParam("id") int id, @HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgNotFoundException, AgendamlgException {
        Usuario usuario = usuarioFacade.find(TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token)));

        eventoFacade.validarEvento(usuario, id);

        Evento evento = eventoFacade.find(id);
        return new EventoProxy(evento);
    }

    // Filtrado de eventos
    /*
        Este endpoint devuelve un filtrado de los eventos basado en el criterio de
        filtrado proporcionado, el cual es modelado en la clase estatica anidada
        Filtrado.
        Devuelve una lista de EventoProxy
        Esta RUTA necesita autenticacion(jwt token) dado que se van a listar
        eventos que puede que no esten validados, de forma que los no validados
        solo seran listados para los periodistas
        Lanza la excepcion si se proporciona un token no valido
     */
    @GET
    @Path("filtrar")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public List<EventoProxy> filtrarEventos(Filtrado filtro, @HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgException {
        // Crear un objeto filtrado acorde a los QueryParam recibidos
        
        
        Usuario usuarioSesion = usuarioDesdeToken(token);

        // Controlar que no se proporcione un filtro incorrecto, es decir nulo
        // o con parametros necesarios de este nulos
        if (filtro == null || filtro.mostrarDeMiPreferencia == null || filtro.ordenarPorDistancia == null) {
            throw new AgendamlgException("El filtro es nulo o parametros esenciales de este son nulos");
        }
        
        // Si se ha decidido ordenar por distancia y faltan latitud longitud y
        // radio se lanza excepcion
        if(filtro.mostrarDeMiPreferencia && (filtro.latitud == null || filtro.longitud == null || filtro.radio == null)){
            throw new AgendamlgException("Se ha decidido filtrar por distancia,"
                    + "pero parametros esenciales como radio, latitud y longitud"
                    + "no se han proporcionado");
        }

        List<Categoria> listaCategorias;

        // Dependiendo de si se quieren mostrar los eventos que sean preferencia del usuario
        // o bien aquellos que se adscriben a las preferencias seleccionadas
        if (filtro.mostrarDeMiPreferencia) {
            listaCategorias = categoriaFacade.buscarPreferenciasUsuario(usuarioSesion);
        } else {
            // Se van a mostrar de las categorias seleccionadas

            // Si no se ha proporcionado ninguna categoria se muestran todas
            if (filtro.categoriasSeleccionadas == null) {
                // Todas las categorias
                listaCategorias = categoriaFacade.findAll();
            } else {
                listaCategorias = new ArrayList<>();
                for (Integer idCategoria : filtro.categoriasSeleccionadas) {
                    listaCategorias.add(categoriaFacade.find(idCategoria));
                }
            }
        }

        List<Evento> listaEventos = eventoFacade.buscarEventoCategorias(listaCategorias, usuarioSesion, filtro.ordenarPorDistancia, filtro.latitud, filtro.longitud, filtro.radio);

        // Crear instancias de EventoProxy para hacer el retorno
        List<EventoProxy> retorno = new ArrayList<>();

        for (Evento evento : listaEventos) {
            retorno.add(new EventoProxy(evento));
        }

        return retorno;
    }

    /**
     * ***********************************************************************
     */
    /* Metodos auxiliares para los endpoints */
    // Dada una direccion permite obtener las coordenadas en las que se encuentra
    // esta. Metodo util y necesario en la creacion de eventos
    public String buscarCoordenadas(String direccion) throws AgendamlgException {
        try {
            return Geolocation.encontrarCoordenadas(direccion);
        } catch (IOException ex) {
            throw new AgendamlgException("Coordenadas no encontradas - Dirección Inválida", ex);
        }
    }

    // Dado un token devuelve el usuario que tiene dicho token asociado
    // Si no se pasa token se devuelve un nulo
    public Usuario usuarioDesdeToken(String token) throws NotAuthenticatedException {
        Usuario usuario = null;

        if (token != null) {
            String id = TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token));
            usuario = usuarioFacade.find(id);
        }

        return usuario;
    }

    /**
     * ****************************************************************************
     */
    /* Clases estaticas anidadas necesarias para dar soporte al transporte de datos */
 /* Clase estatica anidada serializable que representa el criterio de filtrado
       de eventos. Esto permite seleccionar los eventos de forma mas precisa que
       obteniendolos todos
     */
    public static class Filtrado implements Serializable {

        public Boolean ordenarPorDistancia;
        public Integer radio;
        public Double latitud;
        public Double longitud;
        public Boolean mostrarDeMiPreferencia;
        public List<Integer> categoriasSeleccionadas;

        public Filtrado() {
        }

        public Filtrado(boolean ordenarPorDistancia, int radio, double latitud, double longitud, boolean mostrarDeMiPreferencia, List<Integer> categoriasSeleccionadas) {
            this.ordenarPorDistancia = ordenarPorDistancia;
            this.radio = radio;
            this.latitud = latitud;
            this.longitud = longitud;
            this.mostrarDeMiPreferencia = mostrarDeMiPreferencia;
            this.categoriasSeleccionadas = categoriasSeleccionadas;
        }

    }

    /* Clase estatica anidada Serializable la cual permite representar un evento
       que no se corresponde exactamente con la representacion hecha en susodicha clase
       empleada para la entidad JPA */
    public static class EventoProxy implements Serializable {

        // Propiedades con visiblidad publica
        // Id del evento
        public Integer id;

        // Tipo del evento 1=Una vez, 2=Recurrente, 3=Persistente
        public Short tipo;

        // Nombre del evento
        public String nombre;

        // Descripcion del evento
        public String descripcion;

        // Fecha del evento
        public Date fecha;

        // Precio del evento
        public BigDecimal precio;

        // Direccion del evento
        public String direccion;

        // Estado validacion del evento
        public short validado;

        // Lista de categorias del evento, seran objetos CategoriaProxy, para facilitar
        // la operacion en el lado del cliente
        public List<CategoriaREST.CategoriaProxy> categoriaList;

        // ID Usuario creador del evento
        public String creador;

        public Double latitud, longitud;

        // 
        public String propiedadInventada;

        public EventoProxy() {

        }

        public EventoProxy(Integer id, Short tipo, String nombre, String descripcion, Date fecha, BigDecimal precio, String direccion, short validado, List<CategoriaREST.CategoriaProxy> categoriaList, String creador, Double latitud, Double longitud, String propiedadInventada) {
            this.id = id;
            this.tipo = tipo;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.fecha = fecha;
            this.precio = precio;
            this.direccion = direccion;
            this.validado = validado;
            this.categoriaList = categoriaList;
            this.creador = creador;
            this.latitud = latitud;
            this.longitud = longitud;
            this.propiedadInventada = propiedadInventada;
        }

        // Acepta un evento en el constructor
        public EventoProxy(Evento evento) {
            // Construye el evento a partir de un evento ya existente
            this.id = evento.getId();
            this.tipo = evento.getTipo();
            this.nombre = evento.getNombre();
            this.descripcion = evento.getDescripcion();
            this.fecha = evento.getFecha();
            this.precio = evento.getPrecio();
            this.direccion = evento.getDireccion();
            this.validado = evento.getValidado();
            this.latitud = evento.getLatitud() != null ? evento.getLatitud().doubleValue() : null;
            this.longitud = evento.getLongitud() != null ? evento.getLongitud().doubleValue() : null;

            this.categoriaList = new ArrayList<>();
            // Rellenar de ids la lista de categorias
            for (Categoria categoria : evento.getCategoriaList()) {
                this.categoriaList.add(new CategoriaREST.CategoriaProxy(categoria));
            }

            this.creador = evento.getCreador().getId();
        }

    }

}
