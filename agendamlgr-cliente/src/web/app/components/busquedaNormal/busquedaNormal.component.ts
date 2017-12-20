import { Component,OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { EventoService } from '../../services/evento.service';
import { Evento } from '../../interfaces/evento';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-busqueda',
    templateUrl: './busquedaNormal.component.html'
})
export class BusquedaNormalComponent implements OnInit {

    errorResponse: HttpErrorResponse;
    eventos: Evento[] = [];

    constructor(private eventoService: EventoService, private route: ActivatedRoute) {
        if(this.route.snapshot.params['texto']) {
            route.params.subscribe(() => {
                this.listar();
            });
        }
    }

    ngOnInit() {
        this.listar();
    }

    listar() {
        let texto = this.route.snapshot.params['texto'];
        this.eventos = null;
        this.eventoService.filtrarEventos(false,null,null,null,false,null, texto).subscribe(resultado => {
            this.eventos = resultado;
        },(errorResponse) =>{
            this.errorResponse = errorResponse;
            this.eventos = [];
        });
    }

}
