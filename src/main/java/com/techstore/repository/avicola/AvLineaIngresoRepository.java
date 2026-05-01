package com.techstore.repository.avicola;

import com.techstore.model.avicola.AvLineaIngreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AvLineaIngresoRepository extends JpaRepository<AvLineaIngreso, UUID> {
}
