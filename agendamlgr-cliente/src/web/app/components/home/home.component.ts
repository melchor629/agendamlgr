import { Component, OnInit } from '@angular/core';
import { CategoriaService } from '../../services/categoria.service';
import { EventoService } from '../../services/evento.service';
import { Categoria } from '../../interfaces/categoria';
import { Evento } from '../../interfaces/evento';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  eventos: Evento[];
  categorias: Categoria[];

  constructor(private categoriaService: CategoriaService,
              private eventoService: EventoService) {}

  ngOnInit() {
    this.listarCategorias();
    this.listarEventos();
  }

  listarEventos(){
    this.eventoService.buscarEventos().subscribe((resultado)=>{
      this.eventos = resultado;
    });
  }

  listarCategorias(){
    this.categoriaService.buscarTodasLasCategorias().subscribe((resultado)=>{
      this.categorias = resultado;
    });
  }
}
