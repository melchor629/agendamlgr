import {Component, OnInit} from "@angular/core";
import {CategoriaService} from "../../services/categoria.service";
import {EventoService} from "../../services/evento.service";
import {UsuarioService} from "../../services/usuario.service";
import {Evento, eventoVacio} from "../../interfaces/evento";
import {Categoria} from "../../interfaces/categoria";
import {Error} from "../../interfaces/error";

@Component({
    selector: 'crear-evento',
    templateUrl: './crearEvento.component.html',
    styleUrls: ['./crearEvento.component.scss']
})
export class CrearEventoComponent implements OnInit {

    private id: number;
    private evento: Evento;
    private categorias: Categoria[] = [];
    private categoriasEvento: number[] = [];
    private urlFlickr: string;
    private fecha: string;

    private flickrRegexString = 'https:\\/\\/www\\.flickr\\.com\\/photos\\/([0-9@a-zA-Z]*)\\/albums\\/(\\d*)';
    private flickrRegex = new RegExp(this.flickrRegexString, 'g');

    constructor(private categoriaService: CategoriaService,
                private eventoService: EventoService,
                private usuarioService: UsuarioService) {
        this.evento = eventoVacio();
        this.fecha = new Date().toISOString();
    }

    ngOnInit(): void {
        this.categoriaService.buscarTodasLasCategorias().subscribe(categorias => this.categorias = categorias)
    }

    private onCreate(): void {
        this.evento.categoriaList = this.categoriasEvento.map(id => <Categoria>{ id, nombre: null });
        this.evento.fecha = new Date(Date.parse(this.fecha)).toISOString();
        if(this.urlFlickr) {
            //La primera vez parece que no va el regex, a si que lo apaño llamandolo una vez antes de usarlo
            this.flickrRegex.exec(this.urlFlickr);
            let result = this.flickrRegex.exec(this.urlFlickr);
            if(result && result.length === 3) {
                this.evento.flickrAlbumID = result[2];
                this.evento.flickrUserID = result[1];
            } else {
                alert("Mu mal, la URL es incorrecta");
                return;
            }
        }
        this.eventoService.crearEvento(this.evento).subscribe(
            evento => window.location.assign(`verEvento/${evento.id}`),
            error => alert(JSON.stringify(<Error>error.error))
        );
    }

    private onCancel(): void {
        window.history.back();
    }

}