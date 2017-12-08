import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AbstractService } from './abstract.service';
import { Evento } from '../interfaces/evento';
import { Categoria } from '../interfaces/categoria';
import { FotosDeEvento } from '../interfaces/fotosDeEvento';
import { Foto } from '../interfaces/foto';
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
  validarEvento(id: number){
    return this.http.put('http://localhost:8080/agendamlgr-war/rest/evento/validar/'+id,{headers: this.setTokenHeader()});
  }
  filtrarEventos(ordenarPorDistancia: string,
                 radio: number,
                 latitud: number,
                 longitud: number,
                 mostrarDeMiPreferencia: string,
                 categoriasSeleccionadas: Categoria[]) {
    let params = new HttpParams();
    params = params.append('ordenarPorDistancia', ordenarPorDistancia);
    if(ordenarPorDistancia === "true") params = params.append('radio', radio.toString());
    if(ordenarPorDistancia === "true") params = params.append('latitud', latitud.toString());
    if(ordenarPorDistancia === "true") params = params.append('longitud', longitud.toString());
    params = params.append('mostrarDeMiPreferencia', mostrarDeMiPreferencia);
    categoriasSeleccionadas.forEach(categoria => params = params.append('categoriasSeleccionadas', categoria.id.toString()));
    return this.http.get<Evento[]>('http://localhost:8080/agendamlgr-war/rest/evento/filtrar',{headers: this.setTokenHeader(), params});
  }
  buscarFotosParaEvento(id: number){
    return this.http.get<FotosDeEvento>('http://localhost:8080/agendamlgr-war/rest/evento/fotos/'+id,{headers: this.setTokenHeader()});
  }
}
