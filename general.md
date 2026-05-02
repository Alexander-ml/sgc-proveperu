## 1 Qué Hace Cada Contenedor
 
#### Contenedor `database` (PostgreSQL)
 
Corre el motor de base de datos en un entorno aislado. Al crearse por primera vez, ejecuta automáticamente los scripts SQL del directorio `init/`, creando el schema completo y los datos iniciales. Usa un volumen Docker para persistir los datos — si el contenedor se recrea, los datos no se pierden.
 
#### Contenedor `backend` (Spring Boot)
 
Corre el servidor Java compilado. Arranca solo después de que el contenedor de base de datos está sano (healthcheck verificado). Se conecta a `database:5432` usando el nombre del servicio como hostname — dentro de la red Docker, los contenedores se resuelven por nombre.
 
#### Contenedor `frontend` (Nginx + React compilado)
 
Sirve los archivos estáticos compilados de React. Nginx está configurado para:
- Servir `index.html` para cualquier ruta que no sea un archivo existente (necesario para React Router).
- Hacer proxy inverso de las peticiones `/api/*` al contenedor `backend:8080`.
---
 
### `docker-compose.yml` — El Director de Orquesta
 
Define los tres servicios, sus relaciones, configuraciones y comunicaciones. Sus responsabilidades:
 
**Redes internas:** Crea una red privada entre los contenedores. El backend y el frontend se comunican con la base de datos por nombre (`database`), no por IP. El frontend se comunica con el backend por nombre (`backend`). Ningún tráfico interno pasa por el host.
 
**Volúmenes:** El contenedor de base de datos tiene un volumen persistente. Sin volumen, cada vez que se recrea el contenedor se pierden todos los datos. El volumen almacena los archivos de datos de PostgreSQL en el disco del host y los monta en el contenedor.
 
**Variables de entorno:** Lee las variables del archivo `.env` de la raíz y las inyecta en los contenedores. Ningún valor sensible está hardcodeado en el `docker-compose.yml`.
 
**Puertos expuestos:** Solo los puertos que el desarrollador necesita acceder desde el host: `80` para el frontend (accesible en el browser), `5432` para la base de datos (accesible desde pgAdmin), y opcionalmente `8080` para el backend (accesible desde Postman durante el desarrollo).
 
**Orden de arranque:** El backend declara dependencia del servicio de base de datos con su healthcheck. El frontend puede arrancar independientemente ya que Nginx solo redirige el tráfico.
 
**Persistencia:** Solo la base de datos necesita volumen persistente. El frontend y backend son stateless — sus contenedores pueden recrearse sin perder datos porque los datos viven en PostgreSQL.
 
---
 
### Dockerfile por Servicio
 
**`frontend/Dockerfile`:** Proceso de dos etapas. Primera etapa: instala Node.js, copia el código fuente, instala dependencias, y ejecuta el build de Vite generando los archivos estáticos en `dist/`. Segunda etapa: copia solo el directorio `dist/` a una imagen base de Nginx Alpine. La imagen final es pequeña porque no incluye Node.js ni el código fuente.
 
**`backend/Dockerfile`:** Proceso de dos etapas. Primera etapa: usa una imagen de Maven con JDK para compilar el proyecto y generar el archivo `.jar`. Segunda etapa: copia solo el `.jar` a una imagen base de JRE (no JDK) más pequeña. La imagen final solo tiene lo necesario para ejecutar la aplicación.
 
**`database`:** No requiere Dockerfile personalizado. Se usa directamente la imagen oficial `postgres:16`. La imagen oficial acepta un directorio de scripts de inicialización que se monta como volumen.
 
---
 
### Logs
 
Los logs de los tres contenedores son accesibles en tiempo real con Docker Compose. Para el equipo de desarrollo, los logs del backend son la herramienta principal de diagnóstico: muestran cada petición HTTP recibida, las consultas SQL ejecutadas (en modo debug), y los errores con stack trace.
 
---
 
### Healthchecks
 
El healthcheck del contenedor de base de datos usa `pg_isready` — el comando nativo de PostgreSQL que devuelve éxito solo cuando el motor está aceptando conexiones activamente.
 
El healthcheck del contenedor de backend usa `GET /actuator/health` — el endpoint que Spring Boot expone automáticamente con Actuator.
 
Sin healthchecks, `depends_on` en docker-compose solo espera que el proceso del contenedor arranque, no que el servicio dentro esté listo. PostgreSQL puede tardar varios segundos en estar listo después de que el proceso arranca.
 
---
 
### Reinicios
 
El contenedor de base de datos se configura con política de reinicio `unless-stopped`. Si el contenedor falla, Docker lo reinicia automáticamente. Esto es útil para el entorno local cuando la laptop hiberna o reinicia — al volver, los contenedores se levantan solos.
 
---
 
### Flujo Local Diario
 
El flujo de trabajo del equipo con Docker es simple y siempre el mismo:
 
