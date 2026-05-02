DATABASE
 
### Función de la Carpeta `database/`
 
Centraliza todo lo relacionado con la base de datos: el script de inicialización que se ejecuta automáticamente con Docker, los scripts de cambios al schema, las utilidades de prueba, los respaldos y la configuración del motor.
 
---
 
### Por Qué PostgreSQL
 
**ACID garantizado:** Cada operación que afecta múltiples tablas al mismo tiempo (registrar una venta que descuenta stock y actualiza la caja) ocurre como una unidad atómica. Si algo falla en el medio, todo se revierte. La base de datos siempre está en un estado consistente.
 
**Integridad referencial real:** Las claves foráneas son efectivas. No puede existir un `detalle_venta` que referencie una venta inexistente. La base de datos rechaza operaciones inválidas.
 
**Tipos de dato apropiados para dinero:** `NUMERIC(12,2)` para montos monetarios sin errores de coma flotante. `TIMESTAMP` con soporte de timezone. `CHECK constraints` para validar dominios en el motor.
 
**Capacidad para consultas analíticas:** Las consultas del dashboard y los reportes internos de cada módulo involucran múltiples tablas, funciones de agregación y filtros temporales. PostgreSQL optimiza estas consultas automáticamente usando los índices disponibles.
 
**Gratuito y maduro:** Sin costos de licencia. 30+ años de desarrollo activo. La documentación oficial es una de las mejores de cualquier tecnología de base de datos.
 
---
 
### Estructura de la Carpeta `database/`
 
```
database/
├── init/
├── migrations/
├── scripts/
├── backups/
└── config/
```
 
---
 
#### `init/`
 
**Qué contiene:** El script SQL que Docker ejecuta automáticamente la primera vez que crea el contenedor de PostgreSQL. Solo se ejecuta una vez, cuando el volumen de datos está vacío.
 
**Qué debe hacer:** Crear todas las tablas en el orden correcto (primero las tablas sin dependencias, luego las que las referencian con FK), crear los índices necesarios, insertar los datos maestros iniciales (roles, permisos, asignaciones rol-permiso, métodos de pago, tipos de movimiento), e insertar el usuario administrador inicial con contraseña hasheada en BCrypt.
 
**Por qué es crítico:** Es la fuente de verdad del estado inicial del sistema. Si un integrante elimina su contenedor y lo recrea, el sistema debe quedar exactamente igual que la primera vez. Sin este script, cada integrante tiene su propia versión de la base de datos.
 
---
 
#### `migrations/`
 
**Qué contiene:** Scripts SQL numerados secuencialmente que registran cada cambio al schema después de la inicialización.
 
**Formato de nombre:** `V002__agregar_columna_descuento_venta.sql`, `V003__crear_tabla_categoria.sql`. El número garantiza el orden de aplicación.
 
**Cómo funciona en equipo:** Cuando un integrante necesita cambiar el schema (nueva columna, nueva tabla, nuevo índice), crea un nuevo script de migración con el número siguiente. Hace commit. Los demás integrantes descargan el cambio y aplican el script en su base de datos local. Todos terminan con exactamente el mismo schema.
 
**Regla de oro:** Nadie modifica directamente la base de datos de otro integrante. Todo cambio de schema se documenta como script de migración. Esta disciplina es la diferencia entre un equipo que se integra sin problemas y uno que pierde tiempo sincronizando bases de datos.
 
---
 
#### `scripts/`
 
**Qué contiene:** SQL de utilidad que no modifica el schema.
 
El más importante: `seed_data.sql` con datos de prueba realistas. Productos reales de ProvePeru (espumas, pinturas, tapizones, tornillos), clientes de ejemplo con DNI y RUC válidos, proveedores reales (Paraíso, Anypsa, Aceros Arequipa), ventas de ejemplo para que los módulos de análisis tengan algo que mostrar.
 
También incluye: consultas de diagnóstico para verificar el estado del sistema, scripts de limpieza para resetear datos de prueba, y consultas de verificación de integridad.
 
---
 
#### `backups/`
 
**Qué contiene:** Dumps de la base de datos generados durante el desarrollo.
 
**Importante:** Esta carpeta debe estar en el `.gitignore`. Los dumps pueden contener datos reales de prueba que no deben subir al repositorio público.
 
---
 
#### `config/`
 
**Qué contiene:** El archivo `postgresql.conf` con parámetros del motor: timezone, codificación, límite de conexiones, configuración de logging. Para entorno local con Docker, la mayoría de defaults son aceptables. Lo que sí debe configurarse explícitamente es la timezone.
 
---
 
### Cómo Trabajar con la Base de Datos
 
#### Crear o Modificar Tablas
 
Todo cambio de schema sigue el mismo flujo:
 
