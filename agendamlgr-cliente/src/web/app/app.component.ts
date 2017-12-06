import { Component } from '@angular/core';
const $ = require('jquery');

@Component({
    selector: 'my-app',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent {
    constructor() {
        $('html').css('background', 'lightblue');
    }
}