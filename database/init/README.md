# Init Scripts

Contiene el script SQL que Docker ejecuta automáticamente
al crear el contenedor por primera vez.

- Solo se ejecuta cuando el volumen de datos está vacío.
- Orden de ejecución: alfabético por nombre de archivo.
- Convención: 01_schema.sql, 02_datos_maestros.sql

⚠️ NO modificar scripts ya ejecutados. Usar migrations/ para cambios.