package service;

import app.ejb.EventoFacade;
import app.entity.Categoria;
import app.entity.Evento;
import app.exception.AgendamlgNotFoundException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Stateless
@Path("evento")
public class EventoREST {
    
    // Fachada de eventos para poder trabajar con estos
    @EJB private EventoFacade fachadaEvento;
    
    // Obtener un evento dada una id, pasada por URL
    /* Esta RUTA necesita autenticacion(jwt token) en el caso que el evento a obtener no este validado! */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("{id}")
    public EventoProxy obtenerEvento(@PathParam("id") String id) throws AgendamlgNotFoundException{
        Evento evento = fachadaEvento.find(Integer.parseInt(id));
        
        if(evento == null) throw new AgendamlgNotFoundException("Evento con id " + id + " no existe");
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
    public EventoProxy crearEvento(EventoProxy evento){
        
        return null;
    }
    
    // Actualizar un evento. Dado un evento se actualiza este en el servidor
    /* Esta RUTA necesita autenticacion(jwt token), dado que se va a hacer una
       operacion de actualizacion. Ademas un evento solo puede ser editado
       por un periodista
    */
    

    // Eliminar un evento. Dada una id de evento se procede a la eliminacion de este
    /* Esta RUTA necesita autenticacion(jwt token), dado que se va a hacer una
       operacion de eliminacion, ademas se ha de comprobar que el usuario logueado
       es periodista, que es quien puede borrar eventos
    */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("{id}")
    public EventoProxy borrarEvento(@PathParam("id") String id){
        
        // Se devuelve el evento que se acaba de borrar
        return null;
    }
    
    /* Clase estatica anidada Serializable la cual permite representar un evento
       que no se corresponde exactamente con la representacion hecha en susodicha clase
       empleada para la entidad JPA */
    public static class EventoProxy implements Serializable {

        // Propiedades con visiblidad publica
        
        // Id del evento
        private Integer id;

        // Tipo del evento 1=Una vez, 2=Recurrente, 3=Persistente
        private short tipo;

        // Nombre del evento
        private String nombre;

        // Descripcion del evento
        private String descripcion;

        // Fecha del evento
        private Date fecha;

        // Precio del evento
        private BigDecimal precio;

        // Direccion del evento
        private String direccion;

        // Estado validacion del evento
        private short validado;

        // Lista de categorias del evento, seran objetos CategoriaProxy, para facilitar
        // la operacion en el lado del cliente
        private List<CategoriaREST.CategoriaProxy> categoriaList;

        // ID Usuario creador del evento
        private String creador;

        
        
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
           
           this.categoriaList = new ArrayList<>();
           // Rellenar de ids la lista de categorias
           for(Categoria categoria : evento.getCategoriaList()){
               this.categoriaList.add(new CategoriaREST.CategoriaProxy(categoria));
           }
           
           this.creador = evento.getCreador().getId();
        }

        public List<CategoriaREST.CategoriaProxy> getCategoriaList() {
            return categoriaList;
        }

        public String getCreador() {
            return creador;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public String getDireccion() {
            return direccion;
        }

        public Date getFecha() {
            return fecha;
        }

        public Integer getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public BigDecimal getPrecio() {
            return precio;
        }

        public short getTipo() {
            return tipo;
        }

        public short getValidado() {
            return validado;
        }

        public void setCategoriaList(List<CategoriaREST.CategoriaProxy> categoriaList) {
            this.categoriaList = categoriaList;
        }

        public void setCreador(String creador) {
            this.creador = creador;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public void setDireccion(String direccion) {
            this.direccion = direccion;
        }

        public void setFecha(Date fecha) {
            this.fecha = fecha;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public void setPrecio(BigDecimal precio) {
            this.precio = precio;
        }

        public void setTipo(short tipo) {
            this.tipo = tipo;
        }

        public void setValidado(short validado) {
            this.validado = validado;
        }
        
        

    }

}
