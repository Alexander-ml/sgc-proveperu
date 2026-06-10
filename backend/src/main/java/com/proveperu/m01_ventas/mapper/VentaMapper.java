package com.proveperu.m01_ventas.mapper;

import com.proveperu.m01_ventas.dto.response.*;
import com.proveperu.m01_ventas.entity.Comprobante;
import com.proveperu.m01_ventas.entity.Pago;
import com.proveperu.m01_ventas.entity.Venta;
import com.proveperu.m05_gestion_clientes.entity.Cliente;
import com.proveperu.m05_gestion_clientes.enums.TipoCliente;
import com.proveperu.m06_usuarios.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VentaMapper {
    /**
     * Mapeo principal de Venta a VentaResumenResponseDTO.
     * El campo numeroVenta y tipoVendedor son calculados en el Service
     * y asignados manualmente al DTO resultante.
     * Los pagos son inyectados por el service tras la carga batch.
     */
    @Mapping(target = "numeroVenta", ignore = true)
    @Mapping(target = "tipoVendedor", ignore = true)
    @Mapping(target = "metodosPago", ignore = true)
    @Mapping(source = "cliente", target = "cliente")
    @Mapping(source = "usuario", target = "vendedor")
    @Mapping(source = "comprobante", target = "comprobante")
    @Mapping(source = "estadoFisico", target = "estadoVenta")
    VentaResumenResponseDTO toResumenDTO(Venta venta);

    // ─── Cliente ──────────────────────────────────────────────────────────────

    @Mapping(source = "idCliente", target = "idCliente")
    @Mapping(source = "cliente", target = "nombreVisible", qualifiedByName = "resolverNombreVisible")
    ClienteResumenDTO toClienteResumenDTO(Cliente cliente);

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

    // ─── Vendedor / Usuario ───────────────────────────────────────────────────

    @Mapping(source = "idUsuario", target = "idUsuario")
    @Mapping(source = "nombreCompleto", target = "nombreCompleto")
    UsuarioResumenDTO toUsuarioResumenDTO(Usuario usuario);

    // ─── Comprobante ──────────────────────────────────────────────────────────

    @Mapping(source = "tipoComprobante", target = "tipoComprobante")
    @Mapping(source = "serie", target = "serie")
    @Mapping(source = "correlativo", target = "correlativo")
    ComprobanteResumenDTO toComprobanteResumenDTO(Comprobante comprobante);

    // ─── Pago ─────────────────────────────────────────────────────────────────

    @Mapping(source = "metodoPago.idMetodoPago", target = "idMetodoPago")
    @Mapping(source = "metodoPago.nombreMetodoPago", target = "nombreMetodoPago")
    @Mapping(source = "monto", target = "monto")
    MetodoPagoVentaResumenDTO toPagoResumenDTO(Pago pago);

    List<MetodoPagoVentaResumenDTO> toPagoResumenDTOList(List<Pago> pagos);
}
