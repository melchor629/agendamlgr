import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AbstractService } from './abstract.service';
import { Evento } from '../interfaces/evento';
import { Categoria } from '../interfaces/categoria';
import { FotosDeEvento } from '../interfaces/fotosDeEvento';

@Injectable()
export class EventoService extends AbstractService{
  constructor(http: HttpClient){
    super(http);
    console.log('Conectado a EventoService');
  }

  buscarEventosUsuario(userId?: string){
    return this.get<Evento[]>('evento', 'usuario' + (userId ? `/${userId}` : ''));
  }

  buscarEventos(){
    return this.get<Evento[]>('evento');
  }

  buscarEvento(id: number){
    return this.get<Evento>('evento', id);
  }

  crearEvento(evento: Evento){
    return this.post<Evento>('evento', '', evento);
  }

  actualizarEvento(evento: Evento){
    return this.put<Evento>('evento', '', evento);
  }

  borrarEvento(id: number){
    return this.delete<{status:string}>('evento', id);
  }

  validarEvento(id: number){
    return this.put<Evento>('evento',`validar`, { id });
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
    return this.get<Evento[]>('evento', 'filtrar',{ params });
  }

  buscarFotosParaEvento(id: number){
    return this.get<FotosDeEvento>('evento' , `fotos/${id}`);
  }
}
