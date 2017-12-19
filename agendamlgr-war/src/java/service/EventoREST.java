package service;

import app.ejb.CategoriaFacade;
import app.ejb.EventoFacade;
import app.ejb.UsuarioFacade;
import app.entity.Categoria;
import app.entity.Evento;
import app.entity.Usuario;
import app.exception.AgendamlgException;
import app.exception.AgendamlgNotFoundException;
import flickr.Flickr;
import flickr.Photo;
import flickr.PhotoSetInfo;
import flickr.PhotoSetPhotos;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Stateless
@Path("evento")
public class EventoREST {

    // Maximo tamaño de la descripcion (numero de caracteres) de un evento cuando
    // esta es devuelta como resultado de listar los eventos
    private static final int MAX_CARACTERES_DESCRIPCION = 150;

    // Fachada de eventos para poder trabajar con estos
    @EJB
    private EventoFacade eventoFacade;

    // Fachada de usuarios para poder trabajar con estos
    @EJB
    private UsuarioFacade usuarioFacade;

    // Fachada de categorias para trabajar con estas
    @EJB
    private CategoriaFacade categoriaFacade;


    private static final Function<? super Evento, EventoProxyMini> convertToMiniProxyWithFlickr = evento -> {
        EventoProxyMini miniEvento = new EventoProxyMini(evento);
        if (evento.getFlickruserid() != null && evento.getFlickralbumid() != null) {
            try {
                PhotoSetInfo info = Flickr.Photosets.getInfo(evento.getFlickruserid(), evento.getFlickralbumid());
                miniEvento.fotoUrl = info != null && info.primary != null ? info.primary.mediumSizeUrl : null;
            } catch (IOException ignore) {
            }
        }
        return miniEvento;
    };

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("usuario")
    public List<EventoProxyMini> buscarEventosUsuario(@HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgNotFoundException {
        String userId = TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token));
        Usuario user = usuarioFacade.find(userId);
        return eventoFacade.buscarEventosUsuario(user, true)
                .parallelStream()
                .map(convertToMiniProxyWithFlickr)
                .collect(Collectors.toList());
    }

    // Obtener todos los eventos creados por un usuario
    // Esta ruta necesita autenticacion
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("usuario/{id}")
    public List<EventoProxyMini> buscarEventosUsuario(
            @PathParam("id") String userId,
            @HeaderParam("bearer") String token
    ) throws AgendamlgNotFoundException, NotAuthenticatedException {
        Usuario userIdSesion = usuarioDesdeToken(token);
        Usuario usuario = usuarioFacade.find(userId);
        return eventoFacade.buscarEventosUsuario(usuario, userIdSesion.equals(usuario))
                .parallelStream()
                .map(convertToMiniProxyWithFlickr)
                .collect(Collectors.toList());
    }

    // Obtener todos los eventos existentes en el sistema
    /* Esta RUTA necesita autenticacion(jwt token) en el caso que el evento a obtener no este validado! */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<EventoProxyMini> buscarEventos(@HeaderParam("bearer") String token) throws NotAuthenticatedException {

        // Usuario que podria tener la sesion iniciada
        Usuario usuarioSesion = usuarioDesdeToken(token);
        return eventoFacade.buscarEventosTipoUsuario(usuarioSesion).parallelStream().map(convertToMiniProxyWithFlickr).collect(Collectors.toList());
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
            throw AgendamlgNotFoundException.eventoNoExiste(id);
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
    public EventoProxy crearEvento(EventoProxy evento, @HeaderParam("bearer") String token) throws AgendamlgException, NotAuthenticatedException {
        // Implementacion muy similar a la actualizacion de un evento

        Usuario usuario = usuarioFacade.find(TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token)));

        // Si cualquiera de los campos del evento recibido es nulo, se devuelve
        // una excepcion
        if (evento == null || evento.categoriaList == null || evento.descripcion == null || evento.direccion == null || evento.fecha == null || evento.nombre == null || evento.tipo == null) {
            throw AgendamlgException.eventoCamposInvalidos();
        }

        // Evento que sera persistido
        Evento eventoDB = new Evento();

        // Trasladar los campos del evento que se ha recibido en el json al
        // evento que se quiere crear. Considerar que no todos los campos
        // de la entidad son modificados
        /*
            Los campos no actualizables de un evento son:
            id
         */
        // Actualizar categorias. Crear lista de categorias
        List<Categoria> listaCategorias = evento.categoriaList.stream().map(c -> new Categoria(c.id)).collect(Collectors.toList());

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

        // Rellenar latitud y longitud del evento
        // Obtener lat y long
        String coordenadas = buscarCoordenadas(evento.direccion);

        if (coordenadas != null) {
            // Separar coordenadas devueltas en latitud y longitud
            String[] coords = coordenadas.split(",");

            eventoDB.setLatitud(new BigDecimal(coords[0]));
            eventoDB.setLongitud(new BigDecimal(coords[1]));
        } else {
            // las coordenadas son nulas, se dejan así
            eventoDB.setLatitud(null);
            eventoDB.setLongitud(null);
        }

        // Rellenar los atributos propios de Flickr
        modificarDatosFlickr(eventoDB, evento.flickrUserID, evento.flickrAlbumID);

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
    public EventoProxy actualizarEvento(EventoProxy evento, @HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgException, AgendamlgNotFoundException {

        Usuario usuario = usuarioFacade.find(TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token)));

        // Si cualquiera de los campos del evento recibido es nulo, se devuelve
        // una excepcion
        if (evento == null || evento.id == null || evento.categoriaList == null || evento.descripcion == null || evento.direccion == null || evento.fecha == null || evento.nombre == null || evento.tipo == null) {
            throw AgendamlgException.eventoCamposInvalidos();
        }

        Evento eventoDB = eventoFacade.find(evento.id);

        // Comprobar si se ha encontrado el evento o lanzar excepcion
        if (eventoDB == null) {
            throw AgendamlgNotFoundException.eventoNoExiste(evento.id);
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
        List<Categoria> listaCategorias = evento.categoriaList.stream().map(c -> new Categoria(c.id)).collect(Collectors.toList());

        // El metodo al que se llama del bean es el encargado de actualizar la
        // lista de categorias
        eventoDB.setDescripcion(evento.descripcion);
        eventoDB.setDireccion(evento.direccion);
        eventoDB.setFecha(evento.fecha);
        eventoDB.setNombre(evento.nombre);
        eventoDB.setPrecio(evento.precio);
        eventoDB.setTipo(evento.tipo);
        modificarDatosFlickr(eventoDB, evento.flickrUserID, evento.flickrAlbumID);

        // Actualizacion de coordenadas del evento
        // Rellenar latitud y longitud del evento
        // Obtener lat y long
        String coordenadas = buscarCoordenadas(evento.direccion);

        if (coordenadas != null) {
            // Separar coordenadas devueltas en latitud y longitud
            String[] coords = coordenadas.split(",");

            eventoDB.setLatitud(new BigDecimal(coords[0]));
            eventoDB.setLongitud(new BigDecimal(coords[1]));
        } else {
            // las coordenadas son nulas, se dejan así
            eventoDB.setLatitud(null);
            eventoDB.setLongitud(null);
        }
        
        
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
    @Path("validar")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public EventoProxy validarEvento(Map<String, String> json, @HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgNotFoundException, AgendamlgException {
        Usuario usuario = usuarioFacade.find(TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token)));
        int id = Integer.parseUnsignedInt(json.getOrDefault("id", "0"));
        eventoFacade.validarEvento(usuario, id);
        return new EventoProxy(eventoFacade.find(id));
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
 /*  La URL para el filtrado tendra la siguiente pinta: 
        .../evento/filtrar?ordenarPorDistancia=X&radio=X&latitud=X&longitud=X&mostrarDeMiPreferencia=X&categoriasSeleccionadas=1&categoriasSeleccionadas=2...
        (Y asi con tantas categorias como se desee)
    ¡¡¡OJO!!! El radio se ofrece en Kilometros
     */
    @GET
    @Path("filtrar")
    @Produces({MediaType.APPLICATION_JSON})
    public List<EventoProxyMini> filtrarEventos(@HeaderParam("bearer") String token,
            @DefaultValue("false") @QueryParam("ordenarPorDistancia") Boolean ordenarPorDistancia,
            @DefaultValue("0") @QueryParam("radio") Double radio,
            @DefaultValue("0") @QueryParam("latitud") Double latitud,
            @DefaultValue("0") @QueryParam("longitud") Double longitud,
            @DefaultValue("false") @QueryParam("mostrarDeMiPreferencia") Boolean mostrarDeMiPreferencia,
            @DefaultValue("0") @QueryParam("categoriasSeleccionadas") List<Integer> categoriasSeleccionadas,
            @QueryParam("textoTitulo") String textoTitulo) throws NotAuthenticatedException {
        // Crear un objeto filtrado acorde a los QueryParam recibidos
        Filtrado filtro = new Filtrado(ordenarPorDistancia, radio, latitud, longitud, mostrarDeMiPreferencia, categoriasSeleccionadas);

        Usuario usuarioSesion = usuarioDesdeToken(token);

        List<Categoria> listaCategorias;

        // Dependiendo de si se quieren mostrar los eventos que sean preferencia del usuario
        // o bien aquellos que se adscriben a las preferencias seleccionadas
        if (filtro.mostrarDeMiPreferencia) {
            listaCategorias = categoriaFacade.buscarPreferenciasUsuario(usuarioSesion);
        } else {
            // Se van a mostrar de las categorias seleccionadas

            // Si no se ha proporcionado ninguna categoria se muestran todas
            if (filtro.categoriasSeleccionadas.get(0) == 0) {
                // Todas las categorias
                listaCategorias = categoriaFacade.findAll();
            } else {
                listaCategorias = new ArrayList<>();
                for (Integer idCategoria : filtro.categoriasSeleccionadas) {
                    listaCategorias.add(categoriaFacade.find(idCategoria));
                }
            }
        }

        List<Evento> listaEventos = eventoFacade.buscarEventoCategorias(listaCategorias, usuarioSesion, filtro.ordenarPorDistancia, filtro.latitud, filtro.longitud, filtro.radio, textoTitulo);

        // Crear instancias de EventoProxyMini para hacer el retorno
        return listaEventos.parallelStream().map(convertToMiniProxyWithFlickr).collect(Collectors.toList());
    }

    @GET
    @Path("fotos/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public FotosDeEvento buscarFotosParaEvento(@PathParam("id") int eventoId, @HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgNotFoundException, IOException {
        Usuario usuario = usuarioDesdeToken(token);
        Evento evento = eventoFacade.find(eventoId);
        if (evento == null || ((usuario == null || usuario.getTipo() < 3) && evento.getValidado() == 0)) {
            throw AgendamlgNotFoundException.eventoNoExiste(eventoId);
        }

        if (evento.getFlickruserid() != null && evento.getFlickralbumid() != null) {
            PhotoSetPhotos photoSetPhotos = Flickr.Photosets.getPhotos(evento.getFlickruserid(), evento.getFlickralbumid());
            if (photoSetPhotos != null) {
                return new FotosDeEvento(photoSetPhotos);
            }
        }
        return new FotosDeEvento();
    }

    // Esta ruta permite, dada una direccion obtener las coordenadas en un objeto
    // JSON del tipo: {"encontrado":X, latitud":X, "longitud":x}
    // Solo puede ser usada por usuarios logueados!
    @GET
    @Path("coordenadas/{direccion}")
    @Produces({MediaType.APPLICATION_JSON})
    public Coordenadas obtenerCoordenadas(@PathParam("direccion") String direccion, @HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgException {
        String id = TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token));
        // Si no se proporciona token o este no es valido se lanzara una excepcion

        // Se procede a obtener las coordenadas como un par (latitud,longitud)
        String coordenadas = buscarCoordenadas(direccion);
        double latitud = 0, longitud = 0;
        boolean encontrado = true;

        if (coordenadas != null) {
            // Separar coordenadas devueltas en latitud y longitud
            String[] coords = coordenadas.split(",");

            latitud = Double.parseDouble(coords[0]);
            longitud = Double.parseDouble(coords[1]);
        } else {
            // Se indica que no se han encontrado las coordenadas
            encontrado = false;
        }

        return new Coordenadas(encontrado, latitud, longitud);
    }
    
    
    /**
     * ***********************************************************************
     */
    /* Metodos auxiliares para los endpoints */
    // Dada una direccion permite obtener las coordenadas en las que se encuentra
    // esta. Metodo util y necesario en la creacion de eventos
    private String buscarCoordenadas(String direccion) throws AgendamlgException {
        try {
            return Geolocation.encontrarCoordenadas(direccion);
        } catch (IOException ex) {
            throw AgendamlgException.otroError("Coordenadas no encontradas - Dirección Inválida", ex);
        }
    }

    // Dado un token devuelve el usuario que tiene dicho token asociado
    // Si no se pasa token se devuelve un nulo
    private Usuario usuarioDesdeToken(String token) throws NotAuthenticatedException {
        Usuario usuario = null;

        if (token != null) {
            String id = TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token));
            usuario = usuarioFacade.find(id);
        }

        return usuario;
    }

    // Dado un texto permite abreviarlo a un máximo de caracteres dado
    private final static String NON_THIN = "[^iIl1\\.,']";

    private static int textWidth(String str) {
        return str.length() - str.replaceAll(NON_THIN, "").length() / 2;
    }

    private static String ellipsize(String text, int max) {

        if (textWidth(text) <= max) {
            return text;
        }

        // Start by chopping off at the word before max
        // This is an over-approximation due to thin-characters...
        int end = text.lastIndexOf(' ', max - 3);

        // Just one long word. Chop it off.
        if (end == -1) {
            return text.substring(0, max - 3) + "...";
        }

        // Step forward as long as textWidth allows.
        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);

            // No more spaces.
            if (newEnd == -1) {
                newEnd = text.length();
            }

        } while (textWidth(text.substring(0, newEnd) + "...") < max);

        return text.substring(0, end) + "...";
    }

    private static void modificarDatosFlickr(Evento evento, String userId, String albumId) throws AgendamlgException {
        if (albumId == null || !albumId.equals(evento.getFlickralbumid())) {
            evento.setFlickralbumid(albumId);
        }

        if (userId == null || !userId.equals(evento.getFlickruserid())) {
            evento.setFlickruserid(userId);

            if (userId != null && !userId.matches("\\d+@N\\d\\d")) {
                try {
                    evento.setFlickruserid(
                            Flickr.Urls.lookupUser(String.format("http://www.flickr.com/photos/%s/", userId))
                    );
                    if (evento.getFlickruserid() == null) {
                        throw AgendamlgException.flickrUsernameInvalido(userId);
                    }
                } catch (IOException n) {
                    throw AgendamlgException.otroError("Error al obtener el `user_id' de Flickr", n);
                }
            }
        }
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
        public Double radio;
        public Double latitud;
        public Double longitud;
        public Boolean mostrarDeMiPreferencia;
        public List<Integer> categoriasSeleccionadas;

        public Filtrado() {
        }

        public Filtrado(boolean ordenarPorDistancia, double radio, double latitud, double longitud, boolean mostrarDeMiPreferencia, List<Integer> categoriasSeleccionadas) {
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
    // Se encuentra la clase padre, que es una version abreviada de evento empleada
    // para ser de vuelta en casos en los que se ofrece una lista de eventos
    // Anidada en esta se encuentra la clase que representa un evento al completo
    // empleada en las situaciones en las que se devuelve toda la info sobre este
    public static class EventoProxyMini implements Serializable {

        // Propiedades con visiblidad publica
        // Id del evento
        public Integer id;

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

        public String fotoUrl;

        public EventoProxyMini() {
        }

        private EventoProxyMini(Integer id, String nombre, String descripcion, Date fecha, BigDecimal precio, String direccion) {
            this.id = id;
            this.nombre = nombre;
            this.descripcion = ellipsize(descripcion, MAX_CARACTERES_DESCRIPCION);
            this.fecha = fecha;
            this.precio = precio;
            this.direccion = direccion;
        }

        // Acepta un evento en el constructor
        EventoProxyMini(Evento evento) {
            // Construye el evento a partir de un evento ya existente
            this.id = evento.getId();

            this.nombre = evento.getNombre();
            this.descripcion = ellipsize(evento.getDescripcion(), MAX_CARACTERES_DESCRIPCION);
            this.fecha = evento.getFecha();
            this.precio = evento.getPrecio();
            this.direccion = evento.getDireccion();
        }

    }

    public static class EventoProxy extends EventoProxyMini {

        // Tipo del evento 1=Una vez, 2=Recurrente, 3=Persistente
        public Short tipo;

        // Estado validacion del evento
        public boolean validado;

        // Lista de categorias del evento, seran objetos CategoriaProxy, para facilitar
        // la operacion en el lado del cliente
        public List<CategoriaREST.CategoriaProxy> categoriaList;

        // ID Usuario creador del evento
        public String creador;

        public Double latitud, longitud;

        // Atributos para la funcionalidad relacionada con Flickr
        public String flickrUserID, flickrAlbumID;

        public EventoProxy() {
        }

        public EventoProxy(Short tipo, boolean validado, List<CategoriaREST.CategoriaProxy> categoriaList, String creador, Double latitud, Double longitud, Integer id, String nombre, String descripcion, Date fecha, BigDecimal precio, String direccion, String flickrUserID, String flickrAlbumID) {

            super(id, nombre, descripcion, fecha, precio, direccion);
            this.tipo = tipo;
            this.validado = validado;
            this.categoriaList = categoriaList;
            this.creador = creador;
            this.latitud = latitud;
            this.longitud = longitud;
            this.flickrAlbumID = flickrAlbumID;
            this.flickrUserID = flickrUserID;
        }

        EventoProxy(Evento evento) {
            super(evento);
            this.tipo = evento.getTipo();
            this.validado = evento.getValidado() == 1;

            // Flickr
            this.flickrAlbumID = evento.getFlickralbumid();
            this.flickrUserID = evento.getFlickruserid();
            //

            this.categoriaList = evento.getCategoriaList().stream().map(CategoriaREST.CategoriaProxy::new).collect(Collectors.toList());

            this.latitud = evento.getLatitud() != null ? evento.getLatitud().doubleValue() : null;
            this.longitud = evento.getLongitud() != null ? evento.getLongitud().doubleValue() : null;
            this.creador = evento.getCreador().getId();
            // Volver a fijar la descipcion en su version larga
            this.descripcion = evento.getDescripcion();
        }

    }

    public static class FotosDeEvento implements Serializable {

        public String fotoPrimariaUrl;
        public List<Foto> fotos;

        private FotosDeEvento() {
            fotos = new ArrayList<>();
        }

        private FotosDeEvento(PhotoSetPhotos photoSetPhotos) {
            fotoPrimariaUrl = photoSetPhotos.primary.kindLargeSizeUrl;
            fotos = photoSetPhotos.photos.stream().map(Foto::new).collect(Collectors.toList());
        }
    }

    public static class Foto implements Serializable {

        public String titulo, url;

        private Foto(Photo photo) {
            titulo = photo.title;
            url = photo.kindLargeSizeUrl;
        }
    }

    // Devolucion de coordenadas para una direccion dada
    public static class Coordenadas implements Serializable {

        public boolean encontrado;
        public double latitud, longitud;

        public Coordenadas() {
        }

        public Coordenadas(boolean encontrado, double latitud, double longitud) {
            this.encontrado = encontrado;
            this.latitud = latitud;
            this.longitud = longitud;
        }
    }

}
