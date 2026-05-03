# Conexión pgAdmin con PostgreSQL en Docker

## DIAGRAMA DE SITUACIÓN ACTUAL

Antes de empezar, entiende exactamente cómo están posicionados los actores en tu entorno:

```
╔══════════════════════════════════════════════════════════════╗
║  TU LAPTOP (host)                                            ║
║                                                              ║
║  pgAdmin (instalado local o en contenedor)                   ║
║      │                                                       ║
║      │  Conexión: 127.0.0.1:5433                             ║
║      │  (puerto externo mapeado por Docker)                  ║
║      ▼                                                       ║
║    ┌─────────────────────────────────────────────────────┐   ║
║    │  DOCKER                                             │   ║
║    │  Red: proveperu-network                             │   ║
║    │                                                     │   ║
║    │  ┌─────────────────────────────────────────────┐    │   ║
║    │  │  Contenedor: proveperu-database             │    │   ║
║    │  │  Imagen: postgres:16-alpine                 │    │   ║
║    │  │  Puerto interno: 5432                       │    │   ║
║    │  │  Volumen: proveperu-postgres-data           │    │   ║
║    │  │  Red: proveperu-network                     │    │   ║
║    │  └─────────────────────────────────────────────┘    │   ║
║    │                                                     │   ║
║    │  ┌──────────────┐   ┌──────────────────────────┐    │   ║
║    │  │   backend    │   │       frontend           │    │   ║
║    │  │  :8080       │   │        :80               │    │   ║
║    │  └──────────────┘   └──────────────────────────┘    │   ║
║    └─────────────────────────────────────────────────────┘   ║
╚══════════════════════════════════════════════════════════════╝

Puerto mapeado: host:5433 → contenedor:5432
```

**Se esta usando:**  `DB_PORT_EXTERNAL=5433` y `DB_PORT_INTERNAL=5432`. Esto significa que desde fuera de Docker (desde pgAdmin instalado en tu laptop) debes usar el puerto **5433**, no 5432.


## 1. FUNCIONAMIENTO DE LA CONEXIÓN pgAdmin ***con*** PostgreSQL EN DOCKER

### ¿Cómo se conecta pgAdmin?

pgAdmin no tiene acceso directo al interior de Docker. La conexión pasa siempre por el puerto mapeado que tu `docker-compose.yml` expone al host.

```
pgAdmin
   │
   │  TCP/IP a localhost:5433
   │  (el puerto externo que Docker expone al host)
   ▼
Docker Engine (capa de red del host)
   │
   │  Redirige internamente → proveperu-database:5432
   │  (dentro de la red proveperu-network)
   ▼
PostgreSQL dentro del contenedor proveperu-database
   │
   │  Lee y escribe datos en
   ▼
Volumen: proveperu-postgres-data
   └─ /var/lib/postgresql/data (dentro del contenedor)
```

### Qué debe estar activo para que funcione

| Elemento | Por qué es necesario | Qué pasa si no está |
|---|---|---|
| **Contenedor `proveperu-database`** en estado `running` | Es el servidor PostgreSQL | Conexión rechazada. pgAdmin no puede conectar |
| **Puerto `5433` mapeado** | Es el punto de acceso desde el host | Conexión a `127.0.0.1:5433` falla |
| **Red `proveperu-network`** | Permite comunicación entre contenedores | Irrelevante para pgAdmin local (la red interna no afecta la conexión desde el host) |
| **Volumen `proveperu-postgres-data`** | Almacena los datos | Si no existe, PostgreSQL arranca vacío (sin tablas, sin datos) |
| **Credenciales correctas** | PostgreSQL autentica cada conexión | Error de autenticación |

### Qué pasa en cada escenario de fallo

**Si el contenedor está detenido:**
pgAdmin muestra: `could not connect to server: Connection refused. Is the server running on host "localhost" and accepting TCP/IP connections on port 5433?`

**Si el contenedor está corriendo pero el volumen fue eliminado:**
pgAdmin conecta, pero la base de datos `proveperu_db` no existe todavía. PostgreSQL arrancó vacío porque el volumen estaba vacío, lo cual reactivó la ejecución de los scripts en `init/`.

