import {Component, OnInit} from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import {CategoriaService} from './services/categoria.service';

@Component({
    selector: 'my-app',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

    userLoggedIn: boolean;
    private routerUrl: string;
    private textoBuscar: string = "";

    constructor(private categoriaService: CategoriaService, private router: Router) {
        let token = window.location.search.substr(1).split("&").map(e => e.split("=")).reduce((x, e) => {
            x[e[0]] = e[1];
            return x;
        }, {})["token"];
        if (token) {
            window.localStorage.setItem('token', token);
        }
    }

    ngOnInit() {
        this.userLoggedIn = Boolean(window.localStorage.getItem('token'));
        this.router.events.subscribe(e => {
            if(e instanceof NavigationEnd) {
                this.routerUrl = (<NavigationEnd>e).urlAfterRedirects;
                if(this.routerUrl.startsWith("/nbuscar/")) {
                    this.textoBuscar = decodeURIComponent(this.routerUrl.substr(9));
                }
            }
        });
    }

    logout(event: Event) {
        event.preventDefault();
        window.localStorage.removeItem("token");
        window.location.replace("/agendamlgr");
    }

    buscar() {
        this.router.navigateByUrl(`/nbuscar/${encodeURIComponent(this.textoBuscar)}`);
    }
}