1. Escribir el SQL de la nueva tabla, columna, vista, función o trigger.
2. Probarlo en la base de datos local contra el contenedor Docker (usando pgAdmin o ejecutando el SQL en el contenedor).
3. Verificar que el cambio funciona como se espera.
4. Crear un nuevo archivo de migración numerado en `database/migrations/`.
5. Hacer commit del archivo.
6. Los demás integrantes descargan el cambio y lo aplican en su base de datos.
Nunca hacer cambios directamente en la base de datos sin documentarlos como migración. Lo que no está en un script de migración no existe para el equipo.
 
---
 
#### Crear Vistas
 
Las vistas van en un script de migración como cualquier otro cambio. Son útiles para encapsular consultas analíticas complejas que el backend necesita frecuentemente, especialmente para los dashboards con KPIs del negocio.
 
---
 
#### Crear Funciones y Procedimientos Almacenados
 
Funcionan para lógica que se ejecuta frecuentemente en SQL puro (validaciones complejas, cálculos que JPA no optimiza bien). También van en un script de migración con su número secuencial.
 
Usarlos con criterio: la mayor parte de la lógica de negocio debe estar en los servicios Java donde es testeable. Los procedimientos almacenados son difíciles de probar y de versionar.
 
---
 
#### Crear Triggers
 
Los triggers son útiles para: actualizar `fecha_hora_actualizacion` automáticamente al modificar un registro, o registrar movimientos de auditoría directamente en la base de datos. Van en un script de migración.
 
Usarlos con moderación. Un trigger que falla genera un error de base de datos que es difícil de diagnosticar desde la aplicación.
 
---
 
#### Crear Índices
 
Los índices van en los scripts de migración. Las prioridades de indexación para este sistema:
 
- `fecha_hora_venta` en la tabla de ventas (filtros temporales en dashboards)
- `id_cliente` en ventas (historial del cliente)
- `id_producto` en detalle de ventas, compras y movimientos de inventario
- `usuario_login` en usuarios (autenticación)
- `id_caja` en movimientos de caja
- `estado` en tablas con soft delete (para filtrar registros activos eficientemente)
---
 
#### Verificar Cambios con pgAdmin
 
pgAdmin se conecta al contenedor de PostgreSQL en `localhost:5432`. Sirve para:
- Ver visualmente el schema y verificar que las tablas se crearon con la estructura correcta.
- Ejecutar el script de migración manualmente y ver si genera errores.
- Consultar datos de prueba para verificar que los cambios funcionan.
- Ver el plan de ejecución de consultas complejas (EXPLAIN ANALYZE).
---
 
### Buenas Prácticas de Base de Datos
 
**Naming conventions:** `snake_case` en español para todo. Tablas en singular: `venta`, `detalle_venta`, `movimiento_inventario`. Columnas descriptivas: `fecha_hora_creacion`, `id_usuario_creador`. Índices con prefijo `idx_`: `idx_venta_fecha`, `idx_producto_estado`. Claves foráneas con nombre explícito: `fk_detalle_venta_producto`.
 
**Claves primarias:** Siempre `GENERATED ALWAYS AS IDENTITY`. Nunca usar datos del negocio (DNI, RUC, código) como PK porque pueden cambiar.
 
**Claves foráneas:** Siempre declaradas explícitamente con `FOREIGN KEY`. Sin ellas, la base de datos no puede garantizar integridad referencial.
 
**Constraints:** Implementar todos los `CHECK` constraints del diccionario de datos. Son la última línea de defensa contra datos inválidos. Un `CHECK (estado IN ('ACTIVO','INACTIVO'))` evita que ninguna herramienta, script, o bug de aplicación inserte un estado inválido.
 
**Auditoría:** Todas las entidades principales deben tener `fecha_hora_creacion`, `fecha_hora_actualizacion`, `id_usuario_creador`, `id_usuario_actualizo`. 
 
**Soft Delete:** Nunca eliminar registros de forma física. Un producto descontinuado se marca `estado = 'INACTIVO'`. Un cliente dado de baja se desactiva. Esto preserva la integridad del historial — las ventas pasadas siguen referenciando datos válidos.
 
**Timestamps:** Siempre `TIMESTAMP` (no `DATE` a menos que la hora no importe), con default `CURRENT_TIMESTAMP`. Configurar la timezone del contenedor PostgreSQL como `America/Lima` para que los timestamps reflejen la hora peruana.
 
**Rendimiento:** Los índices son obligatorios en columnas que aparecen en `WHERE`, `JOIN` y `ORDER BY` de consultas frecuentes. Sin índices, las consultas de dashboard se vuelven lentas cuando hay miles de registros.
 
**UTF-8:** Crear la base de datos con `ENCODING = 'UTF8'`. Sin esto, los nombres con tildes (tapizón, diseño) se corrompen al guardarse.