import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { AbstractService } from './abstract.service';
@Injectable()
export class CategoriaService extends AbstractService{
  constructor(http: Http){
    super(http);
    console.log('Conectado a CategoriaService');
  }
  buscarCategoria(id: number){
    return this.http.get('http://localhost:8080/agendamlgr-war/rest/categoria/'+id).map(res=>res.json());
  }
  buscarTodasLasCategorias(){
    return this.http.get('http://localhost:8080/agendamlgr-war/rest/categoria').map(res => res.json());
  }
  buscarPreferenciasUsuario(){
    return this.http.get('http://localhost:8080/agendamlgr-war/rest/categoria/preferencias',{headers: this.setTokenHeader()}).map(res => res.json());
  }
  buscarCategoriasEvento(id: number){
    return this.http.get('http://localhost:8080/agendamlgr-war/rest/categoria/categoriasEvento/'+id).map(res=>res.json());
  }
}
