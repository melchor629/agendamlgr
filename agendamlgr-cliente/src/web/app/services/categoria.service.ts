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
    return this.get<Categoria>('categoria', id);
  }

  buscarTodasLasCategorias(){
    return this.get<Categoria[]>('categoria');
  }

  buscarPreferenciasUsuario(){
    return this.get<Categoria[]>('categoria', 'preferencias');
  }

  buscarCategoriasEvento(id: number){
    return this.get<Categoria[]>('categoria', `categoriasEvento${id}`);
  }
}
