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
./mvn clean package
```

2. Ejecuta la palicacion Sprint-boot:

```
mvn spring-boot:run
```

## Built With

* [Spring Boot](https://spring.io/projects/spring-boot) - Framework para aplicaciones Java
* [Spring Security](https://spring.io/projects/spring-security) - Autenticación y autorización
* [MongoDB](https://www.mongodb.com/) - Base de datos NoSQL
* [Maven](https://maven.apache.org/) - Gestión de dependencias
* [Thymeleaf](https://www.thymeleaf.org/) - Motor de plantillas para vistas web
* 












# API Documentation

Este documento describe los principales endpoints disponibles en la API de nuestro sistema similar a Twitter.

## Endpoints principales

### Autenticación y usuarios

#### POST `/log-in`
Autentica a un usuario y genera un token JWT de sesión.

**Entrada:**
```json
{
  "username": "nombre_usuario",
  "password": "contraseña"
}
```

**Salida exitosa:**
```json
{
  "session": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Error:**
- Status: 401 Unauthorized
- Body: "Incorrect credentials"

#### POST `/register`
Registra un nuevo usuario en el sistema.

**Entrada:**
```json
{
  "username": "nuevo_usuario",
  "password": "contraseña",
  "email": "usuario@ejemplo.com"
  // otros campos de usuario
}
```

**Salida exitosa:**
- Status: 201 Created
- Body: Objeto completo del usuario registrado

### Publicaciones

#### GET `/posts/feed` 🔒
**Endpoint protegido con JWT**. Obtiene el feed de publicaciones para el usuario autenticado.

Este endpoint es un ejemplo perfecto de protección mediante JWT:
1. Requiere un token JWT en el encabezado `Authorization` con formato `Bearer [token]`
2. El servicio verifica el token antes de procesar la solicitud
3. Si el token es inválido o no existe, retorna un error 401 Unauthorized

Este mecanismo de protección puede aplicarse a cualquier otra ruta que requiera autenticación, siguiendo el mismo patrón implementado aquí.

**Encabezados requeridos:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Salida exitosa:**
- Status: 200 OK
- Body: Lista de publicaciones en el feed del usuario

**Errores:**
- 401 Unauthorized: Token inválido o no proporcionado
- 400 Bad Request: Error en los parámetros

#### POST `/posts/create`
Crea una nueva publicación.

**Entrada:**
```json
{
  "userId": "id_usuario",
  "content": "Contenido de la publicación",
  "parentPostId": "id_post_padre" // opcional, para respuestas
}
```

**Salida exitosa:**
- Status: 200 OK
- Body: Objeto de la publicación creada

#### GET `/posts/{id}`
Obtiene una publicación específica por su ID.

**Salida exitosa:**
- Status: 200 OK
- Body: Objeto de la publicación

**Error:**
- Status: 404 Not Found (si la publicación no existe)

#### GET `/posts/{id}/replies`
Obtiene todas las respuestas a una publicación específica.

**Salida exitosa:**
- Status: 200 OK
- Body: Lista de publicaciones que son respuestas

#### GET `/posts/user`
Obtiene todas las publicaciones de un usuario específico.

**Parámetros de consulta:**
- `userId`: ID del usuario

**Salida exitosa:**
- Status: 200 OK
- Body: Lista de publicaciones del usuario

#### DELETE `/posts/{id}`
Elimina una publicación específica.

**Salida exitosa:**
- Status: 200 OK
- Body: "Post deleted successfully"

#### POST `/posts/thread`
Crea un nuevo hilo de publicaciones.

**Entrada:**
```json
{
  "userId": "id_usuario",
  "content": "Contenido del post inicial del hilo"
  // No debe incluir parentPostId
}
```

**Salida exitosa:**
- Status: 200 OK
- Body: Objeto de la publicación inicial del hilo

## Protección con JWT

Para proteger cualquier ruta con JWT, se debe seguir el mismo patrón implementado en el endpoint `/posts/feed`:

1. Añadir el parámetro `@RequestHeader(value = "Authorization", required = true) String authHeader` al método del controlador
2. Verificar que el encabezado comience con `"Bearer "`
3. Extraer el token: `String token = authHeader.substring(7);`
4. Verificar el token con el servicio JWT: `jwtService.verify(token)`
5. Procesar la solicitud solo si la verificación es exitosa
6. Devolver un error 401 Unauthorized si la verificación falla

Este mecanismo puede aplicarse a cualquier endpoint que requiera autenticación para proteger los recursos sensibles.

##Despliegue en AWS

### Crecaión de microservicios

Se puede encontrar los microservicios en el siguiente repo:

https://github.com/Andrea2511/BackEndTwitter.git

Para desplegar en aws, se necsita dividir el front del back y subir el front a un S3 para que el back pueda acceder al montar las API:

![image](https://github.com/user-attachments/assets/7764eada-adac-41ab-a5e2-88131157d325)













## Authors

* **Andrea V. Torres Tobar** - [Andrea2511](https://github.com/Andrea2511)
* **Juan Pablo Camargo** - [AutoMemoryNN](https://github.com/AutoMemoryNN)
* **Joan S. Acevedo Aguilar** - [Joan-Acevedo](https://github.com/Joan-Acevedo)

## Acknowledgments

* Inspirado en la funcionalidad básica de Twitter
* Gracias a todos los contribuidores de las bibliotecas utilizadas 