**Si las credenciales son incorrectas:**
pgAdmin muestra: `FATAL: password authentication failed for user "proveperu_user"`

---

## 2. CONFIGURACIÓN EXACTA DE LA CONEXIÓN EN pgAdmin

Cuando creas una nueva conexión en pgAdmin (clic derecho en "Servers" → "Register" → "Server"), usa exactamente estos valores:

**Pestaña General:**
```
Name:    SGC-ProvePeru Local
```

**Pestaña Connection:**

| Campo pgAdmin | Valor correcto | Por qué |
|---|---|---|
| **Host name/address** | `127.0.0.1` | pgAdmin está en tu laptop, fuera de Docker |
| **Port** | `5433` | Es el `DB_PORT_EXTERNAL` de tu .env |
| **Maintenance database** | `proveperu_db` | Es el valor de `POSTGRES_DB` |
| **Username** | `proveperu_user` | Es el valor de `POSTGRES_USER` |
| **Password** | el que pusiste en tu `.env` | Valor de `POSTGRES_PASSWORD` |
| **Save password** | `Marcar`  | Evita escribirlo en cada sesión |

### La confusión más frecuente: ¿`127.0.0.1` o `database`?

| Desde dónde conectas | Host a usar | Puerto a usar | Motivo |
|---|---|---|---|
| **pgAdmin instalado en tu laptop** | `127.0.0.1` | `5433` (DB_PORT_EXTERNAL) | Estás fuera de Docker, accedes por el puerto mapeado |
| **pgAdmin como contenedor Docker en la misma red** | `database` | `5432` (DB_PORT_INTERNAL) | Estás dentro de Docker, usas el nombre del servicio |
| **Backend Spring Boot (dentro de Docker)** | `database` | `5432` (DB_PORT_INTERNAL) | Está en la misma red Docker, usa el nombre del servicio |



### Verificación rápida antes de conectar

```bash
# Verifica que el contenedor está corriendo y el puerto mapeado
docker compose ps

# Debes ver algo como:
# proveperu-database   running   127.0.0.1:5433->5432/tcp
```

Si ves `5433->5432`, el mapeo está activo. pgAdmin puede conectarse.

---

## 3. ROL DEL VOLUMEN `postgres_data`

### Cómo funciona la persistencia

El volumen `proveperu-postgres-data` es un espacio de almacenamiento gestionado por Docker, separado del sistema de archivos del contenedor. Cuando PostgreSQL escribe datos, los escribe en `/var/lib/postgresql/data` dentro del contenedor, pero ese directorio está montado sobre el volumen Docker en el host.

```
Contenedor proveperu-database
   └── /var/lib/postgresql/data/ - montado sobre -  Volumen: proveperu-postgres-data
         ├── base/        (en el host, gestionado por Docker)
         ├── global/
         ├── pg_hba.conf
         └── postgresql.conf
```

### Qué ocurre en cada escenario

**Escenario 1: `docker compose down` (sin -v)**
```bash
docker compose down
```
- El contenedor se elimina.
- El volumen `proveperu-postgres-data` **permanece intacto**.
- Los datos siguen ahí.
- Al volver a levantar con `docker compose up`, PostgreSQL arranca con todos los datos que tenía.
- pgAdmin reconectará y verá exactamente el mismo estado.

**Escenario 2: `docker compose down -v` (con volumen)**
```bash
docker compose down -v
```
- El contenedor se elimina.
- El volumen `proveperu-postgres-data` **se elimina**.
- Los datos se pierden completamente.
- Al volver a levantar con `docker compose up`, PostgreSQL arranca vacío.
- Docker ejecuta automáticamente los scripts de `database/init/` porque el volumen está vacío.
- pgAdmin verá el estado inicial definido por esos scripts.

**Escenario 3: eliminar solo el contenedor, no el volumen**
```bash
docker rm proveperu-database
docker compose up database
```
- Mismo resultado que el escenario 1: datos intactos porque el volumen sobrevive.

### Impacto en lo que ves en pgAdmin