**Al empezar el día:** Levantar todos los servicios con un comando. Los tres contenedores arrancan en el orden correcto.
 
**Durante el desarrollo:** El backend tiene DevTools activos — detecta cambios en el código compilado y reinicia automáticamente. El frontend con Vite tiene hot module replacement — los cambios en React se reflejan en el browser sin recargar.
 
**Al terminar el día:** Detener los contenedores. Los datos persisten en el volumen — la base de datos queda exactamente como se dejó.
 
**Al incorporar cambios del equipo:** Si un integrante subió un script de migración nuevo, descargar los cambios y recrear el contenedor de base de datos para que aplique el nuevo script.
 
---
 
### Errores Comunes con Docker
 
No incluir el directorio de datos de PostgreSQL en el `.gitignore`. Es un directorio de potencialmente gigabytes que nunca debe subir al repositorio.
 
Hardcodear credenciales en `docker-compose.yml` o en los Dockerfiles en lugar de leerlas del `.env`.
 
Olvidar el flag `--build` al levantar los contenedores después de cambios en el código. Sin `--build`, Docker usa las imágenes cacheadas y los cambios no se reflejan.
 
Compartir el archivo `.env` con credenciales reales por medios inseguros. Cada integrante crea su `.env` local basado en el `.env.example`.
 
---
 
## 2. VARIABLES DE ENTORNO

### Principio General 
 
Las variables de entorno son el mecanismo para separar la configuración del código. Lo que cambia entre entornos (credenciales, URLs, puertos, claves secretas) nunca se hardcodea en el código fuente. Va en variables de entorno.
 
---
 
### `.env` en la Raíz del Proyecto
 
**Para qué sirve:** Centraliza las variables que usa `docker-compose.yml` para configurar los tres contenedores.
 
**Qué guardar aquí:**
- Credenciales de PostgreSQL (`POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`)
- Puertos de cada servicio (`BACKEND_PORT`, `FRONTEND_PORT`, `DB_PORT`)
- Clave secreta JWT (`JWT_SECRET_KEY`, larga y aleatoria)
- Tiempo de expiración del JWT en milisegundos (`JWT_EXPIRATION_MS`)

**Qué NO guardar aquí:** Credenciales de sistemas externos reales, claves de APIs de terceros, datos de producción.
 
**Regla de seguridad:** Este archivo nunca sube a GitHub. El `.gitignore` lo excluye desde el primer día. Compartirlo por email o WhatsApp es aceptable para el equipo local, siendo conscientes de que no son credenciales de producción.
 
---
 
### `.env.example` en la Raíz del Proyecto
 
**Para qué sirve:** Plantilla pública del `.env` real. Contiene los nombres de todas las variables con valores de ejemplo o placeholders. Este archivo sí sube a GitHub.
 
**Qué contiene:**
```
POSTGRES_DB=nombre_de_tu_base
POSTGRES_USER=tu_usuario
POSTGRES_PASSWORD=tu_contraseña_segura
BACKEND_PORT=8080
FRONTEND_PORT=80
DB_PORT=5432
JWT_SECRET_KEY=clave_aleatoria_larga_de_al_menos_256_bits
JWT_EXPIRATION_MS=28800000
```
 
Cuando un nuevo integrante clona el repositorio, copia este archivo, lo renombra a `.env`, y rellena los valores reales.
 
---
 
### `.env.local` en `/frontend/`
 
**Para qué sirve:** Variables que el frontend necesita en tiempo de construcción o en tiempo de ejecución en el browser.
 
**Qué guardar aquí:**
- URL base de la API del backend (`VITE_API_URL=http://localhost:8080/api`)
**Importante:** En Vite, solo las variables que empiezan con `VITE_` son accesibles en el código del browser. El resto se ignora.
 
**Qué NO guardar aquí:** Nada sensible. Todo lo que se pone en variables del frontend es visible para cualquiera que inspeccione el build. El JWT_SECRET_KEY nunca va aquí — va solo en el backend.
 
---
 
### `application.yml` en `/backend/`
 
**Para qué sirve:** La configuración principal de Spring Boot. Lee sus valores sensibles de las variables de entorno del contenedor (inyectadas por docker-compose desde el `.env`).
 
**Qué configura:**
- URL de conexión a la base de datos: leída de `${POSTGRES_USER}`, `${POSTGRES_PASSWORD}`, `${POSTGRES_DB}`
- Puerto del servidor: leído de `${BACKEND_PORT}`
- Configuración JPA: dialecto PostgreSQL, timezone, logging de SQL (solo en dev)
- Clave JWT: leída de `${JWT_SECRET_KEY}`
- Expiración JWT: leída de `${JWT_EXPIRATION_MS}`
- Timezone de la JVM: `America/Lima`
**Qué NO guardar aquí:** Ningún valor real de credenciales. Siempre usar la sintaxis `${NOMBRE_VARIABLE}` para leer de entorno.
 
