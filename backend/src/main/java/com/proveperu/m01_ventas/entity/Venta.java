package com.proveperu.m01_ventas.entity;

import com.proveperu.m01_ventas.enums.EstadoVenta;
import com.proveperu.m05_gestion_clientes.entity.Cliente;
import com.proveperu.m06_usuarios.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa la cabecera de una venta realizada
 * dentro del sistema.
 *
 * <p>
 * Actúa como Aggregate Root del contexto de Ventas.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "venta")
public class Venta {
    /**
     * Identificador único de la venta.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

    /**
     * Cliente asociado a la venta.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    /**
     * Usuario que registró la venta.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    /**
     * Fecha y hora en que se registró la venta.
     */
    @Column(name = "fecha_hora_venta", nullable = false, updatable = false)
    private LocalDateTime fechaHoraVenta;

    /**
     * Importe total de la venta.
     */
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    /**
     * Estado de la venta.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoVenta estadoVenta;

    /**
     * Detalles asociados a la venta.
     *
     * <p>
     * La relación es bidireccional y está administrada por
     * la entidad {@link DetalleVenta} mediante el atributo {@code venta}.
     * </p>
     *
     * <p>
     * Cada registro de {@link DetalleVenta} representa un
     * producto incluido dentro de esta venta.
     * </p>
     *
     * <p>
     * Se utiliza {@code CascadeType.ALL} para garantizar que
     * las operaciones de persistencia realizadas sobre la venta
     * se propaguen automáticamente a sus detalles.
     * </p>
     *
     * <p>
     * Se utiliza {@code orphanRemoval = true} para eliminar
     * automáticamente los detalles que sean removidos de la
     * colección, manteniendo la consistencia del agregado.
     * </p>
     */
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detallesVenta = new ArrayList<>();

    /**
     * Pagos asociados a la venta.
     *
     * <p>
     * La relación es bidireccional y está administrada por
     * la entidad {@link Pago} mediante el atributo
     * {@code venta}.
     * </p>
     *
     * <p>
     * Cada registro de {@link Pago} representa un pago
     * parcial o total realizado sobre esta venta.
     * </p>
     */
    @OneToMany(
            mappedBy = "venta",
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Pago> pagos = new ArrayList<>();

    @OneToOne(mappedBy = "venta", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private Comprobante comprobante;
}
