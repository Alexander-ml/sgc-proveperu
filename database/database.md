# Arquitectura de la carpeta `database/` en PostgreSQL
## Función de la Carpeta `database/`
 
La carpeta database/ centraliza todos los componentes relacionados con la base de datos dentro del sistema. Su propósito es garantizar que el entorno de datos sea reproducible, versionado, consistente y controlado en equipo.
 
## Estructura y función de cada componente `database/`

| Carpeta       | Función principal         | Rol en el sistema                       |
| ------------- | ------------------------- | --------------------------------------- |
| `init/`       | Inicialización automática | Define el estado base del sistema       |
| `migrations/` | Versionado del schema     | Controla cambios estructurales          |
| `scripts/`    | Datos y utilidades        | Soporte para pruebas y validación       |
| `backups/`    | Respaldo de datos         | Recuperación ante fallos                |
| `config/`     | Configuración del motor   | Ajuste del comportamiento de PostgreSQL |

```
database/
├── init/
├── migrations/
├── scripts/
├── backups/
└── config/
```

 
### `init/ - Inicialización del sistema`

**Qué contiene:** Contiene scripts SQL que se ejecutan automáticamente al crear el contenedor por primera vez.

Define:
- Tablas
- Relaciones (FK)
- Índices
- Datos iniciales (roles, usuario admin)

Se ejecuta solo cuando el volumen está vacío. 

**Por qué es crítico:** Garantiza que todos los entornos (desarrollo, pruebas) comiencen con exactamente la misma estructura y datos base.

---
 
### `migrations/ - Evolución del schema`
 
**Qué contiene:** Contiene scripts SQL que se ejecutan automáticamente al crear el contenedor por primera vez.

Define:
- Tablas
- Relaciones (FK)
- Índices
- Datos iniciales (roles, usuario admin)

Se ejecuta solo cuando el volumen está vacío
 
**Formato de nombre:** `V002__agregar_columna_descuento_venta.sql`, `V003__crear_tabla_categoria.sql`. El número garantiza el orden de aplicación.
 
**Cómo funciona en equipo:** Garantiza que todos los entornos (desarrollo, pruebas) comiencen con exactamente la misma estructura y datos base.
 
**Regla de oro:** Nadie modifica directamente la base de datos de otro integrante. Todo cambio de schema se documenta como script de migración. Esta disciplina es la diferencia entre un equipo que se integra sin problemas y uno que pierde tiempo sincronizando bases de datos.
 
---
 
### `scripts/ - Datos y utilidades`
 
**Qué contiene:** Incluye scripts que no modifican la estructura, sino los datos.

- Datos de prueba (seed data)
- Scripts de limpieza
- Consultas de diagnóstico
 
El más importante: `seed_data.sql` con datos de prueba realistas. 
- Productos reales de ProvePeru (espumas, pinturas, tapizones, tornillos), 
- Clientes de ejemplo con DNI y RUC válidos, proveedores reales (Paraíso, Anypsa, Aceros Arequipa)
- Ventas de ejemplo para que los módulos de análisis tengan algo que mostrar.
 
**Importancia técnica:**
Facilita pruebas funcionales y validación del sistema sin afectar el diseño estructural.
 
---
 
### `backups/ - Respaldo`
 
**Qué contiene:** Dumps de la base de datos generados durante el desarrollo.
 
**Importante:** Esta carpeta debe estar en el `.gitignore`. Los dumps pueden contener datos reales de prueba que no deben subir al repositorio público.
 
---
 
### `config/ - Configuración del motor`
 
**Qué contiene:** El archivo `postgresql.conf` con parámetros del motor: 
- timezone
- codificación 
- límite de conexiones 
- configuración de logging

Para entorno local con Docker, la mayoría de defaults son aceptables. Lo que sí debe configurarse explícitamente es la timezone.
 
---
 
## Cómo Trabajar con la Base de Datos
 
### Crear o Modificar Tablas
 
Todo cambio de schema sigue el mismo flujo:
 
1. Escribir el SQL de la nueva tabla, columna, vista, función o trigger.
2. Probarlo en la base de datos local contra el contenedor Docker (usando pgAdmin o ejecutando el SQL en el contenedor).
3. Verificar que el cambio funciona como se espera.
4. Crear un nuevo archivo de migración numerado en `database/migrations/`.
5. Hacer commit del archivo.
6. Los demás integrantes descargan el cambio y lo aplican en su base de datos.
Nunca hacer cambios directamente en la base de datos sin documentarlos como migración. Lo que no está en un script de migración no existe para el equipo.
 
---
 
#### Crear `Vistas`
 
