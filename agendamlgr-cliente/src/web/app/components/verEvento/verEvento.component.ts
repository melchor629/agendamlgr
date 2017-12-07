import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CategoriaService } from '../../services/categoria.service';
import { EventoService } from '../../services/evento.service';
import { UsuarioService } from '../../services/usuario.service';
import { Categoria } from '../../interfaces/categoria';
import { Evento } from '../../interfaces/evento';
import { Usuario } from '../../interfaces/usuario';
import { FotosDeEvento } from '../../interfaces/fotosDeEvento';
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

  constructor(private categoriaService: CategoriaService,
              private eventoService: EventoService,
              private usuarioService: UsuarioService,
              route: ActivatedRoute) {
                this.id = Number(route.snapshot.params['id']);
              }

  ngOnInit(){
    this.eventoService.buscarEvento(this.id).subscribe((resultado)=>{
      this.evento = resultado;
      this.usuarioService.buscarUsuario(this.evento.creador).subscribe((resultado2)=>{
          this.nombreCreador = resultado2.nombre;
      });
      this.eventoService.buscarFotosParaEvento(this.evento.id).subscribe((resultado3)=>{
          this.fotosDeEvento = resultado3;
          this.fotos = this.fotosDeEvento.fotos;
      });
    });
  }

}
