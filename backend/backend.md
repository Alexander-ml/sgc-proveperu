BACKEND
 
### Función de la Carpeta `backend/`
 
Es el servidor de la aplicación. Expone la API REST que el frontend consume, aplica las reglas de negocio, gestiona la seguridad, y persiste los datos en PostgreSQL. Es la capa que sabe qué se puede hacer, quién puede hacerlo, y cómo hacerlo correctamente.
 
---
 
### Por Qué Java
 
**Tipado estático:** Detecta errores en tiempo de compilación. En un sistema que maneja dinero, descubrir errores antes de ejecutar el código es una ventaja real.
 
**Ecosistema empresarial maduro:** Las herramientas de debugging, profiling y testing para Java son las más robustas disponibles.
 
**Rendimiento bajo concurrencia:** La JVM maneja eficientemente múltiples usuarios simultáneos sin configuración adicional.
 
**Estabilidad:** Java es un lenguaje con décadas de soporte activo. Las APIs que escriban hoy seguirán funcionando en versiones futuras sin cambios.
 
---
 
### Por Qué Spring Boot
 
**Autoconfiguración:** Detecta las dependencias del proyecto y configura automáticamente los componentes necesarios. Agregar la dependencia de PostgreSQL es suficiente para que Spring Boot configure el pool de conexiones y el gestor de transacciones.
 
**Contenedor IoC:** Gestiona la creación y ciclo de vida de los objetos. En lugar de que cada clase cree sus dependencias con `new`, Spring las inyecta. Esto hace el código modular, reemplazable y testeable.
 
**Convención sobre configuración:** Reduce las decisiones de configuración que el equipo debe tomar, generando código más homogéneo.
 
**Ecosystem completo:** Spring Security, Spring Data JPA, Actuator, Validation — todo integrado y compatible.
 
---
 
### Rol de Cada Dependencia
 
**Spring Web:** Proporciona el servidor HTTP embebido (Tomcat), el framework MVC para definir controladores REST, la serialización automática de objetos Java a JSON, y el manejo del ciclo de vida de las peticiones. Sin esta dependencia, no hay API.
 
**Spring Data JPA:** Abstrae el acceso a la base de datos. Define interfaces de repositorio y genera automáticamente la implementación SQL. Un método llamado `findByFechaHoraVentaBetween(LocalDateTime inicio, LocalDateTime fin)` genera el SQL correcto sin que el equipo lo escriba. Para consultas más complejas, se escribe JPQL directamente.
 
**Spring Security:** Intercepta cada petición antes de llegar al controlador. Valida el token JWT, carga el usuario y sus permisos en el contexto de seguridad, y aplica las reglas de autorización. Define qué endpoints son públicos (solo login) y cuáles requieren autenticación.
 
**Validation:** Permite definir reglas de validación en los DTOs con anotaciones. Valida automáticamente que los datos de entrada cumplan las reglas antes de pasarlos al servicio. Rechaza datos inválidos con HTTP 400 y mensajes descriptivos por campo.
 
**PostgreSQL Driver:** El conector JDBC que permite a la JVM comunicarse con PostgreSQL en el protocolo de red correcto. Spring Data JPA lo usa transparentemente.
 
**JWT (jjwt):** Genera tokens JWT firmados con HMAC-SHA256 al autenticar al usuario. El token incluye ID de usuario, rol y permisos, y fecha de expiración. El filtro de seguridad valida la firma en cada petición sin consultar la base de datos.
 
**Lombok:** Genera getters, setters, constructores, equals y hashCode mediante anotaciones en tiempo de compilación. Con 20+ entidades en el sistema, Lombok elimina cientos de líneas de código repetitivo que nadie lee pero todos deben mantener.
 
**DevTools:** Solo para desarrollo local. Reinicia el servidor automáticamente al detectar cambios en el código. Nunca debe incluirse en la imagen Docker de demostración o producción.
 
**Actuator:** Expone endpoints de diagnóstico. `/actuator/health` es el que Docker usa como healthcheck para verificar que el backend está listo antes de declararlo disponible.
 
**Testing (JUnit + Mockito + Spring Test):** Pruebas unitarias para los servicios donde vive la lógica de negocio. Verifican que una venta con stock insuficiente lanza la excepción correcta, que el cálculo del total es preciso, que el cierre de caja calcula correctamente la diferencia. Se ejecutan en segundos y detectan regresiones inmediatamente.
 
**MapStruct:** Genera el código de conversión entre entidades JPA y DTOs en tiempo de compilación. Sin MapStruct, el mapeo manual es tedioso y propenso a errores: se agrega un campo nuevo a la entidad, se olvida mapearlo al DTO, y el frontend no lo recibe sin que nadie entienda por qué.
 
---
 
### Estructura del Backend
 
```
backend/src/main/java/com/proyecto/
 
├── shared/          ← Infraestructura compartida por todos los módulos
│   ├── entity/      ← BaseEntity con campos de auditoría
│   ├── dto/         ← ApiResponse<T>, PagedResponse<T>, ErrorResponse
│   ├── util/        ← Formateadores y utilidades genéricas
│   ├── constants/   ← Constantes globales del sistema
│   └── exception/   ← Excepciones personalizadas + GlobalExceptionHandler
├── security/        ← Filtro JWT, SecurityConfig, UserDetailsService
├── config/          ← CORS, BCrypt, OpenAPI/Swagger, Timezone
├── auth/            ← Login, generación de JWT
│   ├── controller/
│   ├── service/
│   └── dto/
├── ventas/          ← Módulo 
├── compras/         ← Módulo 
├── inventario/      ← Módulo 
├── clientes/        ← Módulo 
├── usuarios/        ← Módulo 
└── caja/            ← Módulo 
```
 
