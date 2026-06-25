package com.proveperu.m01_ventas.mapper;

import com.proveperu.m01_ventas.dto.response.ClienteListadoResponse;
import com.proveperu.m05_gestion_clientes.entity.Cliente;
import com.proveperu.m05_gestion_clientes.enums.TipoCliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper MapStruct para convertir entidades {@link Cliente}
 * a DTOs de respuesta usados en el módulo de Ventas.
 *
 * <p>
 * La conversión del nombre visible se resuelve en el método
 * {@code resolverNombreVisible}: si el cliente es PERSONA
 * se usa {@code nombreCompleto}, si es EMPRESA se usa
 * {@code razonSocial}.
 * </p>
 *
 * <p>
 * El componente es gestionado por Spring (componentModel = "spring"),
 * permitiendo su inyección como {@code @Autowired}.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ClienteMapper {

    /**
     * Convierte una entidad {@link Cliente} a
     * {@link ClienteListadoResponse}.
     *
     * @param cliente entidad cliente a convertir.
     * @return DTO listo para la respuesta API.
     */
    @Mapping(target = "nombreCompleto", source = "cliente", qualifiedByName = "resolverNombreVisible")
    ClienteListadoResponse toListadoResponse(Cliente cliente);

    /**
     * Convierte una lista de clientes a lista de DTOs.
     *
     * @param clientes lista de entidades.
     * @return lista de DTOs de respuesta.
     */
    List<ClienteListadoResponse> toListadoResponseList(List<Cliente> clientes);

    /**
     * Determina el nombre visible del cliente según su tipo.
     *
     * <p>
     * Para {@code PERSONA} retorna el campo {@code nombreCompleto}.
     * Para {@code EMPRESA} retorna el campo {@code razonSocial}.
     * Si ambos están nulos, retorna cadena vacía.
     * </p>
     *
     * @param cliente entidad cliente.
     * @return nombre visible a mostrar en la interfaz.
     */
    @Named("resolverNombreVisible")
    default String resolverNombreVisible(Cliente cliente) {
        if (TipoCliente.PERSONA.equals(cliente.getTipoCliente())) {
            return cliente.getNombreCompleto() != null
                    ? cliente.getNombreCompleto()
                    : "";
        }
        return cliente.getRazonSocial() != null
                ? cliente.getRazonSocial()
                : "";
    }
}