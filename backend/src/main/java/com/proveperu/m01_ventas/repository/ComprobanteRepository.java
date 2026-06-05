package com.proveperu.m01_ventas.repository;

import com.proveperu.m01_ventas.entity.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Integer> {

}