**Perfiles:** Tener un `application-dev.yml` para configuración específica de desarrollo (nivel de log DEBUG, show-sql activado) y `application.yml` para configuración base. Los Dockerfiles activan el perfil correcto.
 
---

 ### Variables PostgreSQL en el Contenedor
 
PostgreSQL oficial de Docker acepta variables de entorno para su configuración inicial:
- `POSTGRES_DB` — nombre de la base de datos a crear
- `POSTGRES_USER` — usuario administrador de la base
- `POSTGRES_PASSWORD` — contraseña del usuario
- `TZ` — timezone del servidor (`America/Lima`)
Estas variables las inyecta `docker-compose.yml` desde el `.env` de la raíz.
 
---
 
### Resumen de Seguridad
 
| Variable | Dónde vive | Va a Git |
|---|---|---|
| Credenciales de DB | `.env` raíz | ❌ Nunca |
| JWT Secret Key | `.env` raíz | ❌ Nunca |
| URL del backend | `.env.local` frontend | ❌ Nunca |
| Nombres de variables (sin valores) | `.env.example` | ✅ Siempre |
| Configuración base del servidor | `application.yml` | ✅ (sin valores reales) |
 
---

## 4. HTTP ERRORS
 
Los códigos de respuesta HTTP son el contrato entre backend y frontend. El frontend necesita saber exactamente qué significa cada código para mostrar el mensaje correcto al usuario.
 
---
 
### Códigos de Éxito
 
**200 OK**
Operación exitosa con datos que retornar.
- Cuando se consulta la lista de ventas.
- Cuando se obtiene el detalle de un producto.
- Cuando se genera el resumen del dashboard.
**201 Created**
Recurso creado exitosamente.
- Al registrar una nueva venta.
- Al crear un nuevo cliente.
- Al registrar una compra.
- Al aperturar la caja.
**204 No Content**
Operación exitosa sin datos que retornar.
- Al actualizar el estado de un usuario.
- Al desactivar un proveedor (soft delete).
- Al cerrar sesión (si el servidor gestiona algo al logout).
---
 
### Códigos de Error del Cliente
 
**400 Bad Request**
Los datos enviados no cumplen las validaciones básicas de formato.
- Cantidad de producto enviada como texto en lugar de número.
- Campo obligatorio vacío (nombre del cliente en blanco).
- Formato de DNI incorrecto (menos de 8 dígitos).
- El frontend muestra los errores por campo específico.
**401 Unauthorized**
No hay sesión activa o el token JWT expiró.
- El usuario accede a cualquier endpoint protegido sin token.
- El token JWT expiró después de las 8 horas de jornada.
- El frontend redirige automáticamente al login al recibir este código.
**403 Forbidden**
Hay sesión activa pero el rol del usuario no tiene permiso para esta operación.
- Un vendedor intenta acceder al listado de usuarios (`GET /api/usuarios`).
- Un personal de almacén intenta anular una venta.
- El frontend muestra "No tienes permiso para realizar esta acción".
**404 Not Found**
El recurso solicitado no existe en la base de datos.
- Se solicita el detalle de un cliente con ID que no existe.
- Se intenta editar un producto que fue eliminado lógicamente.
- El frontend muestra un mensaje descriptivo de qué no se encontró.
**409 Conflict**
La operación viola una regla de unicidad del sistema.
- Se intenta registrar un usuario con un `usuario_login` que ya existe.
- Se intenta crear un proveedor con un RUC ya registrado.
- Se intenta aperturar la caja cuando ya hay una caja abierta.
- El frontend muestra el motivo específico del conflicto.
**422 Unprocessable Entity**
Los datos tienen formato correcto pero violan una regla de negocio.
- Se intenta vender un producto con stock cero o insuficiente.
- El monto pagado es menor al total de la venta.
- Se intenta cerrar la caja sin haberla aperturado primero.
- Se intenta registrar una compra a un proveedor inactivo.
- El frontend muestra el mensaje de negocio específico (no un error genérico).
---

### Códigos de Error del Servidor
 
**500 Internal Server Error**
Error inesperado en el servidor que no fue anticipado.
- Fallo en la conexión con la base de datos.
- Error de programación no manejado.
- El `GlobalExceptionHandler` captura estas excepciones y devuelve un mensaje genérico al usuario ("Ocurrió un error inesperado, intente nuevamente"). Nunca el stack trace de Java.
- El error completo con stack trace se registra en los logs del backend para diagnóstico.
---
 
### Regla de Oro para el Backend
 
Nunca devolver un stack trace de Java como respuesta al frontend. El `GlobalExceptionHandler` intercepta todas las excepciones no manejadas y las convierte en respuestas JSON estructuradas con el código HTTP apropiado y un mensaje legible para el usuario. El stack trace va a los logs, no a la respuesta.
 
---
