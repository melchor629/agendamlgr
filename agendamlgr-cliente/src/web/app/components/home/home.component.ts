import { Component, OnInit } from '@angular/core';
import { CategoriaService } from '../../services/categoria.service';
import { Categoria } from '../../interfaces/categoria';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  categorias: Categoria[];

  constructor(private categoriaService: CategoriaService) {
  }

  ngOnInit() {
    this.listarCategorias();
  }

  listarCategorias(){
    this.categoriaService.buscarCategorias().subscribe((resultado)=>{
      console.log(resultado);
      this.categorias = resultado;
    });
  }
}
