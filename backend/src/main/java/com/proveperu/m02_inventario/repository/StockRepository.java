package com.proveperu.m02_inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m02_inventario.entity.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {

}
