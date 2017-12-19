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
  noPreferencias: Categoria[] = [];
  categorias: Categoria[] = [];
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
      this.categoriaService.buscarPreferenciasUsuario().subscribe(
        resultado => {
          this.preferencias = resultado;
          this.categoriaService.buscarTodasLasCategorias().subscribe(
              categorias => {
                this.categorias = categorias;
                this.noPreferencias = this.categoriasDeInteresParaInsertar;
              },
              error => this.errorResponse = error
          );
        },(errorResponse) =>{
          this.errorResponse = errorResponse;
        });
      this.eventoService.buscarEventosUsuario().subscribe(resultado => this.eventos = resultado,(errorResponse) =>{
        this.errorResponse = errorResponse;
        this.eventos = [];
      });
    }
  }

  borrar(event: Event, categoria: Categoria) {
    event.preventDefault();
    this.categoriaService.eliminarPreferenciaUsuario(categoria).subscribe(
        res => {
          if(res.deleted) {
            this.preferencias = this.preferencias.filter(value => value.id !== categoria.id);
            this.noPreferencias = this.categoriasDeInteresParaInsertar;
          }
        },
        error => this.errorResponse = error
    );
  }

  insertar(event: Event, categoria: Categoria) {
    event.preventDefault();
    this.categoriaService.añadirPreferenciaUsuario(categoria).subscribe(
        res => {
          this.preferencias = res;
          this.noPreferencias = this.categoriasDeInteresParaInsertar;
        },
        error => this.errorResponse = error
    )
  }

  get categoriasDeInteresParaInsertar(): Categoria[] {
    return this.categorias.filter(categoria => !this.preferencias.find(c => c.id == categoria.id));
  }

}
