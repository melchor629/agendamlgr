import { Categoria } from './categoria';

export interface Evento{
  id: number;
  nombre: string;
  descripcion: string;
  fecha: string;
  precio: number;
  direccion: string;
  fotoUrl: string;
  tipo: number;
  validado: boolean;
  latitud: number;
  longitud: number;
  creador: string;
  categoriaList: Categoria[];
  flickrUserID: string;
  flickrAlbumID: string;
}

export interface Coordenadas{
    encontrado: boolean;
    latitud: number;
    longitud: number;
}

export function eventoVacio(): Evento {
  return {
      id: undefined,
      nombre: undefined,
      descripcion: undefined,
      fecha: undefined,
      precio: undefined,
      direccion: undefined,
      fotoUrl: undefined,
      tipo: undefined,
      validado: undefined,
      latitud: undefined,
      longitud: undefined,
      creador: undefined,
      categoriaList: [],
      flickrUserID: undefined,
      flickrAlbumID: undefined
  };
}
