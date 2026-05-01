package com.techstore.repository.avicola;

import com.techstore.model.avicola.AvJornadaDiaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AvJornadaDiariaRepository extends JpaRepository<AvJornadaDiaria, UUID> {
}
