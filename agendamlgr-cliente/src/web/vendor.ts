// Angular
import '@angular/platform-browser';
import '@angular/platform-browser-dynamic';
import '@angular/core';
import '@angular/common';
import '@angular/router';

// RxJS
import 'rxjs';

// Bootstrap, jQuery & popper
window['$'] = window['jQuery'] = require('jquery');
window['Popper'] = require('popper.js');
require('bootstrap');
require('commonmark');

// Base scss
import './assets/scss/base.scss';