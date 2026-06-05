package com.proveperu.m03_compras.entity;

import com.proveperu.m03_compras.enums.EstadoPagoCompra;
import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.shared.entity.BaseEstadoLogicoEntity;
import com.proveperu.shared.entity.MetodoPago;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa un pago realizado a un proveedor
 * como parte del proceso de compras.
 *
 * <p>
 * Una compra puede registrar uno o varios pagos
 * parciales o totales.
 * </p>
 *
 * <p>
 * Hereda de {@link BaseEstadoLogicoEntity}, incorporando estado lógico.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pago_compra")
public class PagoCompra extends BaseEstadoLogicoEntity {
    /**
     * Identificador único del pago.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_compra")
    private Integer idPagoCompra;

    /**
     * Compra asociada al pago.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra compra;

    /**
     * Método de pago utilizado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    /**
     * Usuario responsable del registro del pago.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_registro", nullable = false)
    private Usuario usuarioRegistro;

    /**
     * Importe pagado.
     */
    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    /**
     * Fecha y hora del pago.
     */
    @Column(name = "fecha_hora_pago", nullable = false)
    private LocalDateTime fechaHoraPago;

    /**
     * Estado operativo del pago.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoPagoCompra estadoFisico;
}
