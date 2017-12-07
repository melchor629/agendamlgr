import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AbstractService } from './abstract.service';
import { Evento } from '../interfaces/evento';
import { Categoria } from '../interfaces/categoria';
@Injectable()
export class EventoService extends AbstractService{
  constructor(http: HttpClient){
    super(http);
    console.log('Conectado a EventoService');
  }
  buscarEventosUsuario(){
    return this.http.get<Evento[]>('http://localhost:8080/agendamlgr-war/rest/evento/usuario',{headers: this.setTokenHeader()});
  }
  buscarEventos(){
    return this.http.get<Evento[]>('http://localhost:8080/agendamlgr-war/rest/evento',{headers: this.setTokenHeader()});
  }
  buscarEvento(id: number){
    return this.http.get<Evento>('http://localhost:8080/agendamlgr-war/rest/evento/'+id,{headers: this.setTokenHeader()});
  }
  crearEvento(evento: Evento){
    return this.http.post('http://localhost:8080/agendamlgr-war/rest/evento',evento,{headers: this.setTokenHeader()});
  }
  actualizarEvento(evento: Evento){
    return this.http.put('http://localhost:8080/agendamlgr-war/rest/evento',evento,{headers: this.setTokenHeader()});
  }
  borrarEvento(id: number){
    return this.http.delete('http://localhost:8080/agendamlgr-war/rest/evento/'+id,{headers: this.setTokenHeader()});
  }
  filtrarEventos(ordenarPorDistancia: string,
                 radio: number,
                 latitud: number,
                 longitud: number,
                 mostrarDeMiPreferencia: string,
                 categoriasSeleccionadas: Categoria[]){
    let url = 'http://localhost:8080/agendamlgr-war/rest/evento/filtrar?ordenarPorDistancia='+ordenarPorDistancia+'&radio='+radio+'&latitud='+latitud+'&longitud='+longitud+'&mostrarDeMiPreferencia='+mostrarDeMiPreferencia;
    url+=categoriasSeleccionadas.reduce((query, categoria) => query+"&categoriasSeleccionadas="+categoria.id, "");
    return this.http.get<Evento[]>(url,{headers: this.setTokenHeader()});
  }
  /*
  buscarFotosParaEvento(id: number){
    return this.http.get('http://localhost:8080/agendamlgr-war/rest/evento/fotos/'+id,{headers: this.setTokenHeader()});
  }
  */
}
