import { Http, Headers } from '@angular/http';
import 'rxjs/add/operator/map';

export class AbstractService{
  constructor(protected http: Http){

  }
  setTokenHeader(){
    if(window.localStorage.token === null){
      return null;
    }else{
      let headers = new Headers();
      headers.append('bearer', window.localStorage.token);
      return headers;
    }
  }
}
