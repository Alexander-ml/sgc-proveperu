# GITHUB — GUÍA COMPLETA DE TRABAJO PARA CADA INTEGRANTE

## Objetivo General

Organizar el trabajo del equipo para que:

- varias personas trabajen al mismo tiempo
- no se sobrescriban cambios
- no se rompa el sistema
- exista historial ordenado
- todo cambio sea revisado
- el proyecto avance profesionalmente

## Configuración Inicial (una sola vez)

Cada integrante ejecutar una vez:

```bash
git config --global user.name "Tu Nombre"
git config --global user.email "correo@ejemplo.com"
```

### Configuración de clave SSH
Cada integrante debe realizar este proceso una sola vez en su equipo antes de clonar el repositorio.
Pasos
1. ####  Generar clave SSH
```bash
ssh-keygen -t ed25519 -C "tu_email"
``` 
Presionar ENTER a todas las opciones.

2. #### Copiar la clave pública
```bash
cat ~/.ssh/id_ed25519.pub
```

Copiar todo el contenido.

3. #### Agregar clave en GitHub
GitHub → Settings → SSH and GPG keys → New SSH key

Pegar la clave copiada y guardar

4. #### Verificar conexión
```bash
ssh -T git@github.com
```

Resultado esperado:

Hi TU-USUARIO! You've successfully authenticated

#### Resultado
- Acceso a repositorios sin credenciales
- Eliminación de errores de autenticación
- Configuración lista para clonar con SSH

## Estructura de Ramas

```text
main          ← Código estable y listo para presentar
develop       ← Rama principal de trabajo del equipo

feature/*     ← Nuevas funcionalidades
fix/*         ← Corrección de errores
hotfix/*      ← Corrección urgente en main
docs/*        ← Documentación
refactor/*    ← Mejoras internas
```

## Explicación de Cada Rama

### `main`

Contiene únicamente código:

* estable
* probado
* aprobado
* listo para demostrar

### `develop`

Es la rama principal de desarrollo.

Aquí se integran los avances de todos.

Debe mantenerse:

* funcional
* sin errores críticos
* ejecutable localmente


### `feature/*`

Rama temporal donde trabaja cada integrante.

Ejemplos:

```text
feature/login-ui
feature/docker-compose
feature/backend-auth
feature/modulo-ventas
```

---

# FLUJO REAL DE TRABAJO DE CADA INTEGRANTE

## Paso 1 — Clonar el repositorio (Solo una vez)

Cuando el integrante entra al proyecto por primera vez:

```bash
git clone git@github.com:Alexander-ml/sgc-proveperu.git
```

Entrar a la carpeta:

```bash
cd sgc-proveperu
```

## Paso 2 — Ver ramas disponibles

```bash
git branch -a
```

Verá algo como:

```text
main
remotes/origin/main
remotes/origin/develop
```

## Paso 3 — Entrar a develop

```bash
git checkout develop
```

## Paso 4 — Antes de empezar cada día

Siempre actualizar:

```bash
git checkout develop
git pull origin develop
```

## ¿Qué hace esto?

| Comando                 | Función                 |
| ----------------------- | ----------------------- |
| git checkout develop    | cambia a develop        |
| git pull origin develop | descarga cambios nuevos |


## Paso 5 — Crear una rama personal para la tarea
### Ejemplo:

```bash
git checkout -b feature/nombre-tarea
```

### Regla Importante

```text
Nunca trabajar directamente en develop.
Nunca trabajar en main.
Siempre en feature/*.
```

## Paso 6 — Ver cambios realizados

```bash
git status
```

Muestra archivos modificados.

## Paso 7 — Guardar cambios en Git

Agregar archivos:

```bash
git add .
```

Guardar commit:

```bash
git commit -m "feat(auth): implementar login JWT"
```


### Commits Profesionales
 
El formato: `tipo(alcance): descripción imperativa breve`
 
**Tipos:**
- `feat` — nueva funcionalidad
- `fix` — corrección de bug
- `refactor` — mejora sin cambio de comportamiento
- `docs` — documentación
- `test` — pruebas
- `chore` — mantenimiento, configuración

**Ejemplos reales para el proyecto:**
- `feat(ventas): implementar validación de stock antes de confirmar venta`
- `fix(caja): corregir cálculo de diferencia en cierre de caja`
- `feat(auth): agregar filtro JWT para validar token en cada petición`
- `refactor(clientes): extraer lógica de validación DNI/RUC a utils`
- `docs(readme): actualizar instrucciones de configuración local`
- `test(ventas): agregar prueba unitaria para StockInsuficienteException`
- `chore(docker): agregar healthcheck al contenedor de base de datos`


## Paso 8 — Subir rama a GitHub

```bash
git push origin feature/nombre-tarea
```

## Paso 9 — Crear Pull Request ir a GitHub

Ir a GitHub y crear PR:

```text
feature/nombre-tarea → develop
```

### Qué debe escribir en el Pull Request

#### Título

```text
Implementar login frontend
```

#### Descripción

```text
- Se creó pantalla login
- Validación básica formulario
- Conectado con endpoint auth
- Issue #12
```

## Paso 10 — Revisión del Equipo

Otro integrante revisa:

* código limpio
* sin errores
* funciona localmente
* no rompe otros módulos

Si está correcto: `Approve`

## Paso 11 — Merge

Se integra a:

```text
develop
```

### Luego eliminar rama

Elimina la rama en tu máquina
```bash
git branch -d feature/nombre-tarea
```

Elimina la rama en GitHub
```bash
git push origin --delete feature/nombre-tarea
```

---

# Qué Está Prohibido

```text
push directo a main
push directo a develop
commits llamados "cambios"
trabajar todos en misma rama
borrar ramas ajenas
merge sin revisar
```

# Ejemplo Completo Real

```bash
git checkout develop
git pull origin develop
git checkout -b feature/modulo-clientes-ui
git add .
git commit -m "feat(clientes): crear listado inicial"
git push origin feature/modulo-clientes-ui
```

Luego PR a develop.