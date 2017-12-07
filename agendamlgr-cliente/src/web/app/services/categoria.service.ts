import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AbstractService } from './abstract.service';
import { Categoria } from '../interfaces/categoria';
@Injectable()
export class CategoriaService extends AbstractService{
  constructor(http: HttpClient){
    super(http);
    console.log('Conectado a CategoriaService');
  }
  buscarCategoria(id: number){
    return this.http.get<Categoria>('http://localhost:8080/agendamlgr-war/rest/categoria/'+id);
  }
  buscarTodasLasCategorias(){
    return this.http.get<Categoria[]>('http://localhost:8080/agendamlgr-war/rest/categoria');
  }
  buscarPreferenciasUsuario(){
    return this.http.get<Categoria[]>('http://localhost:8080/agendamlgr-war/rest/categoria/preferencias',{headers: this.setTokenHeader()});
  }
  buscarCategoriasEvento(id: number){
    return this.http.get<Categoria[]>('http://localhost:8080/agendamlgr-war/rest/categoria/categoriasEvento/'+id);
  }
}
