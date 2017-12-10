import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import 'rxjs/add/operator/map';
import { Observable } from "rxjs/Observable";

export class AbstractService{

  private static BASE_URL = "http://localhost:8080/agendamlgr-war/rest";

  constructor(private http: HttpClient) {}

  get<T>(seccion: string, ruta: string | number = '', opciones: { headers?: HttpHeaders, params?: HttpParams } = {}): Observable<T>  {
    return this.http.get<T>(`${AbstractService.BASE_URL}/${seccion}/${ruta}`, {
      headers: AbstractService.setTokenHeader(opciones.headers),
      params: opciones.params
    });
  }

  post<T>(seccion: string, ruta: string | number = '', objeto: any, opciones: { headers?: HttpHeaders, params?: HttpParams } = {}) {
    return this.http.post<T>(`${AbstractService.BASE_URL}/${seccion}/${ruta}`, objeto, {
      headers: AbstractService.setTokenHeader(opciones.headers),
      params: opciones.params
    });
  }

  put<T>(seccion: string, ruta: string | number = '', objeto: any, opciones: { headers?: HttpHeaders, params?: HttpParams } = {}) {
    return this.http.put<T>(`${AbstractService.BASE_URL}/${seccion}/${ruta}`, objeto, {
      headers: AbstractService.setTokenHeader(opciones.headers),
      params: opciones.params
    });
  }

  delete<T>(seccion: string, ruta: string | number = '', opciones: { headers?: HttpHeaders, params?: HttpParams } = {}) {
    return this.http.delete<T>(`${AbstractService.BASE_URL}/${seccion}/${ruta}`, {
      headers: AbstractService.setTokenHeader(opciones.headers),
      params: opciones.params
    });
  }

  private static setTokenHeader(headers?: HttpHeaders) {
    let token = window.localStorage.getItem('token');
    if(token === null || token === undefined){
      return null;
    }else{
      let httpHeader = headers ? headers : new HttpHeaders();
      return httpHeader.append('bearer', window.localStorage.token);
    }
  }
}
