package com.proveperu.m01_ventas.repository;

import com.proveperu.m01_ventas.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
}
