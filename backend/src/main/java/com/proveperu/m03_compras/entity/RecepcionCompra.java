package com.proveperu.m03_compras.entity;

import com.proveperu.m03_compras.enums.EstadoRecepcionCompra;
import com.proveperu.m06_usuarios.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Representa una recepción registrada para una compra.
 *
 * <p>
 * Permite controlar la recepción física de productos
 * adquiridos a proveedores dentro del proceso de compras.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recepccion_compra")
public class RecepcionCompra {
    /**
     * Identificador único de la recepción.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recepcion_compra")
    private Integer idRecepcionCompra;

    /**
     * Compra asociada a la recepción.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra compra;

    /**
     * Usuario responsable del registro de la recepción.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_registro", nullable = false)
    private Usuario usuarioRegistro;

    /**
     * Fecha y hora en que se registró la recepción.
     */
    @Column(name = "fecha_hora_recepcion", nullable = false)
    private LocalDateTime fechaHoraRecepcion;

    /**
     * Estado operativo de la recepción.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoRecepcionCompra estadoFisico;
}
