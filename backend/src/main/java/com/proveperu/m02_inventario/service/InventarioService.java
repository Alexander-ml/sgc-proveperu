package com.proveperu.m02_inventario.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proveperu.m02_inventario.entity.MovimientoInventario;
import com.proveperu.m02_inventario.entity.Producto;
import com.proveperu.m02_inventario.entity.Stock;
import com.proveperu.m02_inventario.repository.MovimientoInventarioRepository;
import com.proveperu.m02_inventario.repository.ProductoRepository;
import com.proveperu.m02_inventario.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioService {
    private final ProductoRepository productoRepository;
    private final StockRepository stockRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public List<Stock> listarStock() {
        return stockRepository.findAll();
    }

    public List<MovimientoInventario> listarMovimientos() {
        return movimientoInventarioRepository.findAll();
    }
}
