import { Component,OnInit } from '@angular/core';
import { EventoService } from '../../services/evento.service';
import { CategoriaService } from '../../services/categoria.service';
import { Evento } from '../../interfaces/evento';
import { Categoria } from '../../interfaces/categoria';
import { ActivatedRoute, Router } from '@angular/router';

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

  constructor(private eventoService: EventoService, private categoriaService: CategoriaService, private route: ActivatedRoute, private router: Router) {
    if(this.route.snapshot.params['categoriasSeleccionadas']){
      route.params.subscribe(val=>{
        this.listar();
      });
    }
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

  listar() {
    console.log(this.route.snapshot.params['categoriasSeleccionadas']);
    let categoriasIds = this.route.snapshot.params['categoriasSeleccionadas'].split(",").map(Number);;
    var categoriasSeleccionadas = [];
    for(let i=0; i<categoriasIds.length; i++){
      let c = {
        id: categoriasIds[i],
        nombre: ''
      };
      categoriasSeleccionadas.push(c);
    }
    this.listarEventosFiltrados('false',0,0,0,'false',categoriasSeleccionadas);
  }

  buscar() {
      if (this.mostrarDeMiPreferencia) {
        for (let preferencia of this.preferencias) {
          this.categoriasIds.push(preferencia.id);
        }
      }
      var categoriasSet = new Set<number>();
      for(let categoriaId of this.categoriasIds){
        categoriasSet.add(categoriaId);
      }
      this.categoriasIds = [];
      for(let categoriaId in categoriasSet){
        this.categoriasIds.push(+categoriaId);
      }
      console.log(this.categoriasIds);
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
