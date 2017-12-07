import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AbstractService } from './abstract.service';
import { Usuario } from '../interfaces/usuario';
@Injectable()
export class UsuarioService extends AbstractService{
  constructor(http: HttpClient){
    super(http);
    console.log('Conectado a UsuarioService');
  }
  obtenerUsuarioDeLaSesion(){
    return this.http.get<Usuario>('http://localhost:8080/agendamlgr-war/rest/usuario',{headers: this.setTokenHeader()});
  }
  buscarUsuario(id: string){
    return this.http.get<Usuario>('http://localhost:8080/agendamlgr-war/rest/usuario/'+id);
  }
}
