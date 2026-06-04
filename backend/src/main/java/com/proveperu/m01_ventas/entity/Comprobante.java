package com.proveperu.m01_ventas.entity;

import com.proveperu.m01_ventas.enums.EstadoComprobante;
import com.proveperu.m01_ventas.enums.TipoComprobante;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Representa el comprobante emitido como resultado
 * de una venta registrada en el sistema.
 *
 * Puede corresponder a una boleta, factura o nota.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comprobante")
public class Comprobante {
    /**
     * Identificador único del comprobante.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante")
    private Integer idComprobante;

    /**
     * Venta asociada al comprobante.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    /**
     * Tipo documental emitido.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false, length = 10)
    private TipoComprobante tipoComprobante;

    /**
     * Serie del comprobante.
     */
    @Column(name = "serie", nullable = false, unique = true, length = 4)
    private String serie;

    /**
     * Número correlativo del comprobante.
     */
    @Column(name = "correlativo", nullable = false, unique = true, length = 8)
    private String correlativo;

    /**
     * Fecha y hora de emisión.
     */
    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    /**
     * Estado operativo del comprobante.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoComprobante estadoFisico;
}
