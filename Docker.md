# Guía Operativa de Docker para el Equipo

## ANTES DE EMPEZAR — LEE ESTO

Este documento asume que:
- El proyecto ya está clonado en tu laptop
- Tu archivo `.env` ya está configurado (copiado desde `.env.example`)
- Docker Desktop está instalado y **corriendo** (el ícono de Docker debe aparecer activo en la barra de tareas)

Si no tienes el `.env` configurado, pídelo a quien lo tenga antes de continuar.

## 1. ARRANCAR EL PROYECTO
### Requisitos mínimos

| Requisito | Cómo verificar |
|---|---|
| Docker Desktop instalado | Abre una terminal y escribe `docker --version` |
| Docker corriendo | El ícono de Docker en la barra de tareas está activo (no dice "starting") |
| Archivo `.env` en la raíz | Debe existir `sgc-properu/.env` |
| Terminal abierta en la raíz | La carpeta activa debe ser `sgc-properu/` |
---

### Paso 1 — Primera vez o cuando cambia el código

Este comando construye las imágenes y levanta todos los servicios:

```bash
docker compose up --build
```

Úsalo cuando:
- Es la primera vez que levantas el proyecto
- Alguien cambió código del backend o frontend
- Actualizaste dependencias (`pom.xml` o `package.json`)
- Cambiaste los `Dockerfile`

**Qué hace:** Descarga las imágenes base, compila el backend con Maven, compila el frontend con Vite, y levanta los 3 contenedores.

---

### Paso 2 — Días normales sin cambios de código

Cuando nadie cambió código, usa:

```bash
docker compose up
```

Úsalo cuando:
- Ya construiste las imágenes antes
- Solo quieres levantar el sistema para trabajar
- Es el inicio del día de trabajo

**Qué hace:** Levanta los contenedores usando las imágenes que ya existen, sin recompilar nada.

---

### Qué deberías ver en consola si todo va bien

Los mensajes aparecen mezclados de los 3 servicios. Busca estas líneas clave:

**Base de datos lista:**
```
proveperu-database  | database system is ready to accept connections
```

**Backend iniciado:**
```
proveperu-backend   | Started BackendApplication in X seconds
```

**Frontend listo:**
```
proveperu-frontend  | /docker-entrypoint.sh: Configuration complete; ready for start up
```

Cuando veas los 3, el sistema está listo para usar.

---

### Cómo detener el sistema correctamente

Siempre detén con este comando (desde la misma terminal o una nueva en la misma carpeta):

```bash
docker compose down
```

**Nunca cierres la terminal directamente.** Los datos de la base quedan guardados en el volumen, pero los contenedores no se detienen limpiamente.

---

## 2. COMANDOS BÁSICOS DEL EQUIPO

### Ver qué contenedores están corriendo

```bash
docker compose ps
```

**Cuándo usarlo:** Cuando quieres saber si los servicios están activos o detenidos.

**Resultado esperado:**
```
NAME                    STATUS          PORTS
proveperu-backend       running         0.0.0.0:8080->8080/tcp
proveperu-database      running         0.0.0.0:5432->5432/tcp
proveperu-frontend      running         0.0.0.0:80->80/tcp
```

Si alguno dice `exited` o no aparece, ese servicio falló.

---

### Ver logs de un servicio específico

```bash
docker compose logs backend
docker compose logs frontend
docker compose logs database
```

**Ver logs en tiempo real (modo seguimiento):**
```bash
docker compose logs -f backend
```

Para salir del modo seguimiento: `Ctrl + C`

**Cuándo usarlo:** Cuando un servicio no responde o hay un error. Los logs del backend son los más útiles para diagnosticar problemas.

**Ver solo las últimas 50 líneas:**
```bash
docker compose logs --tail=50 backend
```

---

### Reiniciar un servicio sin apagar todo

```bash
docker compose restart backend
docker compose restart frontend
docker compose restart database
```

**Cuándo usarlo:** Cuando un servicio se colgó o se comporta raro pero los demás están bien.

---

### Reconstruir imágenes (cuando hay cambios de código)

```bash
docker compose up --build
```

**Cuándo usarlo:** Siempre que alguien del equipo suba cambios al backend o frontend. Si no usas `--build`, los cambios no se aplican.

---

### Borrar TODO y empezar de cero

```bash
docker compose down -v
```

**CUIDADO:** El `-v` elimina los volúmenes, lo que borra los datos de la base de datos. Usa esto solo cuando:
- El sistema está completamente roto y nada funciona
- Quieres resetear la base de datos completamente
- Te lo indica el Tech Lead

Después de este comando, levanta de nuevo con:
```bash
docker compose up --build
```

---

### Ver cuántos recursos usa cada contenedor

```bash
docker stats
```

Para salir: `Ctrl + C`

---

## 3. VALIDACIÓN DEL SISTEMA

