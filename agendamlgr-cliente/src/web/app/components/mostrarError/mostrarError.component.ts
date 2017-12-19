import { Component, Input, OnChanges, SimpleChanges } from "@angular/core";
import { HttpErrorResponse } from '@angular/common/http';
import { Error,ErrorsList } from "../../interfaces/error";
const $ = window['$'];

@Component({
    selector: 'app-mostrarError',
    templateUrl: './mostrarError.component.html'
})
export class MostrarErrorComponent implements OnChanges {

    @Input() errorResponse: HttpErrorResponse;
    errorMessage: string;
    otherErrorMessage: string;

    ngOnChanges(changes: SimpleChanges): void {
        for(let propertyName in changes) {
            if(propertyName === 'errorResponse') {
                this.errorResponse = changes[propertyName].currentValue;
                if(this.errorResponse) {
                    let uError = this.errorResponse.error;
                    if (uError !== null && uError.error) {
                        let error = <Error>this.errorResponse.error;
                        this.errorMessage = error.error.message;
                        this.otherErrorMessage = error.error.otherMessage;
                        if (error.error.error_id === ErrorsList.EXPIRADO) {
                            window.location.href = 'http://localhost:8080/agendamlgr-war/oauth/init';
                        }
                    } else {
                        this.errorMessage = "No se ha podido establecer conexión con el servidor";
                        this.otherErrorMessage = null;
                        console.log(uError);
                    }
                    $('#myModal').modal('show');
                } else {
                    this.errorMessage = this.otherErrorMessage = null;
                    $('#myModal').modal('hide');
                }
            }
        }
    }

    cerrando() {
        this.errorMessage = this.otherErrorMessage = null;
    }

}
