//Module imports
import { NgModule } from '@angular/core';
import { BrowserModule }  from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
//Component imports
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import { PerfilComponent } from './components/perfil/perfil.component';
import { VerEventoComponent } from './components/verEvento/verEvento.component';
import { BusquedaComponent } from './components/busqueda/busqueda.component';
import { EditarEventoComponent } from "./components/editarEvento/editarEvento.component";
import { CrearEventoComponent } from "./components/crearEvento/crearEvento.component";
//Service imports
import { CategoriaService } from './services/categoria.service';
import { UsuarioService } from './services/usuario.service';
import { EventoService } from './services/evento.service';

const appRoutes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'perfil', component: PerfilComponent},
    {path: 'verEvento/:id', component: VerEventoComponent},
    {path: 'busqueda/:ordenarPorDistancia/:radio/:latitud/:longitud/:mostrarDeMiPreferencia/:categoriasSeleccionadas', component: BusquedaComponent},
    { path: 'editarEvento/:id', component: EditarEventoComponent },
    { path: 'crearEvento', component: CrearEventoComponent }
];

@NgModule({
    imports: [
        BrowserModule,
        HttpClientModule,
        FormsModule,
        RouterModule.forRoot(appRoutes)
    ],
    declarations: [
        AppComponent,
        HomeComponent,
        PerfilComponent,
        VerEventoComponent,
        BusquedaComponent,
        EditarEventoComponent,
        CrearEventoComponent
    ],
    providers: [
        CategoriaService,
        UsuarioService,
        EventoService
    ],
    bootstrap: [ AppComponent ]
})
export class AppModule { }
