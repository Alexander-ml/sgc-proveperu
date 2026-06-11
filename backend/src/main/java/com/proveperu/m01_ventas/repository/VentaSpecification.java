package com.proveperu.m01_ventas.repository;

import com.proveperu.m01_ventas.dto.request.VentaFiltroRequest;
import com.proveperu.m01_ventas.entity.Comprobante;
import com.proveperu.m01_ventas.entity.Pago;
import com.proveperu.m01_ventas.entity.Venta;
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

public class VentaSpecification {
    private VentaSpecification() {}

    /**
     * Construye la Specification dinámica para filtrar ventas.
     *
     * Nota sobre numeroVenta:
     * El número visual "V-{AÑO}-{ID_6}" es derivado.
     * Si el filtro numeroVenta tiene el formato esperado, se extrae el id numérico
     * final y se aplica el predicado sobre id_venta directamente.
     * Si no tiene el formato esperado, se ignora ese filtro silenciosamente
     * para evitar errores por entradas malformadas.
     */
    public static Specification<Venta> conFiltros(VentaFiltroRequest filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();

            // JOIN obligatorio para búsqueda global y filtros relacionales
            Join<Venta, Cliente> clienteJoin = root.join("cliente", JoinType.LEFT);

            // Evitar duplicados por JOINs de colecciones en COUNT query
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }

            // 1. Búsqueda global (q)
            if (StringUtils.hasText(filtro.getQ())) {
                String patron = "%" + filtro.getQ().toLowerCase() + "%";
                Predicate porNombreCliente =
                        cb.like(cb.lower(clienteJoin.get("nombreCompleto")), patron);
                Predicate porRazonSocial =
                        cb.like(cb.lower(clienteJoin.get("razonSocial")), patron);
                // Búsqueda por comprobante (serie o correlativo) via subquery
                Subquery<Integer> subComprobante = query.subquery(Integer.class);
                Root<Comprobante> compRoot = subComprobante.from(Comprobante.class);
                subComprobante.select(compRoot.get("venta").get("idVenta"))
                        .where(cb.or(
                                cb.like(cb.lower(compRoot.get("serie")), patron),
                                cb.like(cb.lower(compRoot.get("correlativo")), patron)
                        ));
                Predicate porComprobante =
                        root.get("idVenta").in(subComprobante);

                predicados.add(cb.or(porNombreCliente, porRazonSocial, porComprobante));
            }

            // 2. Filtro por cliente exacto
            if (filtro.getClienteId() != null) {
                predicados.add(cb.equal(clienteJoin.get("idCliente"), filtro.getClienteId()));
            }

            // 3. Filtro por número visual de venta
            if (StringUtils.hasText(filtro.getNumeroVenta())) {
                Integer idExtraido = extraerIdDeNumeroVenta(filtro.getNumeroVenta().trim());
                if (idExtraido != null) {
                    predicados.add(cb.equal(root.get("idVenta"), idExtraido));
                }
            }

            // 4. Filtro por tipo de comprobante
            if (filtro.getTipoComprobante() != null) {
                Subquery<Integer> subTipoComp = query.subquery(Integer.class);
                Root<Comprobante> compRoot = subTipoComp.from(Comprobante.class);
                subTipoComp.select(compRoot.get("venta").get("idVenta"))
                        .where(cb.equal(compRoot.get("tipoComprobante"), filtro.getTipoComprobante()));
                predicados.add(root.get("idVenta").in(subTipoComp));
            }

            // 5. Filtro por estado de venta
            if (filtro.getEstadoVenta() != null) {
                predicados.add(
                        cb.equal(root.get("estadoFisico"), filtro.getEstadoVenta()));
            }

            // 6. Filtro por método de pago
            if (filtro.getMetodoPagoId() != null) {
                Subquery<Integer> subPago = query.subquery(Integer.class);
                Root<Pago> pagoRoot = subPago.from(Pago.class);
                subPago.select(pagoRoot.get("venta").get("idVenta"))
                        .where(
                                cb.and(
                                        cb.equal(pagoRoot.get("metodoPago").get("idMetodoPago"),
                                                filtro.getMetodoPagoId()),
                                        cb.equal(pagoRoot.get("estadoLogico"), 1)
                                )
                        );
                predicados.add(root.get("idVenta").in(subPago));
            }

            // 7. Filtro por rango de fechas
            if (filtro.getFechaInicio() != null) {
                predicados.add(
                        cb.greaterThanOrEqualTo(
                                root.get("fechaHoraVenta"), filtro.getFechaInicio()));
            }
            if (filtro.getFechaFin() != null) {
                predicados.add(
                        cb.lessThanOrEqualTo(
                                root.get("fechaHoraVenta"), filtro.getFechaFin()));
            }

            return cb.and(predicados.toArray(new Predicate[0]));
        };
    }

    /**
     * Extrae el id numérico del número visual de venta.
     * Formato esperado: V-YYYY-NNNNNN
     * Retorna null si el formato no coincide.
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
}