### Cómo saber que todo funciona

Después de levantar el sistema con `docker compose up`, verifica estos 3 puntos en orden:

#### 1. Base de datos

```bash
docker compose ps
```

La base de datos debe aparecer como `running`.

Si ves `running`, la base de datos está activa. Si ves `exited`, revisa los logs:
```bash
docker compose logs database
```

#### 2. Backend 

Abre el navegador y ve a:

```
http://localhost:8080/actuator/health
```

**Resultado esperado:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

**Cómo interpretar:**
- `"status": "UP"` → El backend está corriendo correctamente
- `"db": { "status": "UP" }` → El backend se conectó a la base de datos exitosamente
- Si ves `"status": "DOWN"` → Hay un problema, revisa los logs del backend
- Si no carga la página → El backend no arrancó, revisa los logs

---

#### 3. Frontend

Abre el navegador y ve a:

```
http://localhost
```

o equivalente:

```
http://localhost:80
```

**Resultado esperado:** Debe cargar la página del sistema (aunque esté incompleta o sea solo una pantalla base).

**Si no carga:**
- Espera 30 segundos más y recarga
- Revisa los logs: `docker compose logs frontend`
- Verifica que el contenedor esté corriendo: `docker compose ps`

### Tabla de validación rápida

| Qué verificar | URL | Resultado esperado |
|---|---|---|
| Backend directo | `http://localhost:8080/actuator/health` | `{"status":"UP"}` |
| Frontend | `http://localhost` | Página carga |
| Base de datos | `docker compose ps` | `database` en estado `running` |

---

## 4. FLUJO DE TRABAJO DIARIO

Sigue este flujo **todos los días** sin excepción.

### Inicio del día

| Paso | Acción | Comando |
|---|---|---|
| 1 | Abre Docker Desktop y espera que inicie | — |
| 2 | Abre la terminal en `sgc-proveperu/` | `cd ruta/sgc-proveperu` |
| 3 | Baja los últimos cambios del repositorio |`git checkout develop` / `git pull origin develop` |
| 4 | Decide si alguien cambió código | (ver tabla abajo) |
| 5 | Levanta el sistema | ver comando según caso |
| 6 | Valida que todo funciona | Revisa los 3 puntos del paso anterior |
| 7 | Comienza a trabajar | — |

---

### ¿Con `--build` o sin `--build`?

| Situación | Comando a usar |
|---|---|
| Inicio del día, nadie cambió código | `docker compose up` |
| Alguien cambió el backend (Java) | `docker compose up --build` |
| Alguien cambió el frontend (React) | `docker compose up --build` |
| Cambiaron el `pom.xml` o `package.json` | `docker compose up --build` |
| Cambiaron un `Dockerfile` | `docker compose up --build` |
| Cambiaron solo el `application.yml` | `docker compose up --build` |
| Cambiaron solo `.env` | `docker compose down` y luego `docker compose up` |
| Cambiaron solo un archivo de la carpeta `database/` | `docker compose up` (sin build) |

**Regla simple para el equipo:** Si tienes duda, usa `--build`. Tarda más pero asegura que los cambios se aplican.

---

### Durante el día (trabajo en módulo)

Cuando trabajas en el backend con IntelliJ (modo dev):
- No necesitas reconstruir Docker con cada cambio pequeño
- Spring DevTools reinicia automáticamente el backend cuando detecta cambios
- Si el reinicio automático no funciona, usa: `docker compose restart backend`

Cuando trabajas en el frontend con VS Code (modo dev):
- Vite tiene hot reload, los cambios se reflejan solos en el navegador
- No necesitas reiniciar Docker

---

### Fin del día

| Paso | Acción | Comando |
|---|---|---|
| 1 | Sube tus cambios a Git | `git add .` → `git commit -m "..."` → `git push` |
| 2 | Detén los contenedores | `docker compose down` |
| 3 | Cierra Docker Desktop | — |

---

## 5. ERRORES COMUNES

### Error 1 — Puerto ya en uso

**Síntoma en consola:**
```
Error response from daemon: Ports are not available: address already in use
```
o
```
Bind for 0.0.0.0:8080 failed: port is already allocated
```

**Causa probable:** Otro proceso en tu laptop está usando el puerto 8080, 5432 o 80. Puede ser otro proyecto Docker, PostgreSQL instalado localmente, o un servidor web.

**Solución:**
```bash
docker compose down
```
Luego verifica qué usa ese puerto:

En Windows (PowerShell):
```powershell
netstat -ano | findstr :8080
```

En Mac/Linux:
```bash
lsof -i :8080
```

Cierra el proceso que usa el puerto y vuelve a:
```bash
docker compose up --build
```

---

### Error 2 — El .env no existe o tiene errores

**Síntoma en consola:**
```
variable is not set. Defaulting to a blank string.
```
o el backend muestra error de conexión a la base de datos.

