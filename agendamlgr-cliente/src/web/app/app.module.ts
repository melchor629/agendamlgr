//Module imports
import { NgModule } from '@angular/core';
import { BrowserModule }  from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';
//Component imports
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import { PerfilComponent } from './components/perfil/perfil.component';
import { VerEventoComponent } from './components/verEvento/verEvento.component';
//Service imports
import { CategoriaService } from './services/categoria.service';
import { UsuarioService } from './services/usuario.service';
import { EventoService } from './services/evento.service';

const appRoutes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'perfil', component: PerfilComponent},
  {path: 'verEvento/:id', component: VerEventoComponent}
]

@NgModule({
    imports: [
        BrowserModule,
        HttpClientModule,
        RouterModule.forRoot(appRoutes)
    ],
    declarations: [
        AppComponent,
        HomeComponent,
        PerfilComponent,
        VerEventoComponent
    ],
    providers: [
        CategoriaService,
        UsuarioService,
        EventoService
    ],
    bootstrap: [ AppComponent ]
})
export class AppModule { }
