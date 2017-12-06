import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { AbstractService } from './abstract.service';
@Injectable()
export class CategoriaService extends AbstractService{
  constructor(http: Http){
    super(http);
    console.log('Conectado a CategoriaService');
  }
  buscarCategorias(){
    return this.http.get('http://localhost:8080/agendamlgr-war/rest/categoria').map(res => res.json());
  }
  buscarPreferencias(){
    return this.http.get('http://localhost:8080/agendamlgr-war/rest/categoria/preferencias',{headers: this.setTokenHeader()}).map(res => res.json());
  }
}
