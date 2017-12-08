import { Component, OnInit } from '@angular/core';
import { ActivatedRoute,Router } from '@angular/router';
import { CategoriaService } from './services/categoria.service';
import { Categoria } from './interfaces/categoria';
@Component({
    selector: 'my-app',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit{

  categorias: Categoria[];
  preferencias: Categoria[];

  ordenarPorDistancia: string;
  radio: number;
  latitud: number;
  longitud: number;
  mostrarDeMiPreferencia: string;
  categoriasSeleccionadas: number[];

  constructor(private categoriaService: CategoriaService, private router: Router) {
    let token = window.location.search.substr(1).split("&").map(e => e.split("=")).reduce((x, e) => { x[e[0]] = e[1]; return x; }, {})["token"];
    if(token){
      window.localStorage.setItem('token',token);
    }
    this.listarCategorias();
    this.listarPreferencias();
  }

  ngOnInit(){
    this.ordenarPorDistancia = 'false';
    this.radio = 0;
    this.latitud = 0;
    this.longitud = 0;
    this.mostrarDeMiPreferencia = 'false';
    this.categoriasSeleccionadas = [3];
  }

  buscar(){
    this.router.navigateByUrl('busqueda/'+this.ordenarPorDistancia+"/"+
                                          this.radio+"/"+
                                          this.latitud+"/"+
                                          this.longitud+"/"+
                                          this.mostrarDeMiPreferencia+"/"+
                                          this.categoriasSeleccionadas);
  }

  listarCategorias(){
    this.categoriaService.buscarTodasLasCategorias().subscribe((resultado)=>{
      this.categorias = resultado;
    });
  }
  listarPreferencias(){
    this.categoriaService.buscarPreferenciasUsuario().subscribe((resultado)=>{
      this.preferencias = resultado;
    });
  }
}
