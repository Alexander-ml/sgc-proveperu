package com.proveperu.m01_ventas.mapper;

import com.proveperu.m01_ventas.dto.response.ProductoVentaResponse;
import com.proveperu.m02_inventario.entity.Producto;
import com.proveperu.m02_inventario.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

/**
 * Mapper MapStruct para construir el DTO {@link ProductoVentaResponse}
 * combinando datos de la entidad {@link Producto} y su {@link Stock}.
 *
 * <p>
 * Dado que el stock vive en una entidad separada ({@code Stock}),
 * se usa un método de fábrica con dos argumentos de entrada.
 * MapStruct no soporta directamente la fusión de dos entidades
 * en un DTO, por lo que el método acepta ambos objetos y mapea
 * explícitamente cada campo.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ProductoVentaMapper {

    /**
     * Construye un {@link ProductoVentaResponse} combinando
     * los datos del producto y su stock disponible.
     *
     * @param producto entidad producto del catálogo.
     * @param stock    stock actual del producto (puede ser nulo
     *                 si no se ha inicializado, en cuyo caso se
     *                 reporta stock cero).
     * @return DTO listo para la respuesta API.
     */
    @Mapping(target = "idProducto",      source = "producto.idProducto")
    @Mapping(target = "codigoProducto",  source = "producto.codigoProducto")
    @Mapping(target = "nombreProducto",  source = "producto.nombreProducto")
    @Mapping(target = "unidadMedida",    source = "producto.unidadMedida")
    @Mapping(target = "stockActual",     source = "stock",    qualifiedByName = "stockActualDeSafe")
    @Mapping(target = "stockMinimo",     source = "stock",    qualifiedByName = "stockMinimoDeSafe")
    ProductoVentaResponse toResponse(Producto producto, Stock stock);

    /**
     * Extrae la cantidad actual del stock de forma segura.
     * Si el stock es nulo retorna {@link BigDecimal#ZERO}.
     *
     * @param stock entidad stock, puede ser nula.
     * @return cantidad actual disponible o cero si no hay stock.
     */
    @org.mapstruct.Named("stockActualDeSafe")
    default BigDecimal stockActualSafe(Stock stock) {
        return stock != null ? stock.getCantidadActual() : BigDecimal.ZERO;
    }

    /**
     * Extrae el stock mínimo de forma segura.
     * Si el stock es nulo retorna {@link BigDecimal#ZERO}.
     *
     * @param stock entidad stock, puede ser nula.
     * @return stock mínimo configurado o cero si no hay stock.
     */
    @org.mapstruct.Named("stockMinimoDeSafe")
    default BigDecimal stockMinimoSafe(Stock stock) {
        return stock != null ? stock.getStockMinimo() : BigDecimal.ZERO;
    }
}