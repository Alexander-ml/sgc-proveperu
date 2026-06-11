package com.proveperu.m01_ventas.entity;

import java.math.BigDecimal;

import com.proveperu.m01_ventas.enums.EstadoPago;
import com.proveperu.shared.entity.BaseCreationEntity;
import com.proveperu.shared.entity.MetodoPago;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa un pago realizado sobre una venta.
 *
 * <p>
 * Una venta puede estar compuesta por uno o varios pagos
 * utilizando distintos métodos de pago.
 * </p>
 *
 * <p>
 * Hereda de {@link BaseCreationEntity}, incorporando información
 * de auditoría como estado lógico y  fecha de creación.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pago")
public class Pago extends BaseCreationEntity {
    /**
     * Identificador único del pago.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    /**
     * Venta asociada al pago.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    /**
     * Método utilizado para realizar el pago.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    /**
     * Monto pagado.
     */
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    /**
     * Estado operativo del pago.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoPago estadoPago;
}