| Acción | Volumen | Lo que ves en pgAdmin |
|---|---|---|
| `docker compose up` | Existente con datos | Las tablas y datos anteriores |
| `docker compose down` + `up` | Existente con datos | Mismo estado anterior |
| `docker compose down -v` + `up` | Recreado vacío → init/ ejecutado | Estado inicial (lo que definiste en init/) |
| Primera vez que levantas | Vacío → init/ ejecutado | Estado inicial |

---

## 4. RELACIÓN ENTRE pgAdmin Y TU ESTRUCTURA `database/`

### Tabla completa de interacción

| Carpeta | Rol técnico | Quién lo ejecuta | Cómo interactúas desde pgAdmin |
|---|---|---|---|
| `init/` | Scripts de inicialización del schema base | **Docker automáticamente** al crear el volumen vacío | Solo lo verificas: abres pgAdmin y confirmas que las tablas existen |
| `migrations/` | Cambios al schema post-inicialización | **Tú manualmente** - Docker no los ejecuta automáticamente | Abres el archivo `.sql`, copias el contenido, lo pegas en el Query Tool de pgAdmin y ejecutas |
| `scripts/` | Datos de prueba, limpieza, diagnóstico | **Tú manualmente** | Igual que migrations: Query Tool en pgAdmin |
| `backups/` | Dumps de la base de datos | **pgAdmin** - tú manualmente | pgAdmin tiene función de backup integrada que genera archivos aquí |
| `config/` | Parámetros del motor PostgreSQL | **Docker al arrancar** si se monta el archivo | Puedes ver los valores actuales en pgAdmin: Server → Properties o con `SHOW timezone` en el Query Tool |

### Lo que se ejecuta automáticamente

Tu `docker-compose.yml` monta esta carpeta:
```yaml
volumes:
  - ./database/init:/docker-entrypoint-initdb.d
```

La imagen oficial `postgres:16-alpine` tiene un comportamiento específico: cuando el directorio de datos está vacío (primera vez o después de `docker compose down -v`), ejecuta automáticamente, en orden alfabético, todos los archivos `.sql` y `.sh` que encuentre en `/docker-entrypoint-initdb.d/`.

**Flujo exacto:**
```
docker compose up --build
    │
    ▼
¿El volumen postgres_data está vacío?
    │
    ├── SÍ → PostgreSQL se inicializa
    │         ├── Crea el cluster de base de datos
    │         ├── Crea la DB: proveperu_db
    │         ├── Crea el usuario: proveperu_user
    │         └── Ejecuta en orden: database/init/*.sql
    │               └── (tus tablas, datos maestros, usuario admin)
    │
    └── NO → PostgreSQL arranca directamente con los datos existentes
              └── Los scripts de init/ NO se ejecutan
```

### Lo que debes ejecutar manualmente desde pgAdmin

Cuando hay un nuevo script de migración (`database/migrations/V002__...sql`):

1. Abre pgAdmin → conecta al servidor
2. Expande: Servers → SGC-ProvePeru Local → Databases → proveperu_db
3. Clic derecho en `proveperu_db` → **Query Tool**
4. Abre el archivo de migración o copia su contenido
5. Ejecuta con el botón ▶ (Run) o F5
6. Verifica que no hubo errores en el panel Messages
7. Refresca el schema para ver los cambios

---

## 5. FLUJO REAL DE TRABAJO

### Primera vez

```
PASO 1 — Preparar el script de inicialización
  └── Crear database/init/01_schema_auth.sql con tus tablas y datos iniciales

PASO 2 — Levantar los contenedores
  └── docker compose up --build
  └── Docker detecta volumen vacío → ejecuta database/init/01_schema_auth.sql

PASO 3 — Verificar en logs que init/ se ejecutó
  └── docker compose logs database
  └── Buscar: "database system is ready to accept connections"
  └── Buscar: "executing /docker-entrypoint-initdb.d/01_schema_auth.sql"

PASO 4 — Conectar pgAdmin
  └── Host: 127.0.0.1 | Puerto: 5433 | DB: proveperu_db | User: proveperu_user

PASO 5 — Validar en pgAdmin
  └── Expandir: proveperu_db → Schemas → public → Tables
  └── Deben aparecer tus tablas: usuario, rol, etc.
  └── Verificar datos: clic derecho en tabla → View/Edit Data → All Rows

PASO 6 — Verificar que el backend se conectó
  └── GET http://localhost:8080/actuator/health
  └── Debe mostrar: "db": { "status": "UP" }
```

