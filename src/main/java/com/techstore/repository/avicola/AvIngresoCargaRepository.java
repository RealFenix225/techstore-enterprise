package com.techstore.repository.avicola;

import com.techstore.model.avicola.AvIngresoCarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AvIngresoCargaRepository extends JpaRepository<AvIngresoCarga, UUID> {
}
