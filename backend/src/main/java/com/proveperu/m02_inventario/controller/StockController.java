package com.proveperu.m02_inventario.controller;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m02_inventario.entity.Stock;
import com.proveperu.m02_inventario.service.InventarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StockController {
       private final InventarioService inventarioService;

    @GetMapping("/api/stock")
    public List<Stock> listarStock() {
        return inventarioService.listarStock();
    }
}
