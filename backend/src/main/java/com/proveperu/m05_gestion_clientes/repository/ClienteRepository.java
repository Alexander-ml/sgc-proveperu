package com.proveperu.m05_gestion_clientes.repository;

import com.proveperu.m05_gestion_clientes.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}
