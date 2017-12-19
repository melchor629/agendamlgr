import { Component, Input, OnChanges, SimpleChanges } from "@angular/core";
import { HttpErrorResponse } from '@angular/common/http';
import { Error,ErrorsList } from "../../interfaces/error";

@Component({
    selector: 'app-mostrarError',
    templateUrl: './mostrarError.component.html'
})
export class MostrarErrorComponent implements OnChanges {

    @Input() errorResponse: HttpErrorResponse;
    error: Error = {} as Error;

    ngOnChanges(changes: SimpleChanges): void {
      for(let propertyName in changes) {
          if(propertyName === 'errorResponse') {
              this.errorResponse = changes[propertyName].currentValue;
              this.error = <Error>this.errorResponse.error;
              if(this.error!=null && this.error.error.error_id== ErrorsList.EXPIRADO){
                window.location.href = 'http://localhost:8080/agendamlgr-war/oauth/init';
              }
          }
      }
    }

}
