import { Component, OnInit } from '@angular/core';
import { CategoriaService } from '../../services/categoria.service';
import { UsuarioService } from '../../services/usuario.service';
import { Categoria } from '../../interfaces/categoria';
import { Usuario } from '../../interfaces/usuario';

@Component({
  selector: 'app-perfil',
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.scss']
})
export class PerfilComponent implements OnInit {

  preferencias: Categoria[];
  usuario: Usuario;

  constructor(private categoriaService: CategoriaService, private usuarioService: UsuarioService) {
  }

  ngOnInit() {
    this.listarPreferencias();
    this.listarDatosUsuarios();
  }

  listarDatosUsuarios(){
    this.usuarioService.obtenerUsuarioDeLaSesion().subscribe((resultado)=>{
      console.log(resultado);
      this.usuario = resultado;
    });
  }

  listarPreferencias(){
    this.categoriaService.buscarPreferenciasUsuario().subscribe((resultado)=>{
      console.log(resultado);
      this.preferencias = resultado;
    });
  }
}
