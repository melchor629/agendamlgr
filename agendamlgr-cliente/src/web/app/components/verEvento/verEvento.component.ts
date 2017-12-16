import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CategoriaService } from '../../services/categoria.service';
import { EventoService } from '../../services/evento.service';
import { UsuarioService } from '../../services/usuario.service';
import {Evento, eventoVacio} from '../../interfaces/evento';
import {FotosDeEvento, fotosDeEventoVacio} from '../../interfaces/fotosDeEvento';
import { Foto } from '../../interfaces/foto';

@Component({
  selector: 'app-home',
  templateUrl: './verEvento.component.html',
  styleUrls: ['./verEvento.component.scss']
})
export class VerEventoComponent implements OnInit {

  id: number;
  evento: Evento;
  nombreCreador: string;
  fotosDeEvento: FotosDeEvento;
  fotos: Foto[];
  private fechaAmigable: string;

  constructor(private categoriaService: CategoriaService,
              private eventoService: EventoService,
              private usuarioService: UsuarioService,
              route: ActivatedRoute) {
                this.id = Number(route.snapshot.params['id']);
                this.evento = eventoVacio();
                this.fotosDeEvento = fotosDeEventoVacio();
              }

  ngOnInit(){
    this.eventoService.buscarEvento(this.id).subscribe((resultado)=>{
      this.evento = resultado;
      this.evento.fecha = this.evento.fecha.replace("[UTC]", "");
      this.fechaAmigable = new Date(Date.parse(this.evento.fecha)).toLocaleString();
      this.usuarioService.buscarUsuario(this.evento.creador).subscribe((resultado2)=>{
          this.nombreCreador = resultado2.nombre;
      });
      this.eventoService.buscarFotosParaEvento(this.evento.id).subscribe((resultado3)=>{
          this.fotosDeEvento = resultado3;
          this.fotos = this.fotosDeEvento.fotos;
      });
    });
  }

  validar(){
    this.eventoService.validarEvento(this.id).subscribe((resultado)=>{
      console.log(resultado);
    });
  }

}
