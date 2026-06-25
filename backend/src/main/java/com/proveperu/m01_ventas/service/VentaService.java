package com.proveperu.m01_ventas.service;

import com.proveperu.m01_ventas.dto.request.VentaCreateRequest;
import com.proveperu.m01_ventas.dto.request.VentaFiltroRequest;
import com.proveperu.m01_ventas.dto.response.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Contrato de aplicación del módulo de ventas.
 *
 * <p>
 * Define las operaciones de consulta disponibles para los
 * controladores del módulo de ventas.
 * </p>
 */
public interface VentaService {

    /**
     * Retorna una página de ventas resumidas que cumplen con los filtros indicados.
     *
     * @param filtro parámetros de búsqueda, filtrado, paginación y ordenamiento
     * @return página de ventas resumidas.
     */
    Page<VentaResumenResponseDTO> listarVentas(VentaFiltroRequest filtro);

    /**
     * Obtiene el detalle completo de una venta identificada por su ID.
     *
     * @param idVenta identificador técnico de la venta.
     * @return DTO con toda la información de la venta.
     * @throws com.proveperu.shared.exception.ResourceNotFoundException si no existe.
     * @throws com.proveperu.shared.exception.ValidationException si el ID es inválido.
     */
    VentaDetalleResponseDTO obtenerDetalleVenta(Integer idVenta);

    /**
     * Retorna la lista de clientes activos disponibles
     * para asociar a una nueva venta.
     *
     * <p>
     * Si no hay clientes activos retorna lista vacía.
     * El frontend puede agregar la opción "Sin cliente"
     * de forma independiente.
     * </p>
     *
     * @return lista de clientes activos con datos básicos.
     */
    List<ClienteListadoResponse> obtenerClientesParaVenta();

    /**
     * Retorna los tipos de comprobante disponibles para
     * emitir al confirmar una venta.
     *
     * <p>
     * Se genera a partir del enum {@code TipoComprobante}
     * sin consultas a base de datos.
     * </p>
     *
     * @return lista de tipos de comprobante con código y descripción.
     */
    List<TipoComprobanteResponse> obtenerTiposComprobante();

    /**
     * Retorna los métodos de pago activos disponibles
     * para registrar en una venta.
     *
     * @return lista de métodos de pago habilitados.
     */
    List<MetodoPagoResponse> obtenerMetodosPago();

    /**
     * Busca productos activos cuyo nombre contenga el texto
     * indicado para agregarlos a una nueva venta.
     *
     * <p>
     * Cada resultado incluye el stock actual disponible.
     * La búsqueda es insensible a mayúsculas/minúsculas.
     * </p>
     *
     * @param nombre texto parcial a buscar en el nombre del producto.
     * @return lista de productos que coinciden con el texto de búsqueda.
     */
    List<ProductoVentaResponse> buscarProductosParaVenta(String nombre);

    /**
     * Registra una nueva venta completa de forma transaccional.
     *
     * <p>
     * Orquesta todo el proceso:
     * </p>
     * <ol>
     *     <li>Validación de existencia de referencias.</li>
     *     <li>Validación de stock disponible por producto.</li>
     *     <li>Cálculo de subtotales y total.</li>
     *     <li>Validación del monto pagado.</li>
     *     <li>Persistencia de Venta, DetalleVenta y Pago.</li>
     *     <li>Generación de MovimientoInventario (EGRESO) por producto.</li>
     *     <li>Actualización de stock.</li>
     *     <li>Registro de MovimientoCaja (INGRESO).</li>
     *     <li>Generación del Comprobante.</li>
     * </ol>
     *
     * <p>
     * Toda la operación se ejecuta bajo {@code @Transactional}.
     * Si cualquier paso falla, todos los cambios se revierten.
     * </p>
     *
     * @param request   datos de la venta a registrar.
     * @param loginUsuario email del usuario autenticado (extraído del JWT).
     * @return respuesta con el ID de la venta, cambio y datos del comprobante.
     */
    VentaCreateResponse crearVenta(VentaCreateRequest request, String loginUsuario);
}
