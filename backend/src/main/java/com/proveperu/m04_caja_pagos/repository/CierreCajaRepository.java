package com.proveperu.m04_caja_pagos.repository;

import com.proveperu.m04_caja_pagos.entity.CierreCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CierreCajaRepository extends JpaRepository<CierreCaja, Integer> {
}