### Flujo diario normal

```
INICIO DEL DÍA:
  1. docker compose up --build
  2. Esperar que los 3 servicios estén "running"
  3. Conectar pgAdmin (si es necesario para ese día)
  4. Verificar /actuator/health

DURANTE EL DESARROLLO:
  Si alguien subió una migración nueva:
    1. git pull origin develop
    2. Abrir el archivo migrations/V00X__descripcion.sql en pgAdmin
    3. Ejecutar en Query Tool
    4. Verificar que no hubo errores
    5. Actualizar al equipo

FIN DEL DÍA:
  1. Si creaste cambios de schema → crear migration SQL → commit
  2. docker compose down
```

### Flujo cuando necesitas resetear todo

```
docker compose down -v
  └── Elimina contenedores Y el volumen con todos los datos

docker compose up --build
  └── Crea todo desde cero
  └── Ejecuta init/ automáticamente
  └── Tienes el estado inicial limpio

Desde pgAdmin:
  └── La conexión existente seguirá funcionando
  └── Verás solo las tablas del init/
  └── Sin datos de prueba (debes ejecutar scripts/ manualmente)
```

---

## 6. VENTAJAS DE pgAdmin EN TU ENTORNO

### Para el flujo de trabajo del equipo

| Ventaja | Aplicación concreta en SGC-ProvePeru |
|---|---|
| **Visualización del schema** | Ver las tablas `usuario`, `rol`, `venta`, `detalle_venta` con sus columnas, tipos, constraints y FK sin escribir SQL |
| **Verificar init/ se ejecutó** | Después de `docker compose up`, confirmar en segundos que las tablas del script existen |
| **Ejecutar migraciones** | Copiar el contenido de `migrations/V002__...sql` al Query Tool y ejecutar con un clic |
| **Ejecutar seed data** | Abrir `scripts/seed_data.sql` en el Query Tool y poblar la base con datos de prueba |
| **Debugging de datos** | Cuando el backend devuelve datos incorrectos, consultar directamente la tabla para verificar qué hay en la base |
| **Validar constraints** | Intentar insertar un registro inválido manualmente para verificar que el CHECK constraint funciona |
| **EXPLAIN ANALYZE** | Pegar una consulta compleja del backend y ver el plan de ejecución para detectar si falta un índice |
| **Backups rápidos** | Click derecho en `proveperu_db` → Backup → genera un dump en `database/backups/` |

### Casos específicos donde pgAdmin es superior a cualquier alternativa en tu proyecto

**Verificar integridad referencial:** Expandir Tables → `detalle_venta` → Constraints → ver la FK hacia `venta`. Visual, inmediato, sin SQL.

**Comparar schema vs entidades Java:** Ver la tabla `usuario` en pgAdmin y comparar columna a columna con tu `Usuario.java`. Detectas discrepancias que causan el error `ddl-auto: validate fails`.

**Analizar datos de prueba:** Ver `View/Edit Data` de la tabla `venta` después de ejecutar un test, verificar que los campos calculados (total, subtotal) tienen los valores correctos.

---

## 7. DESVENTAJAS, RIESGOS Y LIMITACIONES

### Problemas comunes de conexión en tu configuración

**Problema 1: Usar puerto 5432 en lugar de 5433**
Tu `.env` usa `DB_PORT_EXTERNAL=5433`. Si alguien intenta conectar pgAdmin a `localhost:5432`, falla porque ese puerto no está mapeado.

Verificación: `docker compose ps` debe mostrar `127.0.0.1:5433->5432/tcp`.

**Problema 2: Intentar conectar cuando el contenedor no está corriendo**
pgAdmin guarda la configuración de conexión pero no puede conectar si el contenedor está detenido.

