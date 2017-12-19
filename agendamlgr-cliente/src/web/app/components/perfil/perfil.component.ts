import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { CategoriaService } from '../../services/categoria.service';
import { UsuarioService } from '../../services/usuario.service';
import { EventoService } from '../../services/evento.service';
import { Categoria } from '../../interfaces/categoria';
import { Usuario } from '../../interfaces/usuario';
import { Evento } from '../../interfaces/evento';
import { ActivatedRoute } from "@angular/router";

@Component({
  selector: 'app-perfil',
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.scss']
})
export class PerfilComponent implements OnInit {

  errorResponse: HttpErrorResponse;
  usuario: Usuario;
  preferencias: Categoria[] = [];
  eventos: Evento[];

  constructor(private categoriaService: CategoriaService,
              private usuarioService: UsuarioService,
              private eventoService: EventoService,
              private route: ActivatedRoute) {}

  ngOnInit() {
    this.listarDatosUsuarios();
  }

  listarDatosUsuarios(){
    let userId = this.route.snapshot.params['id'];
    if(userId) {
      this.usuarioService.buscarUsuario(userId).subscribe(usuario => this.usuario = usuario,(errorResponse) =>{
        this.errorResponse = errorResponse;
      });
      this.eventoService.buscarEventosUsuario(userId).subscribe(resultado => this.eventos = resultado,(errorResponse) =>{
        this.errorResponse = errorResponse;
        this.eventos = [];
      });
    } else {
      this.usuarioService.obtenerUsuarioDeLaSesion().subscribe(resultado => this.usuario = resultado,(errorResponse) =>{
        this.errorResponse = errorResponse;
      });
      this.categoriaService.buscarPreferenciasUsuario().subscribe(resultado => this.preferencias = resultado,(errorResponse) =>{
        this.errorResponse = errorResponse;
      });
      this.eventoService.buscarEventosUsuario().subscribe(resultado => this.eventos = resultado,(errorResponse) =>{
        this.errorResponse = errorResponse;
        this.eventos = [];
      });
    }
  }
}