---
 
### El Patrón Interno de Cada Módulo
 
Todos los módulos de negocio tienen exactamente la misma estructura interna. Esta consistencia es una de las decisiones más importantes del equipo: cualquier integrante puede abrir cualquier módulo y entender su organización inmediatamente.
 
```
modulo/
├── controller/
├── service/
├── repository/
├── mapper/
├── entity/
└── dto/
    ├── request/
    └── response/
```
 
---
 
#### `entity/`
 
**Qué es:** La clase Java que mapea directamente a una tabla de PostgreSQL usando anotaciones JPA. Cada campo de la clase corresponde a una columna de la tabla. Las relaciones entre entidades se expresan con `@ManyToOne`, `@OneToMany`, `@OneToOne`.
 
**Regla fundamental:** La entidad nunca se envía directamente al frontend. Puede contener campos sensibles (hash de contraseña, referencias circulares que colapsan el serializador JSON). La conversión al DTO de respuesta la hace el mapper.
 
---
 
#### `dto/request/`
 
**Qué es:** El objeto de transferencia que llega del frontend al backend cuando se crea o actualiza un recurso. Contiene exactamente los campos que el frontend envía — ni más ni menos.
 
**Por qué existe separado:** El frontend no debe saber cómo está modelada la base de datos internamente. Los DTOs de request son el contrato entre frontend y backend, independiente del modelo de datos.
 
**Validaciones:** Los DTOs de request tienen las anotaciones de validación (`@NotNull`, `@NotBlank`, `@Min`, `@Size`). Si un campo no cumple la regla, Spring devuelve automáticamente HTTP 400 con el detalle del error por campo.
 
---
 
#### `dto/response/`
 
**Qué es:** El objeto que el backend devuelve al frontend. Contiene exactamente los campos que el frontend necesita mostrar — ni más ni menos.
 
**Por qué existe separado de la entidad:** Permite evolucionar el modelo de datos sin romper el contrato con el frontend, y vice versa. También permite incluir campos calculados (el nombre del cliente en la respuesta de una venta, aunque en la entidad solo está el `id_cliente`).
 
---
 
#### `repository/`
 
**Qué es:** Una interfaz que extiende `JpaRepository`. Spring Data JPA genera la implementación completa en tiempo de ejecución.
 
**Qué contiene:** Solo las consultas personalizadas que van más allá del CRUD estándar (que ya viene gratis con JpaRepository): búsquedas por múltiples campos, filtros por rango de fechas, consultas con agregaciones para los dashboards.
 
---
 
#### `service/`
 
**Qué es:** La capa más importante del sistema. Contiene las reglas de negocio.
 
**Qué hace:** Orquesta los repositorios para ejecutar operaciones complejas. El servicio de ventas, por ejemplo, dentro de una sola transacción: verifica stock, descuenta stock, guarda la venta, guarda el detalle de productos, registra el pago, actualiza la caja, y registra el movimiento de inventario. Si cualquier paso falla, la transacción se revierte completa.
 
**Regla fundamental:** El servicio no sabe nada de HTTP. No maneja `HttpServletRequest` ni construye respuestas HTTP. Solo ejecuta lógica de negocio usando los repositorios. Eso hace que pueda probarse con pruebas unitarias sin levantar un servidor.
 
---
 
#### `controller/`
 
**Qué es:** La puerta de entrada HTTP al módulo. Define los endpoints REST del módulo.
 
**Qué hace:** Recibe la petición, extrae los datos del body o de los parámetros, llama al servicio, y devuelve la respuesta HTTP apropiada.
 
**Cómo debe verse:** Un controlador bien diseñado es delgado. 5 a 10 líneas por método. Si un método del controlador tiene 30 líneas, la lógica de negocio está en el lugar equivocado — debe estar en el servicio.
 
---
 
#### `mapper/`
 
**Qué es:** La clase de MapStruct que convierte entre las representaciones de cada capa.
 
**Qué hace:** `NuevaVentaRequest → Venta` (para crear desde el frontend), `Venta → VentaResponse` (para devolver al frontend). MapStruct genera el código real en tiempo de compilación, no en tiempo de ejecución, por lo que no hay overhead de performance.
 
---
 
### Por Qué Esta Arquitectura
 
**Mantenibilidad:** Cada capa tiene una responsabilidad única. Si cambia la base de datos, solo cambia la entidad y el repositorio. Si cambia lo que el frontend necesita ver, solo cambia el DTO de respuesta y el mapper.
 
**Escalabilidad en equipo:** Cualquier integrante puede trabajar en cualquier módulo sin necesitar entender todos los demás. La estructura es predecible.
 
**Testeabilidad:** Los servicios, que contienen la lógica crítica, pueden probarse con pruebas unitarias sin levantar el servidor ni conectarse a la base de datos. Mockito simula los repositorios.
 
**Seguridad:** Los datos sensibles nunca llegan al frontend porque los DTOs de respuesta solo incluyen lo que se quiere exponer.
 
---