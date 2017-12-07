import { Component, OnInit } from '@angular/core';
import { CategoriaService } from '../../services/categoria.service';
import { UsuarioService } from '../../services/usuario.service';
import { EventoService } from '../../services/evento.service';
import { Categoria } from '../../interfaces/categoria';
import { Usuario } from '../../interfaces/usuario';
import { Evento } from '../../interfaces/evento';

@Component({
  selector: 'app-perfil',
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.scss']
})
export class PerfilComponent implements OnInit {

  usuario: Usuario;
  preferencias: Categoria[];
  eventos: Evento[];

  constructor(private categoriaService: CategoriaService,
              private usuarioService: UsuarioService,
              private eventoService: EventoService) {}

  ngOnInit() {
    this.listarPreferencias();
    this.listarDatosUsuarios();
    this.listarEventos();
  }

  listarDatosUsuarios(){
    this.usuarioService.obtenerUsuarioDeLaSesion().subscribe((resultado)=>{
      this.usuario = resultado;
    });
  }

  listarPreferencias(){
    this.categoriaService.buscarPreferenciasUsuario().subscribe((resultado)=>{
      this.preferencias = resultado;
    });
  }

  listarEventos(){
    this.eventoService.buscarEventosUsuario().subscribe((resultado)=>{
      console.log(resultado);
      this.eventos = resultado;
    });
  }
}
