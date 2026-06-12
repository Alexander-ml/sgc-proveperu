package com.proveperu.m01_ventas.mapper;

import com.proveperu.m01_ventas.dto.response.*;
import com.proveperu.m01_ventas.entity.Comprobante;
import com.proveperu.m01_ventas.entity.Pago;
import com.proveperu.m01_ventas.entity.Venta;
import com.proveperu.m05_gestion_clientes.entity.Cliente;
import com.proveperu.m05_gestion_clientes.enums.TipoCliente;
import com.proveperu.m06_usuarios.entity.Usuario;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper encargado de transformar entidades del módulo de ventas
 * en DTOs optimizados para la capa de presentación.
 *
 * <p>
 * Su responsabilidad es únicamente convertir estructuras de dominio
 * a modelos de salida, sin aplicar validaciones ni lógica de negocio.
 * </p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface VentaMapper {
    /**
     * Convierte una venta en su representación resumida para la grilla principal.
     *
     * <p>
     * Los campos calculados o ensamblados por la capa de servicio
     * se ignoran aquí porque no forman parte del mapeo directo.
     * </p>
     *
     * @param venta entidad de dominio.
     * @return DTO de resumen de venta.
     */
    @Mapping(target = "numeroVenta", ignore = true)
    @Mapping(target = "tipoVendedor", ignore = true)
    @Mapping(target = "metodosPago", ignore = true)
    @Mapping(target = "vendedor", source = "usuario")
    VentaResumenResponseDTO toResumenDTO(Venta venta);

    /**
     * Convierte un cliente en su representación resumida para la venta.
     *
     * @param cliente entidad de cliente.
     * @return DTO resumido del cliente.
     */
    @Mapping(target = "idCliente", source = "idCliente")
    @Mapping(target = "nombreCliente", source = ".", qualifiedByName = "resolverNombreVisible")
    ClienteResumenDTO toClienteResumenDTO(Cliente cliente);

    /**
     * Resuelve el nombre visible del cliente según su tipo.
     *
     * @param cliente entidad de cliente.
     * @return nombre visible o {@code null} si no existe cliente.
     */
    @Named("resolverNombreVisible")
    default String resolverNombreVisible(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        if (cliente.getTipoCliente() == TipoCliente.PERSONA) {
            return cliente.getNombreCompleto();
        }

        return cliente.getRazonSocial();
    }

    /**
     * Convierte un usuario en su representación resumida.
     *
     * @param usuario entidad de usuario.
     * @return DTO resumido del vendedor.
     */
    UsuarioResumenDTO toUsuarioResumenDTO(Usuario usuario);

    /**
     * Convierte un comprobante en su representación resumida.
     *
     * @param comprobante entidad de comprobante.
     * @return DTO resumido del comprobante.
     */
    ComprobanteResumenDTO toComprobanteResumenDTO(Comprobante comprobante);

    /**
     * Convierte un pago en su representación resumida.
     *
     * @param pago entidad de pago.
     * @return DTO resumido del método de pago aplicado.
     */
    @Mapping(source = "metodoPago.idMetodoPago", target = "idMetodoPago")
    @Mapping(source = "metodoPago.nombreMetodoPago", target = "nombreMetodoPago")
    @Mapping(source = "monto", target = "monto")
    MetodoPagoVentaResumenDTO toPagoResumenDTO(Pago pago);

    /**
     * Convierte una lista de pagos a su representación resumida.
     *
     * <p>
     * Si la lista de origen es nula, se retorna una lista vacía
     * para mantener el contrato del DTO de respuesta.
     * </p>
     *
     * @param pagos lista de pagos.
     * @return lista resumida de pagos, nunca nula.
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<MetodoPagoVentaResumenDTO> toPagoResumenDTOList(List<Pago> pagos);
}
