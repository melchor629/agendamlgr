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
                this.totalPages = this.eventos ? Math.round(this.eventos.length / 10) : 1;
            }
        }
    }

    private goToPage(page: number): void {
        this.page = page;
    }

    private goToPreviousPage(): void {
        this.page--;
    }

    private goToNextPage(): void {
        this.page++;
    }

    private range(initial: number, final: number, step: number = 1) {
        let r = [];
        for(let i = initial; i < final; i += step) {
            r.push(i);
        }
        return r;
    }

}