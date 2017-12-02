# agendamlgr
Aplicación que almacena Eventos - _Reborn? Rework? Rebase?_

## Breve Introducción
agendamlgr proporciona un servicio REST, parecido a [agendamlg][1] que nos permite gestionar eventos en Málaga.
Para mostrar la información acerca de los eventos se proporciona un cliente realizado con **?????**.      

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
 1. Crear la base de datos usando el script que se encuentra en la carpeta /sql/agendamlg.sql (la BD se llama AGENDAMLGR)
 2. Clonar el repositorio
 3. Colocar token.json en la carpeta `agendamlg-war/src/java/servlet/tokens.json`
 4. Añadir a .war la carpeta lib (en Netbeans properties>libraries>seleccionar todos los .jar que están en lib)

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
