# Twitter - ECI

Clone simplificado de Twitter desarrollado con Spring Boot y MongoDB. Esta aplicación permite a los usuarios registrarse, iniciar sesión, crear publicaciones, responder a publicaciones existentes y visualizar hilos de conversación.

## Getting Started

Estas instrucciones te permitirán obtener una copia del proyecto en funcionamiento en tu máquina local para propósitos de desarrollo y pruebas.

### Prerequisites

Para ejecutar este proyecto necesitas:

```
- Java 17 o superior
- Maven 3.6 o superior
- MongoDB
- Git
```

### Installing

Sigue estos pasos para configurar tu entorno de desarrollo:

1. Clona el repositorio en tu máquina local:

```
git clone https://github.com/Joan-Acevedo/PruebaTwitter.git
cd PruebaTwitter
```

2. Configura MongoDB:
   - Asegúrate de tener MongoDB instalado y en ejecución
   - Crea un archivo `.env` en la raíz del proyecto con tus credenciales de MongoDB:

```
MONGODB_URI=mongodb://localhost:27017/twitterdb
```

3. Compila y ejecuta la aplicación:

```
./mvnw spring-boot:run
```

4. La aplicación estará disponible en:

```
http://localhost:8080
```

## Running the tests

Para ejecutar las pruebas automatizadas del sistema, utiliza el siguiente comando:

```
./mvnw test
```

![Image](https://github.com/user-attachments/assets/50592203-cd79-4749-9d07-5e91aa2c5b64)

## Deployment

Para desplegar la aplicación en un entorno de producción:

1. Compila el proyecto:

```
./mvnw clean package
```

2. Crea un archivo de propiedades de producción con la configuración adecuada.

3. Ejecuta el JAR generado:

```
java -jar target/twitter-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Built With

* [Spring Boot](https://spring.io/projects/spring-boot) - Framework para aplicaciones Java
* [Spring Security](https://spring.io/projects/spring-security) - Autenticación y autorización
* [MongoDB](https://www.mongodb.com/) - Base de datos NoSQL
* [Maven](https://maven.apache.org/) - Gestión de dependencias
* [Thymeleaf](https://www.thymeleaf.org/) - Motor de plantillas para vistas web



## Authors

* **Andrea V. Torres Tobar** - [Andrea2511](https://github.com/Andrea2511)
* **Juan Pablo Camargo** - [AutoMemoryNN](https://github.com/AutoMemoryNN)
* **Joan S. Acevedo Aguilar** - [Joan-Acevedo](https://github.com/Joan-Acevedo)

## Acknowledgments

* Inspirado en la funcionalidad básica de Twitter
* Gracias a todos los contribuidores de las bibliotecas utilizadas 