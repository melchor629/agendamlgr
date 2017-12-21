//Module imports
import { NgModule } from '@angular/core';
import { BrowserModule }  from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AgmCoreModule } from '@agm/core';
import { AgmSnazzyInfoWindowModule } from '@agm/snazzy-info-window';
import { DateTimePickerModule } from 'ng-pick-datetime';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
//Component imports
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import { PerfilComponent } from './components/perfil/perfil.component';
import { VerEventoComponent } from './components/verEvento/verEvento.component';
import { BusquedaComponent } from './components/busqueda/busqueda.component';
import { EditarEventoComponent } from "./components/editarEvento/editarEvento.component";
import { CrearEventoComponent } from "./components/crearEvento/crearEvento.component";
import { ListadoEventosComponent } from "./components/listadoEventos/listadoEventos.component";
import { MostrarErrorComponent } from "./components/mostrarError/mostrarError.component";
import { BusquedaNormalComponent } from "./components/busquedaNormal/busquedaNormal.component";
import { MapaDesdeDireccionComponent } from "./components/mapaDesdeDireccion/mapaDesdeDireccion.component";
//Service imports
import { CategoriaService } from './services/categoria.service';
import { UsuarioService } from './services/usuario.service';
import { EventoService } from './services/evento.service';
//Pipe imports
import { ParseDateStringPipe } from "./pipes/parseDateString.pipe";
import { MarkdownPipe } from "./pipes/markdown.pipe";

import localeEs from '@angular/common/locales/es';
import { registerLocaleData } from '@angular/common';

const apiKey = require('./services/tokens.json');

const appRoutes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'perfil', component: PerfilComponent},
    { path: 'perfil/:id', component: PerfilComponent },
    {path: 'verEvento/:id', component: VerEventoComponent},
    { path: 'buscar/:categoriasSeleccionadas', component: BusquedaComponent },
    { path: 'buscar', component: BusquedaComponent },
    { path: 'nbuscar/:texto', component: BusquedaNormalComponent },
    { path: 'editarEvento/:id', component: EditarEventoComponent },
    { path: 'crearEvento', component: CrearEventoComponent }
];

@NgModule({
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        HttpClientModule,
        FormsModule,
        RouterModule.forRoot(appRoutes),
        AgmCoreModule.forRoot({
            apiKey: apiKey.google_api_key
        }),
        AgmSnazzyInfoWindowModule,
        DateTimePickerModule
    ],
    declarations: [
        AppComponent,
        HomeComponent,
        PerfilComponent,
        VerEventoComponent,
        BusquedaComponent,
        EditarEventoComponent,
        CrearEventoComponent,
        ListadoEventosComponent,
        MostrarErrorComponent,
        BusquedaNormalComponent,
        ParseDateStringPipe,
        MarkdownPipe,
        MapaDesdeDireccionComponent
    ],
    providers: [
        CategoriaService,
        UsuarioService,
        EventoService
    ],
    bootstrap: [ AppComponent ]
})
export class AppModule { }

registerLocaleData(localeEs, 'es');
