//Module imports
import { NgModule } from '@angular/core';
import { BrowserModule }  from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { RouterModule, Routes } from '@angular/router';
//Component imports
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import { PerfilComponent } from './components/perfil/perfil.component';
//Service imports
import { CategoriaService } from './services/categoria.service';
import { UsuarioService } from './services/usuario.service';

const appRoutes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'perfil', component: PerfilComponent}
]

@NgModule({
    imports: [
        BrowserModule,
        HttpModule,
        RouterModule.forRoot(appRoutes)
    ],
    declarations: [
        AppComponent,
        HomeComponent,
        PerfilComponent
    ],
    providers: [
        CategoriaService,
        UsuarioService
    ],
    bootstrap: [ AppComponent ]
})
export class AppModule { }
