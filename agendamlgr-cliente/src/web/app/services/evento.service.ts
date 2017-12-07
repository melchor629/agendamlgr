import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { AbstractService } from './abstract.service';
@Injectable()
export class EventoService extends AbstractService{
  constructor(http: Http){
    super(http);
    console.log('Conectado a EventoService');
  }
  buscarEventosUsuario(){
    return this.http.get('http://localhost:8080/agendamlgr-war/rest/evento/usuario',{headers: this.setTokenHeader()}).map(res => res.json());
  }
}
