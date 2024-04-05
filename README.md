# Gestor de Viajes 

## Introducción
Este proyecto consiste en la creación de una app móvil usando Android Studio, que es un gestor de viajes. Las funcionalidades básicas de la app permitirían al usuario crear, modificar y compartir itinerarios de viaje con otros usuarios. Dentro de cada itinerario, se podrán planificar las actividades que se desean realizar, así como su visualización en un calendario integrado. Dentro de este calendario, el usuario podrá marcar como realizadas las actividades aún no realizadas para ese itinerario, siguiendo un buen progreso de viaje. A nivel de itinerario también habrá una sección que le permitirá al usuario hacer una gestión de documentos, teniendo así de forma organizada los documentos necesarios para ese viaje, esto sería independiente para cada usuario.Una vez haya pasado la fecha de finalización del viaje, el itinerario se marcaría como finalizado, por lo que no se podrían seguir añadiendo actividades ni destinos, quedando guardado y sirviendo solo para visualización.

## Características Principales
### Creación y Organización de Itinerarios:
Para crear y modificar los itinerarios, un usuario podrá hacerlo de forma individual o compartida. En el caso de ser individual, el usuario añadirá los destinos a visitar para ese itinerario, además de las actividades. Si fuese compartido, varios usuarios podrían seguir un sistema de votación para decidir los destinos y actividades a realizar dentro de ese itinerario. Para esto, se ha pensado que se podría usar el servicio Firebase Realtime Database, que permite almacenar y sincronizar los itinerarios de los usuarios en tiempo real.

### Calendario de Actividades y Reservas:
Dentro de cada itinerario, los usuarios podrán visualizar las actividades a realizar, además de poder marcarlas como realizadas. Esto es, varios usuarios podrían de forma compartida marcar como completadas diferentes actividades dentro del mismo itinerario. Se seguiría utilizando Firebase Realtime Database para así poder modificar las actividades dentro del calendario en tiempo real.

### Gestión de Documentos:
Aunque el itinerario podría estar compartido entre diferentes usuarios, dentro de este, cada usuario tendría acceso a un apartado privado en el que poder ver y gestionar todos sus documentos. Es decir, varios usuarios podrían acceder a un itinerario compartido, pero cada uno de ellos tendría acceso dentro de este a sus propios documentos(pasaportes, billetes, entradas, seguros...). Para esto, se ha pensado en utilizar Firebase Storage, en el que solo el propio usuario tendría acceso a estos documentos.

### Gestión de Multimedia:

Para cada itinerario, se podría ir recopilando los archivos multimedia realizados por un usuario, o por varios si fuese un itinerario compartido. Los usuarios del mismo itinerario podrían visualizar todo lo subido por los demás durante el viaje. Para esto se volvería a hacer uso de Firebase Realtime Database, para poder actualizar los datos en tiempo real.

## Firebase
### Firebase Authentication:

Firebase Authentication es un servicio de autenticación, que permite inicios de sesión seguros en las aplicaciones. Los usuarios pueden autenticarse utilizando diversos métodos, facilitando la gestión de usuarios y la protección de datos, garantizando que solo usuarios autorizados puedan acceder a la aplicación.

### Firebase Realtime Database:
Firebase Realtime Database funciona como un almacén de datos en tiempo real que permite sincronizar datos entre usuarios y dispositivos en tiempo real. Cualquier cambio realizado en la base de datos se refleja instantáneamente en todos los clientes conectados a la aplicación, lo que permite una experiencia de usuario fluida y colaborativa.

### Firebase Storage:

Firebase Storage es un servicio de almacenamiento en la nube, diseñado para el almacenamiento seguro de archivos y datos multimedia. Permite cargar y visualizar archivos de manera eficiente, garantizando que los archivos estén disponibles para su acceso en todo momento, de forma segura.
