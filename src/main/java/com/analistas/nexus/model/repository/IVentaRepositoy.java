package com.analistas.nexus.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.analistas.nexus.model.entities.Venta;

public interface IVentaRepositoy extends JpaRepository<Venta, Long>{

    @Query("select v from Venta v Where v.activo = true")
    List<Venta> buscarSoloHabilitados( );
    
}
