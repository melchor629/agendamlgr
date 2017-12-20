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

  eliminarPreferenciaUsuario(categoria: Categoria) {
    return this.delete<{deleted: boolean}>('categoria', `preferencias/${categoria.id}`);
  }

  añadirPreferenciaUsuario(categoria: Categoria) {
    return this.post<Categoria[]>('categoria', 'preferencias', categoria);
  }
}
