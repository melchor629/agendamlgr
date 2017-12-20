import {Component, Input, OnChanges, SimpleChanges} from "@angular/core";
import {EventoService} from "../../services/evento.service";
import {Coordenadas} from "../../interfaces/evento";

@Component({
    selector: 'mapa-desde-direccion',
    templateUrl: 'mapaDesdeDireccion.component.html'
})

export class MapaDesdeDireccionComponent implements OnChanges {

    @Input() direccion: string;

    private resultados: Coordenadas;

    constructor(private eventoService: EventoService) {
        this.resultados = {encontrado: false, latitud: 0, longitud: 0};
    }

    ngOnChanges(changes: SimpleChanges): void {
        for (let propertyName in changes) {
            /* if (propertyName === 'direccion') {
                if (this.direccion != null && this.direccion.trim().length != 0) {
                    this.eventoService.obtenerCoordenadasDesdeDireccion(this.direccion).subscribe(
                        coordenadas => {
                            this.resultados = coordenadas;
                        },
                        error => console.log(error)
                    )
                }
            } */
        }
    }

    actualizarMapa() {
        if (this.direccion != null && this.direccion.trim().length != 0) {
            this.eventoService.obtenerCoordenadasDesdeDireccion(this.direccion).subscribe(
                coordenadas => {
                    this.resultados = coordenadas;
                },
                error => console.log(error)
            )
        }
    }

}