**Causa probable:** El archivo `.env` no existe o le falta una variable.

**Solución:**
1. Verifica que el archivo `.env` existe en la raíz:
```bash
ls -la
```
2. Si no existe, cópialo del ejemplo:
```bash
cp .env.example .env
```
3. Abre `.env` y verifica que todas las variables tienen valor (sin espacios vacíos)
4. Vuelve a levantar:
```bash
docker compose down
docker compose up
```

---

### Error 3 — El backend no conecta a la base de datos

**Síntoma en logs del backend:**
```
Connection refused: database/5432
```
o
```
FATAL: password authentication failed for user
```

**Causa probable A:** La base de datos no terminó de inicializarse antes de que el backend intentó conectarse.

**Solución A:** Espera 30 segundos y revisa si el backend se recuperó solo. Spring Boot reintenta la conexión automáticamente.

**Causa probable B:** Las credenciales en `.env` no coinciden.

**Solución B:** Verifica que `POSTGRES_USER`, `POSTGRES_PASSWORD` y `POSTGRES_DB` en el `.env` sean exactamente los mismos que usa el backend en `application.yml`.

---

### Error 4 — Un contenedor dice "exited"

**Síntoma:**
```bash
docker compose ps
# Muestra: proveperu-backend    exited (1)
```

**Solución — Ver exactamente qué falló:**
```bash
docker compose logs backend
```

Lee el final de los logs. El error suele estar en las últimas 10-20 líneas. Busca palabras como `ERROR`, `Exception`, `Failed`, `FATAL`.

Casos más comunes:
- Error de Java en el backend → error en código o configuración
- Error de conexión → la base de datos no está lista
- Error de puerto → otro proceso usa el mismo puerto

---

### Error 5 — "Cannot connect to Docker daemon"

**Síntoma:**
```
Cannot connect to the Docker daemon at unix:///var/run/docker.sock
```

**Causa:** Docker Desktop no está corriendo.

**Solución:** Abre Docker Desktop, espera a que el ícono deje de girar y muestre "Docker Desktop is running", y vuelve a intentar.

---

### Error 6 — Los cambios en el código no se reflejan

**Síntoma:** Modifiqué el backend pero el sistema sigue igual.

**Causa:** No usaste `--build` al levantar, así que Docker usó la imagen vieja.

**Solución:**
```bash
docker compose down
docker compose up --build
```

---

### Error 7 — "No space left on device"

**Síntoma:**
```
no space left on device
```

**Causa:** Docker acumuló imágenes y contenedores viejos que ocupan espacio.

**Solución — Limpiar recursos sin uso:**
```bash
docker system prune
```

Docker te preguntará si confirmas. Escribe `y` y presiona Enter. Esto NO elimina tus contenedores activos ni los volúmenes con datos.

---

## 6. CHECKLIST FINAL DE VALIDACIÓN

Copia esto y verifica punto por punto antes de decir que el sistema funciona:

```
CHECKLIST — Validación del Sistema SGC-ProvePeru
================================================

□ 1. Docker Desktop está corriendo (ícono activo)

□ 2. Terminal abierta en la carpeta sgc-proveperu/
      Verificación: ls debe mostrar docker-compose.yml

□ 3. Archivo .env existe en la raíz
      Verificación: ls -la debe mostrar .env

□ 4. docker compose up ejecutado sin errores críticos en consola

□ 5. Los 3 contenedores están corriendo
      Verificación: docker compose ps
      ✔ proveperu-database  → running
      ✔ proveperu-backend   → running
      ✔ proveperu-frontend  → running

□ 6. Backend responde correctamente
      Verificación: http://localhost:8080/actuator/health
      ✔ Resultado: {"status":"UP"}

□ 7. Base de datos conectada al backend
      Verificación: http://localhost:8080/actuator/health
      ✔ Resultado: "db": {"status":"UP"}

□ 8. Frontend carga en el navegador
      Verificación: http://localhost
      ✔ La página carga sin error

================================================
Si los 9 puntos tienen ✔ → El sistema está listo para trabajar.
Si alguno falla → Revisa la sección de Errores Comunes.
```

---

## REFERENCIA RÁPIDA — COMANDOS ESENCIALES

```bash
# Levantar (primera vez o con cambios de código)
docker compose up --build

# Levantar (días normales, sin cambios)
docker compose up

# Ver estado de contenedores
docker compose ps

# Ver logs de un servicio
docker compose logs backend
docker compose logs frontend
docker compose logs database

# Ver logs en tiempo real
docker compose logs -f backend

# Reiniciar un servicio
docker compose restart backend

# Detener todo (sin borrar datos)
docker compose down

# Borrar todo incluyendo datos de la base
docker compose down -v

# Limpiar espacio en disco
docker system prune
```