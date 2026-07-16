package com.proveperu.m02_inventario.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proveperu.m02_inventario.dto.request.RegistrarProductoRequest;
import com.proveperu.m02_inventario.dto.response.InventarioDashboardResponse;
import com.proveperu.m02_inventario.dto.response.ProductoInventarioResponse;
import com.proveperu.m02_inventario.entity.MovimientoInventario;
import com.proveperu.m02_inventario.entity.Producto;
import com.proveperu.m02_inventario.entity.Stock;
import com.proveperu.m02_inventario.entity.TipoMovimientoInventario;
import com.proveperu.m02_inventario.repository.MovimientoInventarioRepository;
import com.proveperu.m02_inventario.repository.ProductoRepository;
import com.proveperu.m02_inventario.repository.StockRepository;
import com.proveperu.m02_inventario.repository.TipoMovimientoInventarioRepository;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import com.proveperu.shared.enums.EstadoLogico;
import com.proveperu.shared.exception.ValidationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {
    private static final String ESTADO_SIN_STOCK = "SIN_STOCK";
    private static final String ESTADO_STOCK_BAJO = "STOCK_BAJO";
    private static final String ESTADO_DISPONIBLE = "DISPONIBLE";
    private static final String ESTADO_TODOS = "TODOS";
    private final ProductoRepository productoRepository;
    private final StockRepository stockRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    

private final TipoMovimientoInventarioRepository tipoMovimientoInventarioRepository;

        @Transactional(readOnly = true)
    public InventarioDashboardResponse obtenerDashboard() {

        log.info("Consultando dashboard de inventario");

        List<Stock> stocks = obtenerStockProductosActivos();

        int totalProductos = stocks.size();

        int productosSinStock =
                (int) stocks.stream()
                        .filter(stock ->
                                determinarEstadoStock(stock)
                                        .equals(ESTADO_SIN_STOCK)
                        )
                        .count();

        int productosStockBajo =
                (int) stocks.stream()
                        .filter(stock ->
                                determinarEstadoStock(stock)
                                        .equals(ESTADO_STOCK_BAJO)
                        )
                        .count();

        int productosDisponibles =
                (int) stocks.stream()
                        .filter(stock ->
                                determinarEstadoStock(stock)
                                        .equals(ESTADO_DISPONIBLE)
                        )
                        .count();

        log.info(
                "Dashboard inventario consultado. Total: {}, Sin stock: {}, Stock bajo: {}, Disponible: {}",
                totalProductos,
                productosSinStock,
                productosStockBajo,
                productosDisponibles
        );

        return InventarioDashboardResponse.builder()
                .totalProductos(totalProductos)
                .productosSinStock(productosSinStock)
                .productosStockBajo(productosStockBajo)
                .productosDisponibles(productosDisponibles)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ProductoInventarioResponse> listarProductosInventario(
            String buscar,
            String estadoStock
    ) {

        log.info(
                "Listando productos de inventario. Buscar: {}, Estado stock: {}",
                buscar,
                estadoStock
        );

        String buscarNormalizado = normalizarTextoBusqueda(buscar);
        String estadoNormalizado = normalizarEstadoStock(estadoStock);

        List<ProductoInventarioResponse> productos =
                obtenerStockProductosActivos()
                        .stream()
                        .filter(stock ->
                                coincideBusqueda(
                                        stock,
                                        buscarNormalizado
                                )
                        )
                        .filter(stock ->
                                coincideEstadoStock(
                                        stock,
                                        estadoNormalizado
                                )
                        )
                        .map(this::mapearProductoInventario)
                        .collect(Collectors.toList());

        log.info(
                "Productos de inventario listados correctamente. Total: {}",
                productos.size()
        );

        return productos;
    }

    @Transactional
public ProductoInventarioResponse registrarProducto(
        RegistrarProductoRequest request
) {

    log.info(
            "Registrando producto en inventario. Codigo: {}, Nombre: {}",
            request.getCodigoProducto(),
            request.getNombreProducto()
    );

    String codigoLimpio =
            limpiarTextoObligatorio(
                    request.getCodigoProducto()
            ).toUpperCase(Locale.ROOT);

    String nombreLimpio =
            limpiarTextoObligatorio(
                    request.getNombreProducto()
            );

    String descripcionLimpia =
            limpiarTextoOpcional(
                    request.getDescripcion()
            );

    if (productoRepository.existsByCodigoProducto(codigoLimpio)) {
        throw new ValidationException(
                "Ya existe un producto registrado con ese código"
        );
    }

    LocalDateTime ahora = LocalDateTime.now();

    Producto producto = Producto.builder()
            .codigoProducto(codigoLimpio)
            .nombreProducto(nombreLimpio)
            .descripcion(descripcionLimpia)
            .unidadMedida(request.getUnidadMedida())
            .estadoFisico(EstadoActivoInactivo.ACTIVO)
            .build();

    producto.setEstadoLogico(EstadoLogico.ACTIVO);
    producto.setFechaHoraCreacion(ahora);
    producto.setFechaHoraActualizacion(ahora);

    Producto productoGuardado =
            productoRepository.save(
                    producto
            );

    Stock stock = Stock.builder()
            .producto(productoGuardado)
            .cantidadActual(request.getCantidadInicial())
            .stockMinimo(request.getStockMinimo())
            .fechaHoraActualizacion(ahora)
            .build();

    Stock stockGuardado =
            stockRepository.save(
                    stock
            );

    log.info(
            "Producto registrado correctamente. IdProducto: {}, Codigo: {}",
            productoGuardado.getIdProducto(),
            productoGuardado.getCodigoProducto()
    );

    return mapearProductoInventario(
            stockGuardado
    );
}

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

    private List<Stock> obtenerStockProductosActivos() {

        return stockRepository
                .findAllByProducto_EstadoLogicoAndProducto_EstadoFisicoOrderByProducto_NombreProductoAsc(
                        EstadoLogico.ACTIVO,
                        EstadoActivoInactivo.ACTIVO
                );
    }

    private ProductoInventarioResponse mapearProductoInventario(
            Stock stock
    ) {

        Producto producto = stock.getProducto();

        return ProductoInventarioResponse.builder()
                .idProducto(producto.getIdProducto())
                .codigoProducto(producto.getCodigoProducto())
                .nombreProducto(producto.getNombreProducto())
                .descripcion(producto.getDescripcion())
                .unidadMedida(producto.getUnidadMedida().name())
                .cantidadActual(stock.getCantidadActual())
                .stockMinimo(stock.getStockMinimo())
                .estadoStock(determinarEstadoStock(stock))
                .estadoProducto(producto.getEstadoFisico().name())
                .fechaHoraActualizacion(
                        stock.getFechaHoraActualizacion()
                )
                .build();
    }

    private String determinarEstadoStock(
            Stock stock
    ) {

        BigDecimal cantidadActual =
                valorSeguro(
                        stock.getCantidadActual()
                );

        BigDecimal stockMinimo =
                valorSeguro(
                        stock.getStockMinimo()
                );

        if (cantidadActual.compareTo(BigDecimal.ZERO) == 0) {
            return ESTADO_SIN_STOCK;
        }

        if (cantidadActual.compareTo(stockMinimo) <= 0) {
            return ESTADO_STOCK_BAJO;
        }

        return ESTADO_DISPONIBLE;
    }

    private boolean coincideBusqueda(
            Stock stock,
            String buscar
    ) {

        if (buscar == null || buscar.isBlank()) {
            return true;
        }

        Producto producto = stock.getProducto();

        String codigo =
                producto.getCodigoProducto() == null
                        ? ""
                        : producto.getCodigoProducto()
                                .toLowerCase(Locale.ROOT);

        String nombre =
                producto.getNombreProducto() == null
                        ? ""
                        : producto.getNombreProducto()
                                .toLowerCase(Locale.ROOT);

        return codigo.contains(buscar)
                || nombre.contains(buscar);
    }

    private boolean coincideEstadoStock(
            Stock stock,
            String estadoStock
    ) {

        if (estadoStock == null
                || estadoStock.isBlank()
                || ESTADO_TODOS.equals(estadoStock)) {
            return true;
        }

        return determinarEstadoStock(stock)
                .equals(estadoStock);
    }

    private String normalizarTextoBusqueda(
            String texto
    ) {

        if (texto == null || texto.trim().isBlank()) {
            return null;
        }

        return texto.trim()
                .toLowerCase(Locale.ROOT);
    }

    private String normalizarEstadoStock(
            String estadoStock
    ) {

        if (estadoStock == null || estadoStock.trim().isBlank()) {
            return ESTADO_TODOS;
        }

        String estado =
                estadoStock.trim()
                        .toUpperCase(Locale.ROOT);

        if (ESTADO_TODOS.equals(estado)
                || ESTADO_SIN_STOCK.equals(estado)
                || ESTADO_STOCK_BAJO.equals(estado)
                || ESTADO_DISPONIBLE.equals(estado)) {
            return estado;
        }

        throw new ValidationException(
                "Estado de stock no válido. Use TODOS, SIN_STOCK, STOCK_BAJO o DISPONIBLE"
        );
    }

    private String limpiarTextoObligatorio(
            String texto
    ) {

        return Objects.requireNonNull(texto)
                .trim();
    }

    private String limpiarTextoOpcional(
            String texto
    ) {

        if (texto == null || texto.trim().isBlank()) {
            return null;
        }

        return texto.trim();
    }

    private BigDecimal valorSeguro(
            BigDecimal valor
    ) {

        return valor == null
                ? BigDecimal.ZERO
                : valor;
    }
}
