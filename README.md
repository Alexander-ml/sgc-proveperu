# Sistema de Gestión Comercial — ProvePeru S.R.L.

Sistema web para la gestión de ventas, inventario, compras y clientes.
Desarrollado para Inversiones ProvePeru S.R.L., distribuidora de insumos para muebles en Chiclayo, Perú.

## Tecnologías

- **Frontend:** React 18 + Vite + Bootstrap 5 + Nginx
- **Backend:** Java 21 + Spring Boot 3 + Spring Security + JWT
- **Base de datos:** PostgreSQL 16
- **Infraestructura:** Docker + Docker Compose
- **Control de versiones:** GitHub

## Equipo

| Integrante | Rol |
|---|---|
| César Medina | Backend Lead |
| David Sánchez | Backend |
| Iris Arroyo | Frontend Lead |
| Marco Hernandez | Frontend |

## Requisitos Previos

- Docker Desktop instalado y corriendo
- Git instalado
- pgAdmin 4 para administrar la base de datos visualmente (Opcional) 

## Configuración Local

### 1. Clonar el repositorio
`git clone https://github.com/Alexander-ml/sgc-proveperu.git`

`cd sgc-proveperu`

### 2. Crear el archivo de variables de entorno

Copiar el archivo de ejemplo y completar los valores:

`cp .env.example .env`

Editar el archivo .env con los valores correctos para el entorno local.

### 3. Levantar el sistema

`docker compose up --build`

### 4. Verificar que todo funciona

- Frontend: http://localhost
- Backend health: http://localhost:8080/actuator/health
- Base de datos: localhost:5432 (conectar con pgAdmin)

## Estructura del Proyecto

Visualizar en `ARQUITECTURA.md` para la documentación completa

## Ramas de Trabajo

- `main` — Estado entregable. Solo merge desde develop.
- `develop` — Integración continua del equipo.
- `feature/*` — Trabajo individual por funcionalidad.

## Módulos del Sistema

- **Ventas** — Registro y gestión de ventas
- **Compras** — Órdenes de compra y recepciones
- **Inventario** — Control de stock en tiempo real
- **Clientes** — Gestión de clientes y historial
- **Caja** — Control de flujo de efectivo
- **Admin** — Usuarios y roles del sistema