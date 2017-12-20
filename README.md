# agendamlgr
Aplicación que almacena Eventos - _Reborn? Rework? Rebase?_

## Breve Introducción
agendamlgr proporciona un servicio REST, parecido a [agendamlg][1] que nos permite gestionar eventos en Málaga.
Para mostrar la información acerca de los eventos se proporciona un cliente realizado con **Angular 2** y **Bootstrap**.      

## Características
agendamlg proporciona los siguientes servicios:     
* Diferentes tipos de usuarios con diferentes permisos: anónimo, registrado, superusuario y periodista.      
* Iniciar Sesión y Cerrar Sesión, mediante Google OAuth 2.0.     
* Ver perfil de Usuario.    
* Ver todos los evento no caducados y validados
* Filtrar eventos en función de su categoría
* Filtrar eventos según su posición geográfica
* Creación de eventos
* Validación de eventos
* Eliminación de eventos
* Añadir álbums de Flickr para la visualización de imágenes en los eventos
* Visualización de mapa mediante Google Maps
* Envío de correo acerca de eventos recién publicados a usuarios interesados
* Envío de correo al usuario cuyo evento ha sido validado
el cliente permite acceder todos los servicios de la lista.

## Configuración
 1. Crear la base de datos usando los scripts que se encuentra en la carpeta /sql/agendamlg.sql /sql/seed.sql (la BD se llama `AGENDAMLGR`, el usuario y contraseña `agendamlgr`)
 2. Clonar el repositorio
 3. Colocar `token.json` de Google OAuth 2.0 en la carpeta `agendamlg-war/src/java/servlet/tokens.json`
 4. Colocar `token.json` del resto de tokens en la carpeta `agendamlg-war/src/java/service/tokens.json`
 5. Añadir al proyecto _agendamlr-war_ la carpeta `agendamlgr-war/lib` (en Netbeans properties>libraries>seleccionar todos los .jar que están en lib)
 6. Instalar dependencias de node.js del proyecto _agendamlgr-cliente_ con `npm install`
 7. Compilar la versión _debug_ (`npm run build-debug`) o _release_ (`npm run build`) del cliente

## Quieres instalar AngularMaps (o cualquier otra nueva dependencia) y peta?
Si después del pull `npm install` no funciona, haz lo siguiente: `npm i -f` y luego `npm install`

## Requisitos
Hay que tener instalado:
* JVM
* node.js
* Glassfish
* Apache Derby

## Creado a partir de:
* [Glassfish](https://javaee.github.io/glassfish/) - Servidor Web
* [Java EE](http://www.oracle.com/technetwork/java/javaee/overview/index.html) - Marco de Trabajo para Desarrollo Web
* [Apache Derby](https://db.apache.org/derby/) - Base de datos
* [node.js][2] - Parte del frontend

## Autores
* **Antonio Ángel Cruzado Castillo**
* **John Carlo Purihin**
* **Melchor Alejo Garau Madrigal**
* **Manuel Jesús Rodríguez Rodríguez**

  [1]: https://github.com/aangelcc/agendamlg
  [2]: http://nodejs.org
