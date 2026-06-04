package com.proveperu.m02_inventario.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_movimiento_inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoMovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_movimiento_inventario")
    private Integer idTipoMovimientoInventario;

    @Column(name = "nombre", nullable = false, unique = true, length = 30)
    private String nombre;

    @Column(name = "estado_logico", nullable = false)
    private Integer estadoLogico = 1;

    @Column(name = "estado_fisico", nullable = false, length = 20)
    private String estadoFisico = "ACTIVO";
}