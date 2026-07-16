package com.proveperu.m01_ventas.repository;

import com.proveperu.m01_ventas.dto.request.VentaFiltroRequest;
import com.proveperu.m01_ventas.entity.Comprobante;
import com.proveperu.m01_ventas.entity.Pago;
import com.proveperu.m01_ventas.entity.Venta;
import com.proveperu.m01_ventas.enums.EstadoVenta;
import com.proveperu.m01_ventas.enums.TipoComprobante;
import com.proveperu.m05_gestion_clientes.entity.Cliente;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Especificación JPA utilizada para construir filtros dinámicos sobre la entidad {@link Venta}.
 *
 * <p>
 * Esta clase concentra la traducción de criterios funcionales del listado de ventas
 * hacia predicados Criteria API, permitiendo componer consultas flexibles sin exponer
 * lógica de persistencia en la capa de servicio.
 * </p>
 *
 * <p>
 * El diseño está orientado al caso de uso de búsqueda y paginación de ventas,
 * incluyendo filtros por cliente, comprobante, estado, método de pago, número visual
 * y rango de fechas.
 * </p>
 */
public class VentaSpecification {
    private VentaSpecification() {}

    /**
     * Construye una {@link Specification} dinámica para filtrar ventas según los criterios
     * recibidos en el DTO de filtro.
     *
     * <p>
     * Los filtros soportados incluyen:
     * </p>
     * <ul>
     *     <li>Búsqueda global sobre cliente y comprobante.</li>
     *     <li>Filtro por cliente exacto.</li>
     *     <li>Filtro por número visual derivado de la venta.</li>
     *     <li>Filtro por tipo de comprobante.</li>
     *     <li>Filtro por estado de la venta.</li>
     *     <li>Filtro por método de pago.</li>
     *     <li>Filtro por rango de fechas.</li>
     * </ul>
     *
     * <p>
     * Cuando el número visual no cumple el formato esperado, el filtro se ignora para
     * evitar errores por entradas malformadas.
     * </p>
     *
     * @param filtro criterios de consulta enviados por la capa web.
     * @return especificación lista para ser usada por {@code JpaSpecificationExecutor}.
     */
    public static Specification<Venta> conFiltros(VentaFiltroRequest filtro) {
        return (root, query, cb) -> {
            if (filtro == null) {
                return cb.conjunction();
            }

            List<Predicate> predicados = new ArrayList<>();

            // JOIN necesario para filtros sobre cliente.
            Join<Venta, Cliente> clienteJoin = root.join("cliente", JoinType.LEFT);

            // Evita duplicados en la consulta principal cuando existen subconsultas o joins.
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }

            // 1. Búsqueda global.
            if (StringUtils.hasText(filtro.getQ())) {
                String patron = "%" + filtro.getQ().trim().toLowerCase(Locale.ROOT) + "%";

                Predicate porNombreCliente = cb.like(cb.lower(clienteJoin.get("nombreCompleto")), patron);

                Predicate porRazonSocial = cb.like(cb.lower(clienteJoin.get("razonSocial")), patron);

                Subquery<Integer> subComprobante = query.subquery(Integer.class);
                Root<Comprobante> compRoot = subComprobante.from(Comprobante.class);
                subComprobante.select(compRoot.get("venta").get("idVenta"))
                        .where(cb.or(
                                cb.like(cb.lower(compRoot.get("serie")), patron),
                                cb.like(cb.lower(compRoot.get("correlativo")), patron)
                        ));
                Predicate porComprobante = root.get("idVenta").in(subComprobante);
                predicados.add(cb.or(porNombreCliente, porRazonSocial, porComprobante));
            }

            // 2. Filtro por cliente exacto.
            if (filtro.getClienteId() != null) {
                predicados.add(cb.equal(clienteJoin.get("idCliente"), filtro.getClienteId()));
            }

            // 3. Filtro por número visual de venta.
            if (StringUtils.hasText(filtro.getNumeroVenta())) {
                Integer idExtraido = extraerIdDeNumeroVenta(filtro.getNumeroVenta().trim());
                if (idExtraido != null) {
                    predicados.add(cb.equal(root.get("idVenta"), idExtraido));
                }
            }

            // 4. Filtro por tipo de comprobante.
            TipoComprobante tipoComprobante = toTipoComprobante(filtro.getTipoComprobante());
            if (tipoComprobante != null) {
                Subquery<Integer> subTipoComp = query.subquery(Integer.class);
                Root<Comprobante> compRoot = subTipoComp.from(Comprobante.class);
                subTipoComp.select(compRoot.get("venta").get("idVenta"))
                        .where(cb.equal(compRoot.get("tipoComprobante"), tipoComprobante));
                predicados.add(root.get("idVenta").in(subTipoComp));
            }

            // 5. Filtro por estado de venta.
            EstadoVenta estadoVenta = toEstadoVenta(filtro.getEstadoVenta());
            if (estadoVenta != null) {
                predicados.add(cb.equal(root.get("estadoVenta"), estadoVenta));
            }

            // 6. Filtro por método de pago.
            if (filtro.getMetodoPagoId() != null) {
                Subquery<Integer> subPago = query.subquery(Integer.class);
                Root<Pago> pagoRoot = subPago.from(Pago.class);
                subPago.select(pagoRoot.get("venta").get("idVenta"))
                        .where(
                                cb.and(
                                        cb.equal(
                                                pagoRoot.get("metodoPago").get("idMetodoPago"),
                                                filtro.getMetodoPagoId()
                                        ),
                                        cb.equal(pagoRoot.get("estadoLogico"), 1)
                                )
                        );
                predicados.add(root.get("idVenta").in(subPago));
            }

            // 7. Rango de fechas.
            if (filtro.getFechaInicio() != null) {
                predicados.add(
                        cb.greaterThanOrEqualTo(
                                root.get("fechaHoraVenta"),
                                filtro.getFechaInicio()
                        )
                );
            }

            if (filtro.getFechaFin() != null) {
                predicados.add(
                        cb.lessThanOrEqualTo(
                                root.get("fechaHoraVenta"),
                                filtro.getFechaFin()
                        )
                );
            }

            return cb.and(predicados.toArray(new Predicate[0]));
        };
    }

    /**
     * Extrae el identificador técnico contenido en el número visual de venta.
     *
     * <p>
     * Formato esperado: {@code V-YYYY-NNNNNN}. Si el formato no coincide o el valor
     * numérico no puede ser interpretado, el método retorna {@code null}.
     * </p>
     *
     * @param numeroVenta número visual ingresado por el usuario.
     * @return identificador técnico de la venta o {@code null} si no es válido.
     */
    private static Integer extraerIdDeNumeroVenta(String numeroVenta) {
        String[] partes = numeroVenta.split("-");
        if (partes.length != 3) {
            return null;
        }

        try {
            return Integer.parseInt(partes[2]);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Convierte un valor textual al enum {@link TipoComprobante}.
     *
     * <p>
     * La conversión es tolerante a espacios y a diferencias de mayúsculas/minúsculas.
     * Si el valor no corresponde a ningún enum válido, retorna {@code null}.
     * </p>
     *
     * @param valor texto recibido desde el filtro.
     * @return enum convertido o {@code null} si el valor no es válido.
     */
    private static TipoComprobante toTipoComprobante(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }

        try {
            return TipoComprobante.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Convierte un valor textual al enum {@link EstadoVenta}.
     *
     * <p>
     * La conversión es tolerante a espacios y a diferencias de mayúsculas/minúsculas.
     * Si el valor no corresponde a ningún enum válido, retorna {@code null}.
     * </p>
     *
     * @param valor texto recibido desde el filtro.
     * @return enum convertido o {@code null} si el valor no es válido.
     */
    private static EstadoVenta toEstadoVenta(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }

        try {
            return EstadoVenta.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
