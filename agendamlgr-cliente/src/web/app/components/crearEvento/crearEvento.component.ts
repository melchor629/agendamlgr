import {Component, OnInit} from "@angular/core";
import { HttpErrorResponse } from '@angular/common/http';
import {CategoriaService} from "../../services/categoria.service";
import {EventoService} from "../../services/evento.service";
import {UsuarioService} from "../../services/usuario.service";
import {Evento, eventoVacio} from "../../interfaces/evento";
import {Categoria} from "../../interfaces/categoria";
import {Router} from "@angular/router";

@Component({
    selector: 'crear-evento',
    templateUrl: './crearEvento.component.html',
    styleUrls: ['./crearEvento.component.scss']
})
export class CrearEventoComponent implements OnInit {

    errorResponse: HttpErrorResponse;
    private evento: Evento;
    private categorias: Categoria[] = [];
    private categoriasEvento: number[] = [];
    private urlFlickr: string;
    private fecha: Date;

    private flickrRegexString = 'https://www\\.flickr\\.com/photos/([0-9@a-zA-Z]*)/(?:albums|sets)/(\\d*)';
    private flickrRegex = new RegExp(this.flickrRegexString, 'g');

    constructor(private categoriaService: CategoriaService,
                private eventoService: EventoService,
                private usuarioService: UsuarioService,
                private router: Router) {
        this.evento = eventoVacio();
        this.fecha = new Date();
    }

    ngOnInit(): void {
        this.categoriaService.buscarTodasLasCategorias().subscribe(categorias => this.categorias = categorias,(errorResponse) =>{
          this.errorResponse = errorResponse;
        });
    }

    private onCreate(): void {
        this.evento.categoriaList = this.categoriasEvento.map(id => <Categoria>{ id, nombre: null });
        this.evento.fecha = this.fecha.toISOString();
        if(this.urlFlickr) {
            let result = this.flickrRegex.exec(this.urlFlickr);
            if(result && result.length === 3) {
                this.evento.flickrAlbumID = result[2];
                this.evento.flickrUserID = result[1];
            } else {
                alert("URL de flickr incorrecta...");
                return;
            }
        }
        this.eventoService.crearEvento(this.evento).subscribe(
            evento => this.router.navigateByUrl(`/verEvento/${evento.id}`),(errorResponse) =>{
              this.errorResponse = errorResponse;
            });
    }

    private onCancel(): void {
        window.history.back();
    }

}