Síntoma: "Connection refused" o timeout.
Solución: `docker compose up` primero, luego abrir pgAdmin.

**Problema 3: pgAdmin como contenedor en la misma red - host incorrecto**
Si decides agregar pgAdmin como servicio Docker en `proveperu-network`, el host cambia a `database` (nombre del servicio) y el puerto a `5432` (interno). Muchos confunden esto con la configuración de pgAdmin externo.

**Problema 4: Contraseña con caracteres especiales en .env**
Si `POSTGRES_PASSWORD` tiene caracteres como `@`, `#`, `$`, pueden interpretarse de forma incorrecta en el `.env`. Usa comillas si es necesario.

### Riesgos de exponer el puerto 5432/5433

Tu configuración expone el puerto `5433` en `0.0.0.0` (todas las interfaces de red del host). En un entorno local de desarrollo esto es aceptable. Los riesgos a tener en cuenta:

| Riesgo | Nivel en desarrollo local | Mitigación |
|---|---|---|
| Acceso no autorizado desde la red local | Bajo (es tu laptop en tu red) | Usar contraseña fuerte en `.env` |
| Exposición accidental en red pública (café, universidad) | Medio | Cambiar a `127.0.0.1:5433:5432` en docker-compose para limitar a localhost |
| Credenciales en `.env` sin protección | Bajo si el `.env` no está en Git | Verificar que `.env` está en `.gitignore` |

---

## 8. PUNTOS CRÍTICOS Y BUENAS PRÁCTICAS

### Lista de verificación antes de conectar

```
□ docker compose ps → proveperu-database aparece como "running"
□ docker compose ps → el puerto muestra 0.0.0.0:5433->5432/tcp
□ docker compose logs database → no hay errores de inicialización
□ Tu .env tiene los valores correctos de POSTGRES_USER y POSTGRES_PASSWORD
□ pgAdmin tiene configurado: host=127.0.0.1, port=5433
```

### Errores críticos que debes evitar

| Error | Consecuencia | Cómo evitarlo |
|---|---|---|
| Modificar tablas directamente en pgAdmin sin crear migration | El cambio no existe para el equipo - se pierde al recrear el volumen | Todo cambio de schema → nuevo archivo en `migrations/` |
| Ejecutar `DELETE` directo en pgAdmin en tablas de negocio | Viola la regla de soft delete | Usar `UPDATE estado = 'INACTIVO'`, no DELETE |
| Borrar el volumen con `-v` en entorno con datos reales | Pérdida total de datos | Hacer backup primero, confirmar con el equipo |
| Compartir capturas de pantalla de pgAdmin con datos sensibles | Exposición de información del cliente | Usar datos de prueba ficticios en desarrollo |
| Ejecutar migrations en desorden | El schema queda inconsistente | Siempre ejecutar en orden numérico ascendente | 

### Configuración realizada que se hizo en el docker-compose.yml

**Decisión: pgAdmin instalado localmente en cada laptop (no como contenedor Docker).**

Razón: Cada desarrollador gestiona su propia conexión a su contenedor local. No hay conflictos entre integrantes. Es más simple de configurar en Fase 0.

---

## RESUMEN

**¿Qué necesitas para conectar pgAdmin?**

```
1. docker compose up (contenedor database corriendo)
2. pgAdmin configurado con:
   Host:     127.0.0.1
   Port:     5433           ← DB_PORT_EXTERNAL del .env
   Database: proveperu_db   ← POSTGRES_DB
   User:     proveperu_user ← POSTGRES_USER
   Password: [tu contraseña del .env]
```

**¿Qué hace Docker con tus scripts?**
- `database/init/` → ejecutado automáticamente UNA VEZ (cuando el volumen está vacío)
- `database/migrations/` → ejecutado manualmente por ti desde pgAdmin
- `database/scripts/` → ejecutado manualmente cuando lo necesites

**Regla de oro:**
Si quieres datos persistentes → `docker compose down` (sin `-v`)
Si quieres empezar desde cero → `docker compose down -v` (los scripts de init/ se re-ejecutan)

---