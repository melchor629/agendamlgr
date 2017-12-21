// Angular
import '@angular/platform-browser';
import '@angular/platform-browser-dynamic';
import '@angular/core';
import '@angular/common';
import '@angular/router';
import '@angular/platform-browser/animations';

// RxJS
import 'rxjs';

// Bootstrap, jQuery & popper
window['$'] = window['jQuery'] = require('jquery');
window['Popper'] = require('popper.js');
require('bootstrap');
require('commonmark');

// Extras
import '@agm/core';
import '@agm/snazzy-info-window';
import 'ng-pick-datetime';

// Base scss
import './assets/scss/base.scss';
import './assets/img/ic_my_location_black_24px.svg';