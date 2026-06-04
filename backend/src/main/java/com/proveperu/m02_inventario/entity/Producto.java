package com.proveperu.m02_inventario.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "codigo_producto", nullable = false, unique = true, length = 50)
    private String codigoProducto;

    @Column(name = "nombre_producto", nullable = false, length = 100)
    private String nombreProducto;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida;

    @Column(name = "estado_logico", nullable = false)
    private Integer estadoLogico = 1;

    @Column(name = "estado_fisico", nullable = false, length = 20)
    private String estadoFisico = "ACTIVO";

    @Column(name = "fecha_hora_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaHoraCreacion;

    @Column(name = "fecha_hora_actualizacion")
    private LocalDateTime fechaHoraActualizacion;
}
