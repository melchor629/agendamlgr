import { HttpClient, HttpHeaders } from '@angular/common/http';
import 'rxjs/add/operator/map';

export class AbstractService{
  constructor(protected http: HttpClient){

  }
  setTokenHeader(){
    let token = window.localStorage.getItem('token');
    if(token === null || token === undefined){
      return null;
    }else{
      let httpHeader = new HttpHeaders();
      return httpHeader.append('bearer', window.localStorage.token);
    }
  }
}
