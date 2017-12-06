import { Component } from '@angular/core';
const $ = require('jquery');

@Component({
    selector: 'my-app',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent {
    constructor() {
      var token = window.location.search.substr(1).split("&").map(e => e.split("=")).reduce((x, e) => { x[e[0]] = e[1]; return x; }, {})["token"];
      if(token){
        window.localStorage.setItem('token',token);
      }
    }
}
