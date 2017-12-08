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
    return this.get<Usuario>('usuario');
  }

  buscarUsuario(id: string){
    return this.get<Usuario>('usuario', id);
  }
}
