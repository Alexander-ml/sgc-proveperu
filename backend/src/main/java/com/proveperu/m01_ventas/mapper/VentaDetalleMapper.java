package com.proveperu.m01_ventas.mapper;

import com.proveperu.m01_ventas.dto.response.*;
import com.proveperu.m01_ventas.entity.Comprobante;
import com.proveperu.m01_ventas.entity.DetalleVenta;
import com.proveperu.m01_ventas.entity.Pago;
import com.proveperu.m01_ventas.entity.Venta;
import com.proveperu.m01_ventas.enums.EstadoVenta;
import com.proveperu.m01_ventas.enums.TipoComprobante;
import com.proveperu.m05_gestion_clientes.entity.Cliente;
import com.proveperu.m05_gestion_clientes.enums.TipoCliente;
import com.proveperu.m06_usuarios.entity.Usuario;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper encargado de transformar las entidades del módulo de ventas
 * en los DTOs de detalle completo para la vista individual de una venta.
 *
 * <p>
 * Los campos derivados (numeroVenta, tipoVendedor, subtotalGeneral,
 * montoPagadoTotal, cambio) son ignorados aquí y calculados
 * exclusivamente en la capa de servicio.
 * </p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface VentaDetalleMapper {
    /**
     * Convierte una venta en su DTO de detalle completo.
     *
     * <p>
     * Los campos derivados se ignoran en el mapeo automático
     * porque son enriquecidos por el service tras la conversión.
     * </p>
     *
     * @param venta entidad de dominio.
     * @return DTO de detalle de venta.
     */
    @Mapping(target = "numeroVenta",       ignore = true)
    @Mapping(target = "tipoVendedor",      ignore = true)
    @Mapping(target = "pagos",             ignore = true)
    @Mapping(target = "productos",         ignore = true)
    @Mapping(target = "subtotalGeneral",   ignore = true)
    @Mapping(target = "montoPagadoTotal",  ignore = true)
    @Mapping(target = "cambio",            ignore = true)
    @Mapping(target = "estadoVenta",       source = "estadoVenta", qualifiedByName = "estadoVentaToString")
    @Mapping(target = "vendedor",          source = "usuario")
    VentaDetalleResponseDTO toDetalleDTO(Venta venta);

    /**
     * Convierte el enum de estado de venta en su representación textual.
     *
     * @param estadoVenta enum de estado.
     * @return nombre del estado o null.
     */
    @Named("estadoVentaToString")
    default String estadoVentaToString(
            EstadoVenta estadoVenta) {
        return estadoVenta != null ? estadoVenta.name() : null;
    }

    /**
     * Convierte un cliente en su DTO resumido para el detalle de venta.
     *
     * @param cliente entidad de cliente.
     * @return DTO del cliente.
     */
    @Mapping(target = "nombreVisible", source = ".", qualifiedByName = "resolverNombreVisibleDetalle")
    ClienteDetalleVentaDTO toClienteDetalleDTO(Cliente cliente);

    /**
     * Resuelve el nombre visible del cliente según su tipo.
     *
     * @param cliente entidad de cliente.
     * @return nombre visible.
     */
    @Named("resolverNombreVisibleDetalle")
    default String resolverNombreVisibleDetalle(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return cliente.getTipoCliente() == TipoCliente.PERSONA
                ? cliente.getNombreCompleto()
                : cliente.getRazonSocial();
    }

    /**
     * Convierte un usuario en su DTO resumido para el detalle de venta.
     *
     * @param usuario entidad de usuario.
     * @return DTO del vendedor.
     */
    UsuarioDetalleVentaDTO toUsuarioDetalleDTO(Usuario usuario);

    /**
     * Convierte un comprobante en su DTO para el detalle de venta.
     *
     * @param comprobante entidad de comprobante.
     * @return DTO del comprobante o null.
     */
    @Mapping(target = "tipoComprobante", source = "tipoComprobante", qualifiedByName = "tipoComprobanteToString")
    ComprobanteDetalleVentaDTO toComprobanteDetalleDTO(Comprobante comprobante);

    /**
     * Convierte el enum de tipo de comprobante en su representación textual.
     *
     * @param tipo enum de tipo comprobante.
     * @return nombre del tipo o null.
     */
    @Named("tipoComprobanteToString")
    default String tipoComprobanteToString(TipoComprobante tipo) {
        return tipo != null ? tipo.name() : null;
    }

    /**
     * Convierte un pago en su DTO para el detalle de venta.
     *
     * @param pago entidad de pago.
     * @return DTO del pago.
     */
    @Mapping(target = "metodoPagoId",     source = "metodoPago.idMetodoPago")
    @Mapping(target = "metodoPagoNombre", source = "metodoPago.nombreMetodoPago")
    PagoVentaDetalleDTO toPagoDetalleDTO(Pago pago);

    /**
     * Convierte una lista de pagos en sus DTOs de detalle.
     *
     * @param pagos lista de pagos.
     * @return lista de DTOs.
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<PagoVentaDetalleDTO> toPagoDetalleDTOList(List<Pago> pagos);

    /**
     * Convierte un detalle de venta en su DTO de producto.
     *
     * @param detalleVenta entidad de detalle.
     * @return DTO del producto en la venta.
     */
    @Mapping(target = "productoId",      source = "producto.idProducto")
    @Mapping(target = "codigoProducto",  source = "producto.codigoProducto")
    @Mapping(target = "nombreProducto",  source = "producto.nombreProducto")
    DetalleProductoVentaDTO toDetalleProductoDTO(DetalleVenta detalleVenta);

    /**
     * Convierte una lista de detalles en sus DTOs de producto.
     *
     * @param detalles lista de detalles.
     * @return lista de DTOs de producto.
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<DetalleProductoVentaDTO> toDetalleProductoDTOList(List<DetalleVenta> detalles);
}
