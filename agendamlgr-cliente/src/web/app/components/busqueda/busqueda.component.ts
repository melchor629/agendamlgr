import { Component,OnInit } from '@angular/core';
import { EventoService } from '../../services/evento.service';
import { CategoriaService } from '../../services/categoria.service';
import { Evento } from '../../interfaces/evento';
import { Categoria } from '../../interfaces/categoria';

@Component({
  selector: 'app-busqueda',
  templateUrl: './busqueda.component.html',
  styleUrls: ['./busqueda.component.scss']
})
export class BusquedaComponent implements OnInit {

  categorias: Categoria[];
  preferencias: Categoria[];
  ordenarPorDistancia: boolean;
  radio: number;
  latitud: number;
  longitud: number;
  mostrarDeMiPreferencia: boolean;
  categoriasIds: number[];
  eventos: Evento[] = [];
  palabraFiltro: string;

  constructor(private eventoService: EventoService, private categoriaService: CategoriaService) {
  }

  ngOnInit() {
    this.ordenarPorDistancia = false;
    this.radio = 0;
    this.latitud = 0;
    this.longitud = 0;
    this.mostrarDeMiPreferencia = false;
    this.categoriasIds = [];
    this.listarCategorias();
    this.listarPreferencias();
  }

  buscar() {
      if (this.mostrarDeMiPreferencia) {
        for (let preferencia of this.preferencias) {
          this.categoriasIds.push(preferencia.id);
        }
      }else{
        for (let preferencia of this.preferencias) {
          var index = this.categoriasIds.indexOf(preferencia.id);
          if(index != -1){
            this.categoriasIds.splice(index, 1);
          }
        }
      }
      let categoriasSeleccionadas = [];
      for(let i=0; i<this.categoriasIds.length; i++){
        let c = {
          id: this.categoriasIds[i],
          nombre: ''
        };
        categoriasSeleccionadas.push(c);
      }
      this.listarEventosFiltrados(this.ordenarPorDistancia.toString(),this.radio,this.latitud,this.longitud,this.mostrarDeMiPreferencia.toString(),categoriasSeleccionadas);
  }

  addOrRemove(id: number) {
      var index = this.categoriasIds.indexOf(id);
      if (index == -1) {
          this.categoriasIds.push(id);
      } else {
          this.categoriasIds.splice(index, 1);
      }
      console.log(this.categoriasIds);
  }

  listarCategorias() {
      this.categoriaService.buscarTodasLasCategorias().subscribe((resultado) => {
          this.categorias = resultado;
      });
  }

  listarPreferencias() {
      this.categoriaService.buscarPreferenciasUsuario().subscribe((resultado) => {
          this.preferencias = resultado;
      });
  }

  listarEventosFiltrados(ordenarPorDistancia: string,
                         radio: number,
                         latitud: number,
                         longitud: number,
                         mostrarDeMiPreferencia: string,
                         categoriasSeleccionadas: Categoria[]){
    this.eventoService.filtrarEventos(ordenarPorDistancia,radio,latitud,longitud,mostrarDeMiPreferencia,categoriasSeleccionadas).subscribe((resultado)=>{
      this.eventos = resultado;
    });
  }
}
