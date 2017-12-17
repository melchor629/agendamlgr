import {Component, OnInit} from "@angular/core";
import {CategoriaService} from "../../services/categoria.service";
import {EventoService} from "../../services/evento.service";
import {UsuarioService} from "../../services/usuario.service";
import {ActivatedRoute} from "@angular/router";
import {Evento, eventoVacio} from "../../interfaces/evento";
import {Categoria} from "../../interfaces/categoria";

@Component({
    selector: 'editar-evento',
    templateUrl: './editarEvento.component.html',
    styleUrls: ['./editarEvento.component.scss']
})
export class EditarEventoComponent implements OnInit {

    private id: number;
    private evento: Evento;
    private categorias: Categoria[] = [];
    private categoriasEvento: number[] = [];
    private urlFlickr: string;
    private fecha: string;

    private flickrRegexString = '/https:\\/\\/www\\.flickr\\.com\\/photos\\/([0-9@a-zA-Z]*)\\/(albums|sets)\\/(\\d*)/g';
    private flickrRegex = new RegExp(this.flickrRegexString, 'g');

    constructor(private categoriaService: CategoriaService,
                private eventoService: EventoService,
                private usuarioService: UsuarioService,
                route: ActivatedRoute) {
        this.id = Number(route.snapshot.params['id']);
        this.evento = eventoVacio();
        this.fecha = new Date().toISOString();
    }

    ngOnInit(): void {
        this.eventoService.buscarEvento(this.id).subscribe(evento => {
            this.evento = evento;
            if(evento.flickrUserID && evento.flickrAlbumID)
                this.urlFlickr = `https://www.flickr.com/photos/${evento.flickrUserID}/albums/${evento.flickrAlbumID}`;
            evento.fecha = evento.fecha.replace("[UTC]", "");
            this.fecha = new Date(Date.parse(evento.fecha)).toISOString();
            this.categoriasEvento = evento.categoriaList.map(categoria => categoria.id);
        });
        this.categoriaService.buscarTodasLasCategorias().subscribe(categorias => this.categorias = categorias)
    }

    private onEdit(): void {
        this.evento.categoriaList = this.categoriasEvento.map(id => <Categoria>{ id, nombre: null });
        this.evento.fecha = new Date(Date.parse(this.fecha)).toISOString();
        if(this.urlFlickr) {
            //La primera vez parece que no va el regex, a si que lo apaño llamandolo una vez antes de usarlo
            // this.flickrRegex.exec(this.urlFlickr);
            let result = this.flickrRegex.exec(this.urlFlickr);
            if(result && result.length === 3) {
                this.evento.flickrAlbumID = result[2];
                this.evento.flickrUserID = result[1];
            } else {
                alert("Mu mal, la URL es incorrea probando actualizacion servjlkjer 3");
                return;
            }
        }
        this.eventoService.actualizarEvento(this.evento).subscribe(
            () => window.location.assign(`verEvento/${this.evento.id}`),
            error => alert(JSON.stringify(error.error))
        );
    }

    private onCancel(): void {
        window.history.back();
    }

}
