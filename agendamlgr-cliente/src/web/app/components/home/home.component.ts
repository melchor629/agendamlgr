import { Component, OnInit } from '@angular/core';
import { EventoService } from '../../services/evento.service';
import { Evento } from '../../interfaces/evento';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  eventos: Evento[];

  constructor(private eventoService: EventoService) {}

  ngOnInit() {
    this.listarEventos();
  }

  listarEventos(){
    this.eventoService.buscarEventos().subscribe((resultado)=>{
      this.eventos = resultado;
    });
  }
}
