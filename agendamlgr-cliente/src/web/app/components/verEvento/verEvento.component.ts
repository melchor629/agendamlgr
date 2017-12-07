import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CategoriaService } from '../../services/categoria.service';
import { EventoService } from '../../services/evento.service';
import { Categoria } from '../../interfaces/categoria';
import { Evento } from '../../interfaces/evento';

@Component({
  selector: 'app-home',
  templateUrl: './verEvento.component.html',
  styleUrls: ['./verEvento.component.scss']
})
export class VerEventoComponent implements OnInit {

  id: number;
  evento: Evento;

  constructor(private categoriaService: CategoriaService,
              private eventoService: EventoService,
              route: ActivatedRoute) {
                this.id = Number(route.snapshot.params['id']);
              }

  ngOnInit(){
    this.eventoService.buscarEvento(this.id).subscribe((resultado)=>{
      this.evento = resultado;
    });
  }

}
