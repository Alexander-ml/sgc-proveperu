package com.proveperu.m06_usuarios.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Entidad que representa una sesión iniciada por un usuario
 * dentro del sistema.
 *
 * Permite registrar el historial de accesos, incluyendo
 * la fecha y hora de inicio y finalización de cada sesión.
 *
 * Una sesión pertenece a un único usuario, mientras que
 * un usuario puede tener múltiples sesiones registradas.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuario_sesion")
public class UsuarioSesion {

    /**
     * Identificador único de la sesión.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_sesion")
    private Integer idUsuarioSesion;

    /**
     * Usuario propietario de la sesión.
     *
     * Relación muchos a uno debido a que un usuario
     * puede iniciar múltiples sesiones a lo largo del tiempo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    /**
     * Fecha y hora de inicio de sesión.
     */
    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    /**
     * Fecha y hora de cierre de sesión.
     *
     * Puede ser nula mientras la sesión
     * permanezca activa.
     */
    @Column(name = "fecha_hora_fin")
    private LocalDateTime fechaHoraFin;

}
