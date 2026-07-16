package com.proveperu.m06_usuarios.dto.request;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarPermisosRolRequest {
    
    @NotEmpty(message = "Debe enviar al menos un permiso")
    private List<Integer> permisos;
}
