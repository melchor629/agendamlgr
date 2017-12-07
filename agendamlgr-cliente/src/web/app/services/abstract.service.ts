import { HttpClient, HttpHeaders } from '@angular/common/http';
import 'rxjs/add/operator/map';

export class AbstractService{
  constructor(protected http: HttpClient){

  }
  setTokenHeader(){
    if(window.localStorage.token === null){
      return null;
    }else{
      let httpHeader = new HttpHeaders();
      let tokenHeader = httpHeader.append('bearer', window.localStorage.token);
      return tokenHeader;
    }
  }
}
