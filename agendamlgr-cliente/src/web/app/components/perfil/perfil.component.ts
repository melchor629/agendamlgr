import { Component, OnInit } from '@angular/core';
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

  usuario: Usuario;
  preferencias: Categoria[] = [];
  eventos: Evento[] = [];

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
      this.usuarioService.buscarUsuario(userId)
          .subscribe(usuario => this.usuario = usuario, error => console.error(error));
      this.eventoService.buscarEventosUsuario(userId).subscribe(resultado => this.eventos = resultado);
    } else {
      this.usuarioService.obtenerUsuarioDeLaSesion().subscribe(resultado => this.usuario = resultado);
      this.categoriaService.buscarPreferenciasUsuario().subscribe(resultado => this.preferencias = resultado);
      this.eventoService.buscarEventosUsuario().subscribe(resultado => this.eventos = resultado);
    }
  }
}
