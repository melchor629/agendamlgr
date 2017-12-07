import { Categoria } from './categoria';
export interface Evento{
  id: number;
  nombre: string;
  descripcion: string;
  fecha: Date;
  precio: number;
  direccion: string;
  fotoUrl: string;
  tipo: number;
  validado: boolean;
  latitud: number;
  longitud: number;
  categoriaList: Categoria[];
}
