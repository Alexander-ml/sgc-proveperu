package com.proveperu.m02_inventario.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {
     @Id
    @Column(name = "id_producto")
    private Integer idProducto;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @Column(name = "cantidad_actual", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadActual = BigDecimal.ZERO;

    @Column(name = "stock_minimo", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockMinimo = BigDecimal.ZERO;

    @Column(name = "fecha_hora_actualizacion", nullable = false)
    private LocalDateTime fechaHoraActualizacion;
}
