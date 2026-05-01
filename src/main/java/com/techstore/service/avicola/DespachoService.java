package com.techstore.service.avicola;


import com.techstore.dto.request.LineaDespachoRequestDTO;
import com.techstore.dto.response.EstadoCuentaResponseDTO;
import com.techstore.dto.response.LineaDespachoResponseDTO;

import java.util.UUID;

/**
 * Contrato del Motor de Madrugada.
 * Gestiona el registro de despachos durante el turno nocturno del operario.
 *
 * Separar interfaz de implementación permite:
 *   - Testear controllers con mocks sin levantar contexto de BD.
 *   - Sustituir implementaciones (ej. implementación offline) sin tocar llamadores.
 */
public interface DespachoService {

    /**
     * Registra una línea de despacho individual asociada a un estado de cuenta.
     * Calcula el peso neto aplicando la fórmula oficial (tara + fantasma).
     *
     * @param request datos crudos del operario desde la PWA móvil
     * @return línea persistida con peso neto calculado
     * @throws com.techstore.exception.ResourceNotFoundException si el estado de cuenta no existe
     * @throws com.techstore.avicola.exception.AvBusinessException por violaciones de reglas de negocio
     */
    LineaDespachoResponseDTO registrarLineaDespacho(LineaDespachoRequestDTO request);

    /**
     * Devuelve la vista consolidada del estado de cuenta de un cliente:
     * todas sus líneas de despacho con totales calculados.
     *
     * @param estadoCuentaId UUID del estado de cuenta
     * @return DTO consolidado con peso neto total y deuda calculada (si hay precio fijado)
     */
    EstadoCuentaResponseDTO consultarEstadoCuenta(UUID estadoCuentaId);
}
