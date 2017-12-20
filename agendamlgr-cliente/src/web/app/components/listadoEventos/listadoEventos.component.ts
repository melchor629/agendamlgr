import {Component, Input, OnChanges, SimpleChanges} from "@angular/core";
import {Evento} from "../../interfaces/evento";
import { LatLngBoundsLiteral } from "@agm/core";

@Component({
    selector: 'listado-eventos',
    templateUrl: 'listadoEventos.component.html'
})
export class ListadoEventosComponent implements OnChanges {

    @Input() eventos: Evento[] = [];
    @Input() eventosPorPagina = 10;
    @Input() coordedanadasCentrado: any;

    // Coordenadas en las que se centra el mapa
    private latitud: number = 0;
    private longitud: number = 0;
    private limites: LatLngBoundsLiteral;

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
            if(this.eventos) this.calcularLimitesDelMapa(this.eventos.filter(e => e.longitud != null && e.latitud != null));
        }
        else {
            // Si no se proporcionan datos, el mapa aperecera centrado
            // en la latitud y longitud medias de los eventos a listar
            if (this.eventos) {

                let eventosConCoordenadas = this.eventos.filter(e => e.longitud != null && e.latitud != null);
                let coords = eventosConCoordenadas.reduce((a, b) => {
                    a.lat += b.latitud;
                    a.long += b.longitud;
                    return a;
                }, {lat: 0, long: 0});

                this.latitud = coords.lat / eventosConCoordenadas.length;
                this.longitud = coords.long / eventosConCoordenadas.length;
                this.calcularLimitesDelMapa(eventosConCoordenadas);
            }
        }
    }

    private calcularLimitesDelMapa(eventos: Evento[]): void {
        if(eventos.length === 1) {
            this.limites = {
                east: eventos[0].longitud + 0.002,
                west: eventos[0].longitud - 0.002,
                north: eventos[0].latitud - 0.002,
                south: eventos[0].latitud + 0.002
            };
        } else {
            this.limites = {
                east: eventos.reduce((a, e) => Math.max(a, e.longitud), -180.0),
                west: eventos.reduce((a, e) => Math.min(a, e.longitud), +180.0),
                north: eventos.reduce((a, e) => Math.min(a, e.latitud), +90.0),
                south: eventos.reduce((a, e) => Math.max(a, e.latitud), -90.0)
            };
        }
    }

}