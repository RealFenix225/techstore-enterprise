package com.techstore.service;



import com.techstore.dto.request.FijarPrecioRequestDTO;
import com.techstore.dto.request.TransaccionPagoRequestDTO;
import com.techstore.dto.response.FijarPrecioResponseDTO;
import com.techstore.dto.response.TransaccionPagoResponseDTO;

import java.util.UUID;

/**
 * Contrato del Motor de Casa.
 * Gestiona la fijación de precios y el registro de pagos.
 * Todas las operaciones son exclusivas del rol ADMIN.
 */
public interface CobranzaService {

    /**
     * Fija el precio por kg y el descuento de rectificación para un estado de cuenta.
     * Solo puede ejecutarse una vez por estado de cuenta (precio nulo → no nulo).
     *
     * @param estadoCuentaId UUID del estado de cuenta a actualizar
     * @param request        DTO con precio y descuento validados
     * @return resumen con deuda calculada tras el cierre parcial
     * @throws com.techstore.exception.ResourceNotFoundException si el estado de cuenta no existe
     * @throws com.techstore.avicola.exception.AvBusinessException AV-003 si el precio ya fue fijado
     */
    FijarPrecioResponseDTO fijarPrecioYDescuento(UUID estadoCuentaId, FijarPrecioRequestDTO request);

    /**
     * Registra una transacción de pago (abono) contra un estado de cuenta.
     * Valida la coherencia de triangulación antes de persistir.
     *
     * @param request DTO con tipo de pago, monto y proveedor destino (si aplica)
     * @return transacción persistida con saldo pendiente actualizado
     * @throws com.techstore.exception.ResourceNotFoundException si el estado de cuenta o proveedor no existen
     * @throws com.techstore.avicola.exception.AvBusinessException AV-006 si triangulación sin proveedor
     * @throws com.techstore.avicola.exception.AvBusinessException AV-008 si el precio aún no fue fijado
     */
    TransaccionPagoResponseDTO registrarPago(TransaccionPagoRequestDTO request);
}
