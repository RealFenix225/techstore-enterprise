package com.techstore.repository.avicola;

import com.techstore.model.avicola.AvTransaccionPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AvTransaccionPagoRepository extends JpaRepository<AvTransaccionPago, UUID> {
}
