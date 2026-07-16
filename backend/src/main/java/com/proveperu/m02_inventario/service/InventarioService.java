package com.proveperu.m02_inventario.service;
import java.util.List;

import com.proveperu.m02_inventario.entity.Producto;
import org.springframework.stereotype.Service;

import com.proveperu.m02_inventario.entity.MovimientoInventario;

import com.proveperu.m02_inventario.entity.Stock;
import com.proveperu.m02_inventario.entity.TipoMovimientoInventario;
import com.proveperu.m02_inventario.repository.MovimientoInventarioRepository;
import com.proveperu.m02_inventario.repository.ProductoRepository;
import com.proveperu.m02_inventario.repository.StockRepository;
import com.proveperu.m02_inventario.repository.TipoMovimientoInventarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioService {
    private final ProductoRepository productoRepository;
    private final StockRepository stockRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

private final TipoMovimientoInventarioRepository tipoMovimientoInventarioRepository;

    public List<Producto> listarProductos() {

        return productoRepository.findAll();
    }

    public List<Stock> listarStock() {
        return stockRepository.findAll();
    }

    public List<MovimientoInventario> listarMovimientos() {
        return movimientoInventarioRepository.findAll();
    }
    public List<TipoMovimientoInventario> listarTiposMovimiento() {
    return tipoMovimientoInventarioRepository.findAll();
}
}
