import { Component,OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { EventoService } from '../../services/evento.service';
import { CategoriaService } from '../../services/categoria.service';
import { Evento } from '../../interfaces/evento';
import { Categoria } from '../../interfaces/categoria';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-busqueda',
  templateUrl: './busqueda.component.html',
  styleUrls: ['./busqueda.component.scss']
})
export class BusquedaComponent implements OnInit {

  errorResponse: HttpErrorResponse = new HttpErrorResponse({});
  categorias: Categoria[];
  preferencias: Categoria[];
  ordenarPorDistancia: boolean = false;
  radio: number = 0.1;
  latitud: number = null;
  longitud: number = null;
  mostrarDeMiPreferencia: boolean = false;
  categoriasIds: number[] = [];
  eventos: Evento[] = [];
  palabraFiltro: string;

  constructor(private eventoService: EventoService, private categoriaService: CategoriaService, private route: ActivatedRoute) {
    if(this.route.snapshot.params['categoriasSeleccionadas']){
      route.params.subscribe(() => {
        this.listar();
      });
    }
  }

  ngOnInit() {
    this.listarCategorias();
    this.listarPreferencias();
  }

  listar() {
    this.route.snapshot.params['categoriasSeleccionadas'].split(",").map(Number).forEach((c: number) => this.categoriasIds.push(c));
    let categoriasSeleccionadas = [];
    for(let i=0; i<this.categoriasIds.length; i++){
      let c = {
        id: this.categoriasIds[i],
        nombre: ''
      };
      categoriasSeleccionadas.push(c);
    }
    this.listarEventosFiltrados(false,0,0,0,false,categoriasSeleccionadas);
  }

  buscar() {
      let categoriasSeleccionadas = [];
      for(let i=0; i<this.categoriasIds.length; i++){
        let c = {
          id: this.categoriasIds[i],
          nombre: ''
        };
        categoriasSeleccionadas.push(c);
      }
      this.listarEventosFiltrados(this.ordenarPorDistancia,this.radio,this.latitud,this.longitud,this.mostrarDeMiPreferencia,categoriasSeleccionadas);
  }

  listarCategorias() {
      this.categoriaService.buscarTodasLasCategorias().subscribe((resultado) => {
          this.categorias = resultado;
      },(errorResponse) =>{
        this.errorResponse = errorResponse;
      });
  }

  listarPreferencias() {
      this.categoriaService.buscarPreferenciasUsuario().subscribe((resultado) => {
          this.preferencias = resultado;
      },(errorResponse) =>{
        this.errorResponse = errorResponse;
      });
  }

  listarEventosFiltrados(ordenarPorDistancia: boolean,
                         radio: number,
                         latitud: number,
                         longitud: number,
                         mostrarDeMiPreferencia: boolean,
                         categoriasSeleccionadas: Categoria[]){
    this.eventos = null;
    this.eventoService.filtrarEventos(ordenarPorDistancia,radio,latitud,longitud,mostrarDeMiPreferencia,categoriasSeleccionadas).subscribe((resultado)=>{
      this.eventos = resultado;
    },(errorResponse) =>{
      this.errorResponse = errorResponse;
    });
  }

  comprobarPermisosUbicacion(): void {
    if(this.ordenarPorDistancia) {
      //TODO Comprobar el tema de la posición
    }
  }

}
