import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { EventoService } from '../../services/evento.service';
import { Evento } from '../../interfaces/evento';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {

  errorResponse: HttpErrorResponse;
  eventos: Evento[];

  constructor(private eventoService: EventoService) {}

  ngOnInit() {
    this.listarEventos();
  }

  listarEventos(){
    this.eventoService.buscarEventos().subscribe((resultado)=>{
      this.eventos = resultado;
    },(errorResponse) =>{
      this.errorResponse = errorResponse;
      this.eventos = [];
    });
  }
}
