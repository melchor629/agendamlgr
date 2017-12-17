import { Component, Input, OnChanges, SimpleChanges } from "@angular/core";
import { Evento } from "../../interfaces/evento";

@Component({
    selector: 'listado-eventos',
    templateUrl: 'listadoEventos.component.html'
})
export class ListadoEventosComponent implements OnChanges {

    @Input() eventos: Evento[] = [];
    @Input() eventosPorPagina = 10;
    private page = 0;
    private totalPages = 1;

    ngOnChanges(changes: SimpleChanges): void {
        for(let propertyName in changes) {
            if(propertyName === 'eventos') {
                this.page = 0;
                this.totalPages = Math.round(this.eventos.length / 10);
            }
        }
    }

}