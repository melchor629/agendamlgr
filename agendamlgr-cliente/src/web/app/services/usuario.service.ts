import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { AbstractService } from './abstract.service';
@Injectable()
export class UsuarioService extends AbstractService{
  constructor(http: Http){
    super(http);
    console.log('Conectado a UsuarioService');
  }
  obtenerUsuarioDeLaSesion(){
    return this.http.get('http://localhost:8080/agendamlgr-war/rest/usuario',{headers: this.setTokenHeader()}).map(res => res.json());
  }
  buscarUsuario(id: string){
    return this.http.get('http://localhost:8080/agendamlgr-war/rest/usuario/'+id).map(res=>res.json());
  }
}
