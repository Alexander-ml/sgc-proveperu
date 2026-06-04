package com.proveperu.m02_inventario.controller;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m02_inventario.entity.MovimientoInventario;
import com.proveperu.m02_inventario.service.InventarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MovimientoInventarioController {
    
    private final InventarioService inventarioService;

    @GetMapping("/api/movimientos-inventario")
    public List<MovimientoInventario> listarMovimientos() {
        return inventarioService.listarMovimientos();
    }
}
