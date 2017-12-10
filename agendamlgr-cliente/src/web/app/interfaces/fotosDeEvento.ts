import { Foto } from './foto';
export class FotosDeEvento{
  fotoPrimariaUrl: string;
  fotos: Foto[];
}

export function fotosDeEventoVacio(): FotosDeEvento {
  return {
    fotoPrimariaUrl: null,
    fotos: []
  };
}