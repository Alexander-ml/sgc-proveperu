package com.proveperu.m03_compras.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.proveperu.m03_compras.enums.EstadoCompra;
import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.shared.entity.BaseCreationEntity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa la cabecera de una compra registrada
 * a un proveedor dentro del sistema.
 *
 * <p>
 * Actúa como Aggregate Root del contexto de Compras.
 * </p>
 *
 * <p>
 * Hereda de {@link BaseCreationEntity}, incorporando información
 * de auditoría como estado lógico y fecha de creación.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "compra")
public class Compra extends BaseCreationEntity {
    /**
     * Identificador único de la compra.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Integer idCompra;

    /**
     * Proveedor al cual se realiza la compra.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    /**
     * Usuario que registró la compra.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_registro", nullable = false)
    private Usuario usuarioRegistro;

    /**
     * Importe total de la compra.
     */
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    /**
     * Estado operativo de la compra.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoCompra estadoFisico;

    /**
     * Productos incluidos dentro de la compra.
     *
     * <p>
     * La relación es bidireccional y está administrada por
     * la entidad {@link DetalleCompra} mediante el atributo {@code compra}.
     * </p>
     *
     * <p>
     * Cada registro de {@link DetalleCompra} representa un producto adquirido dentro de esta compra.
     * </p>
     *
     * <p>
     * Se utiliza {@code CascadeType.ALL} para propagar las operaciones de persistencia del agregado.
     * </p>
     *
     * <p>
     * Se utiliza {@code orphanRemoval = true} para
     * eliminar automáticamente los detalles removidos
     * de la colección.
     * </p>
     */
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleCompra> detallesCompra = new ArrayList<>();

    /**
     * Recepciones registradas para la compra.
     *
     * <p>
     * La relación es bidireccional y está administrada por
     * la entidad {@link RecepcionCompra} mediante el atributo {@code compra}.
     * </p>
     *
     * <p>
     * Cada registro representa una recepción parcial
     * o total asociada a esta compra.
     * </p>
     */
    @OneToMany(mappedBy = "compra", fetch = FetchType.LAZY)
    @Builder.Default
    private List<RecepcionCompra> recepcionesCompra = new ArrayList<>();
    /**
     * Pagos registrados para la compra.
     *
     * <p>
     * La relación es bidireccional y está administrada por
     * la entidad {@link PagoCompra} mediante el atributo
     * {@code compra}.
     * </p>
     *
     * <p>
     * Cada registro representa un pago parcial
     * o total realizado al proveedor.
     * </p>
     */
    @OneToMany(
            mappedBy = "compra",
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<PagoCompra> pagos = new ArrayList<>();
}
