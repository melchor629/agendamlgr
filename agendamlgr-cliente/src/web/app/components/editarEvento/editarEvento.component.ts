import {Component, OnInit} from "@angular/core";
import { HttpErrorResponse } from '@angular/common/http';
import {CategoriaService} from "../../services/categoria.service";
import {EventoService} from "../../services/evento.service";
import {UsuarioService} from "../../services/usuario.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Evento, eventoVacio} from "../../interfaces/evento";
import {Categoria} from "../../interfaces/categoria";

@Component({
    selector: 'editar-evento',
    templateUrl: './editarEvento.component.html',
    styleUrls: ['./editarEvento.component.scss']
})
export class EditarEventoComponent implements OnInit {

    errorResponse: HttpErrorResponse;
    private id: number;
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
                private router : Router,
                route: ActivatedRoute) {
        this.id = Number(route.snapshot.params['id']);
        this.evento = eventoVacio();
        this.fecha = new Date();
    }

    ngOnInit(): void {
        this.eventoService.buscarEvento(this.id).subscribe(evento => {
            this.evento = evento;
            if (evento.flickrUserID && evento.flickrAlbumID)
                this.urlFlickr = `https://www.flickr.com/photos/${evento.flickrUserID}/albums/${evento.flickrAlbumID}`;
            evento.fecha = evento.fecha.replace("Z[UTC]", "");
            this.fecha = new Date(evento.fecha);
            this.categoriasEvento = evento.categoriaList.map(categoria => categoria.id);
        },(errorResponse) =>{
          this.errorResponse = errorResponse;
        });
        this.categoriaService.buscarTodasLasCategorias().subscribe(categorias => this.categorias = categorias,(errorResponse) =>{
          this.errorResponse = errorResponse;
        });
    }

    private onEdit(): void {
        // Resetear el elemento LastIndex del regex para que se vuelva a ejecutar
        this.flickrRegex.lastIndex = 0;

        this.evento.categoriaList = this.categoriasEvento.map(id => <Categoria>{id, nombre: null});
        this.evento.fecha = this.fecha.toISOString();
        if (this.urlFlickr) {
            let result = this.flickrRegex.exec(this.urlFlickr);
            if (result && result.length === 3) {
                this.evento.flickrAlbumID = result[2];
                this.evento.flickrUserID = result[1];
            } else {
                alert("URL de flickr incorrecta...");
                return;
            }
        }
        this.eventoService.actualizarEvento(this.evento).subscribe(
            () => this.router.navigateByUrl(`/verEvento/${this.evento.id}`),
            (errorResponse) =>{
              this.errorResponse = errorResponse;
            });
    }

    private onCancel(): void {
        window.history.back();
    }
}
