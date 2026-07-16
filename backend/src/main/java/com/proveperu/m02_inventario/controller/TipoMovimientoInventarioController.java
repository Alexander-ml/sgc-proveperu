package com.proveperu.m02_inventario.controller;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m02_inventario.entity.TipoMovimientoInventario;
import com.proveperu.m02_inventario.service.InventarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TipoMovimientoInventarioController {
      private final InventarioService inventarioService;

//    @GetMapping("/api/tipos-movimiento-inventario")
//    public List<TipoMovimientoInventario> listarTiposMovimiento() {
//        return inventarioService.listarTiposMovimiento();
//    }
}