Las vistas son consultas SQL almacenadas que permiten abstraer lógica compleja de acceso a datos.

Se utilizan principalmente en:
Reportes
Dashboards
KPIs del negocio
Encapsulan múltiples JOIN, filtros y agregaciones en una sola estructura reutilizable.

**Importancia técnica:**
Permiten simplificar el backend, evitando repetir consultas complejas y mejorando la mantenibilidad.

**Ubicación:**
Siempre en `migrations/`, ya que forman parte del esquema lógico.
 
---
 
#### Crear `Funciones` y `Procedimientos Almacenados`
 
Son bloques de lógica ejecutados directamente dentro de PostgreSQL.

**Funciones:** Retornan un valor
- Se usan para:
  - Cálculos (totales, impuestos, etc.)
  - Validaciones

**Procedimientos almacenados:** No necesariamente retornan valor
- Se usan para:
  - Procesos completos (ej: registrar una venta).


**Importancia técnica:**
Reducen la carga del backend en operaciones intensivas y permiten ejecutar lógica cerca de los datos.

**Buenas prácticas:**
Usarlos solo cuando aporten valor real
Mantener la lógica principal en el backend (más fácil de testear y mantener)

**Ubicación:**
Siempre en migrations/.
 
---
 
#### Crear `Triggers`
 
Los triggers son mecanismos que ejecutan acciones automáticamente ante eventos en la base de datos:

- `INSERT`
- `UPDATE`
- `DELETE`

**Casos de uso comunes:**

- Actualizar automáticamente `fecha_hora_actualizacion`
- Registrar auditoría de cambios
- Validaciones adicionales a nivel de base de datos

**Importancia técnica:**

- Garantizan consistencia y automatización sin depender del backend.

**Riesgos:**

- Pueden generar errores difíciles de rastrear
Aumentan la complejidad del sistema

**Recomendación:**

- Usarlos con moderación y solo para lógica crítica.

**Ubicación:**

- Siempre en migrations/ (incluyendo la función del trigger).
 
---
 
#### Crear `Índices`
Los índices son estructuras que optimizan la velocidad de las consultas.

**Dónde aplicarlos:**
- Columnas usadas en:
  - WHERE
  - JOIN
  - ORDER BY

**Importancia técnica:**
- Sin índices, el rendimiento degrada significativamente a medida que crecen los datos. 

---

### Convención de nombres para archivos SQL en `database/`
| Carpeta       | Formato de nombre             | Ejemplo                          | Regla clave                       |
| ------------- | ----------------------------- | -------------------------------- | --------------------------------- |
| `init/`       | `NN_nombre_descriptivo.sql`   | `01_schema_inicial.sql`          | Orden lógico de ejecución inicial |
| `migrations/` | `VNNN__descripcion_clara.sql` | `V002__crear_tabla_producto.sql` | Versionado secuencial obligatorio |
| `scripts/`    | `accion_objetivo.sql`         | `seed_data.sql`                  | Nombre funcional, no versionado   |
| `backups/`    | `backup_fecha.sql`            | `backup_2026_05_03.sql`          | Basado en fecha para trazabilidad |
| `config/`     | nombre estándar del motor     | `postgresql.conf`                | No se renombra arbitrariamente    |

### Aplicado a tus componentes
| Componente         | Nombre recomendado (migrations/)         | Ejemplo                                        |
| ------------------ | ---------------------------------------- | ---------------------------------------------- |
| **Vistas (Views)** | `VNNN__crear_view_<nombre>.sql`          | `V005__crear_view_reporte_ventas.sql`          |
| **Funciones**      | `VNNN__crear_funcion_<nombre>.sql`       | `V006__crear_funcion_calcular_total.sql`       |
| **Procedimientos** | `VNNN__crear_procedimiento_<nombre>.sql` | `V007__crear_procedimiento_procesar_venta.sql` |
| **Triggers**       | `VNNN__crear_trigger_<nombre>.sql`       | `V008__crear_trigger_auditoria_venta.sql`      |
| **Índices**        | `VNNN__crear_indice_<tabla>.sql`         | `V009__crear_indice_venta_fecha.sql`           |

### Reglas clave
| Regla                           | Descripción                                    |
| ------------------------------- | ---------------------------------------------- |
| **Prefijo secuencial (`V001`)** | Garantiza orden de ejecución                   |
| **Doble guion bajo (`__`)**     | Separador estándar entre versión y descripción |
| **Descripción clara**           | Indica exactamente qué hace el script          |
| **Un cambio por archivo**       | Facilita control y rollback                    |
| **Sin abreviaciones ambiguas**  | Mejora comprensión en equipo                   |
