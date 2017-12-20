import {Component, Input, OnChanges, OnInit, SimpleChanges} from "@angular/core";
import {Evento} from "../../interfaces/evento";

@Component({
    selector: 'listado-eventos',
    templateUrl: 'listadoEventos.component.html'
})
export class ListadoEventosComponent implements OnChanges {

    @Input() eventos: Evento[] = [];
    @Input() eventosPorPagina = 10;
    @Input() coordedanadasCentrado: any;

    // Coordenadas en las que se centra el mapa
    private latitud: Number = 0;
    private longitud: Number = 0;

    private page = 0;
    private totalPages = 1;


    ngOnChanges(changes: SimpleChanges): void {
        for (let propertyName in changes) {
            if (propertyName === 'eventos') {
                this.page = 0;

                this.totalPages = this.eventos ? Math.trunc(this.eventos.length / 10) + +(this.eventos.length % 10 !== 0) : 1;
                this.actualizarCoordenadasCentrado();
            }

            if (propertyName === 'coordedanadasCentrado') {
                this.actualizarCoordenadasCentrado();
            }
        }
    }

    private goToPage(page: number, event: Event): void {
        event.preventDefault();
        this.page = page;
    }

    private goToPreviousPage(event: Event): void {
        event.preventDefault();
        this.page--;
    }

    private goToNextPage(event: Event): void {
        event.preventDefault();
        this.page++;
    }

    private range(initial: number, final: number, step: number = 1) {
        let r = [];
        for (let i = initial; i < final; i += step) {
            r.push(i);
        }
        return r;
    }

    private actualizarCoordenadasCentrado(): void {
        if (this.coordedanadasCentrado) {
            this.latitud = this.coordedanadasCentrado.latitude;
            this.longitud = this.coordedanadasCentrado.longitude;
        }
        else {
            // Si no se proporcionan datos, el mapa aperecera centrado
            // en la latitud y longitud medias de los eventos a listar
            if (this.eventos) {

                let eventosCalculados: number = 0;
                let coords = this.eventos.reduce((a, b) => {
                    if (b.longitud != null && b.latitud != null) {
                        a.lat += b.latitud as number;
                        a.long += b.longitud as number;
                        eventosCalculados++;
                    }
                    return a;
                }, {lat: 0, long: 0});

                this.latitud = coords.lat / eventosCalculados;
                this.longitud = coords.long / eventosCalculados;
            }
        }
    }

}