# Migrations

Scripts SQL numerados secuencialmente para cambios de schema
después de la inicialización.

Formato: V{número}__{descripción_snake_case}.sql
Ejemplo: V002__agregar_columna_descuento_venta.sql

Regla: NUNCA modificar scripts existentes.
Siempre crear uno nuevo con el número siguiente.