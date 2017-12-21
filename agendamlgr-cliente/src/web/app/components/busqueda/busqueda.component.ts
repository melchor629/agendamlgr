import { Component,OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { EventoService } from '../../services/evento.service';
import { CategoriaService } from '../../services/categoria.service';
import { Evento } from '../../interfaces/evento';
import { Categoria } from '../../interfaces/categoria';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-busqueda',
    templateUrl: './busqueda.component.html',
    styleUrls: ['./busqueda.component.scss']
})
export class BusquedaComponent implements OnInit {

    errorResponse: HttpErrorResponse;
    categorias: Categoria[];
    preferencias: Categoria[];
    ordenarPorDistancia: boolean = false;
    radio: number = 0.1;
    latitud: number = null;
    longitud: number = null;
    mostrarDeMiPreferencia: boolean = false;
    categoriasIds: number[] = [];
    eventos: Evento[] = [];
    geolocalizando: boolean = false;
    texto: string = "";
    coordenadasGeolocalizacion: any = null;

    constructor(private eventoService: EventoService, private categoriaService: CategoriaService, private route: ActivatedRoute) {
        if(this.route.snapshot.params['categoriasSeleccionadas']){
            route.params.subscribe(() => {
                this.listar();
            });
        }
    }

    ngOnInit() {
        this.listarCategorias();
        this.listarPreferencias();
    }

    listar() {
        this.route.snapshot.params['categoriasSeleccionadas'].split(",").map(Number).forEach((c: number) => this.categoriasIds.push(c));
        let categoriasSeleccionadas = [];
        for(let i=0; i<this.categoriasIds.length; i++){
            let c = {
                id: this.categoriasIds[i],
                nombre: ''
            };
            categoriasSeleccionadas.push(c);
        }
        this.listarEventosFiltrados(false,0,0,0,false,categoriasSeleccionadas);
    }

    buscar() {
        let categoriasSeleccionadas = [];
        for(let i=0; i<this.categoriasIds.length; i++){
            let c = {
                id: this.categoriasIds[i],
                nombre: ''
            };
            categoriasSeleccionadas.push(c);
        }
        this.listarEventosFiltrados(this.ordenarPorDistancia,this.radio,this.latitud,this.longitud,this.mostrarDeMiPreferencia,categoriasSeleccionadas,this.texto);
    }

    listarCategorias() {
        this.categoriaService.buscarTodasLasCategorias().subscribe((resultado) => {
            this.categorias = resultado;
        }, (errorResponse) => {
            this.errorResponse = errorResponse;
        });
    }

    listarPreferencias() {
        if(localStorage.getItem('token')) {
            this.categoriaService.buscarPreferenciasUsuario().subscribe((resultado) => {
                this.preferencias = resultado;
            }, (errorResponse) => {
                this.errorResponse = errorResponse;
            });
        }
    }

    listarEventosFiltrados(ordenarPorDistancia: boolean,
                           radio: number,
                           latitud: number,
                           longitud: number,
                           mostrarDeMiPreferencia: boolean,
                           categoriasSeleccionadas: Categoria[],
                           texto?: string){
        this.eventos = null;
        this.eventoService.filtrarEventos(ordenarPorDistancia,radio,latitud,longitud,mostrarDeMiPreferencia,categoriasSeleccionadas, texto).subscribe((resultado)=>{
            this.eventos = resultado;
        },(errorResponse) =>{
            this.errorResponse = errorResponse;
            this.eventos = [];
        });
    }

    geolocalizacion() {
        this.coordenadasGeolocalizacion = null;
        if(this.ordenarPorDistancia) {
            if(navigator.geolocation) {
                this.geolocalizando = true;
                navigator.geolocation.getCurrentPosition(
                    position => {
                        this.latitud = position.coords.latitude;
                        this.longitud = position.coords.longitude;
                        this.coordenadasGeolocalizacion = position.coords;
                        this.geolocalizando = false;
                    },
                    error => {
                        if(error.code === error.PERMISSION_DENIED) {
                            this.mostrarError('Has denegado el acceso para que obtengamos tu posición');
                        } else if(error.code === error.POSITION_UNAVAILABLE) {
                            this.mostrarError(
                                'No hemos podido obtener tu posición geográfica',
                                error.message
                            );
                        } else if(error.code === error.TIMEOUT) {
                            this.mostrarError(
                                'Ha pasado tanto tiempo obteniendo tu posición, que hemos decidido no seguir esperando...',
                                error.message
                            );
                        }
                        this.ordenarPorDistancia = false;
                        this.geolocalizando = false;
                    },
                    { enableHighAccuracy: true }
                );
            } else {
                this.mostrarError('Tu navegador no soporta geolocalización');
                this.ordenarPorDistancia = false;
                this.geolocalizando = false;
            }
        } else {
            this.latitud = null;
            this.longitud = null;
        }
    }

    private mostrarError(mensaje: string, otroMensaje?: string) {
        this.errorResponse = new HttpErrorResponse({
            error: {
                error: {
                    message: mensaje,
                    otherMessage: otroMensaje
                }
            }
        });
    }

}
