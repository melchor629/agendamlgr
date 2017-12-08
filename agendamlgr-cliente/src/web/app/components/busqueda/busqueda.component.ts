import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EventoService } from '../../services/evento.service';
import { Evento } from '../../interfaces/evento';
import { Categoria } from '../../interfaces/categoria';

@Component({
  selector: 'app-busqueda',
  templateUrl: './busqueda.component.html',
  styleUrls: ['./busqueda.component.scss']
})
export class BusquedaComponent{

  eventos: Evento[];
  palabraFiltro: string;

  constructor(private eventoService: EventoService, private route: ActivatedRoute, private router: Router) {
    route.params.subscribe(val=>{
      this.listar();
    });
  }

  listar() {
    console.log(this.route.snapshot.params['ordenarPorDistancia']);
    console.log(this.route.snapshot.params['radio']);
    console.log(this.route.snapshot.params['latitud']);
    console.log(this.route.snapshot.params['longitud']);
    console.log(this.route.snapshot.params['mostrarDeMiPreferencia']);
    console.log(this.route.snapshot.params['categoriasSeleccionadas']);
    //this.listarEventos();
    let ordenarPorDistancia = this.route.snapshot.params['ordenarPorDistancia'];
    let radio = this.route.snapshot.params['radio'];
    let latitud = this.route.snapshot.params['latitud'];
    let longitud = this.route.snapshot.params['longitud'];
    let mostrarDeMiPreferencia = this.route.snapshot.params['mostrarDeMiPreferencia'];
    let categoriasIds = this.route.snapshot.params['categoriasSeleccionadas'].split(",").map(Number);;
    var categoriasSeleccionadas = [];
    for(let i=0; i<categoriasIds.length; i++){
      let c = {
        id: categoriasIds[i],
        nombre: ''
      };
      categoriasSeleccionadas.push(c);
    }
    this.listarEventosFiltrados(ordenarPorDistancia,radio,latitud,longitud,mostrarDeMiPreferencia,categoriasSeleccionadas);
  }

  listarEventos(){
    this.eventoService.buscarEventos().subscribe((resultado)=>{
      this.eventos = resultado;
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
