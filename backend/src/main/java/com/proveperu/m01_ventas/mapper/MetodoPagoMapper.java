package com.proveperu.m01_ventas.mapper;

import com.proveperu.m01_ventas.dto.response.MetodoPagoResponse;
import com.proveperu.shared.entity.MetodoPago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper MapStruct para convertir entidades {@link MetodoPago}
 * a DTOs de respuesta usados en el módulo de Ventas.
 *
 * <p>
 * Pertenece al módulo de Ventas aunque la entidad {@code MetodoPago}
 * vive en el módulo shared, porque el contexto de uso es la
 * selección de método de pago al crear una venta.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface MetodoPagoMapper {

    /**
     * Convierte una entidad {@link MetodoPago} a
     * {@link MetodoPagoResponse}.
     *
     * @param metodoPago entidad a convertir.
     * @return DTO de respuesta con id y nombre del método.
     */
    @Mapping(target = "idMetodoPago", source = "idMetodoPago")
    @Mapping(target = "nombre", source = "nombreMetodoPago")
    MetodoPagoResponse toResponse(MetodoPago metodoPago);

    /**
     * Convierte una lista de métodos de pago a lista de DTOs.
     *
     * @param metodos lista de entidades.
     * @return lista de DTOs de respuesta.
     */
    List<MetodoPagoResponse> toResponseList(List<MetodoPago> metodos);
}