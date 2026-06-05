package com.proveperu.m03_compras.repository;

import com.proveperu.m03_compras.entity.DetalleCompra;
import com.proveperu.m03_compras.entity.DetalleCompraId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, DetalleCompraId> {
}
