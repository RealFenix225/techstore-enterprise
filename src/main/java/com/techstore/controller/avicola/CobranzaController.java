package com.techstore.controller.avicola;


import com.techstore.dto.request.FijarPrecioRequestDTO;
import com.techstore.dto.request.TransaccionPagoRequestDTO;
import com.techstore.dto.response.FijarPrecioResponseDTO;
import com.techstore.dto.response.TransaccionPagoResponseDTO;
import com.techstore.service.CobranzaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/avicola/cobranzas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // Seguridad a nivel de clase: ningún endpoint escapa sin ADMIN
@Tag(name = "Cobranza", description = "Motor de Casa — fijación de precios y registro de pagos. Solo ADMIN.")
public class CobranzaController {

    private final CobranzaService cobranzaService;

    // -------------------------------------------------------------------------
    // PATCH /api/v1/avicola/cobranzas/{estadoCuentaId}/precio
    // Fija el precio por kg y el descuento de rectificación al mediodía.
    // PATCH es el verbo correcto: es una actualización parcial de un recurso,
    // no la creación de uno nuevo ni la sustitución completa (PUT).
    // -------------------------------------------------------------------------
    @Operation(
        summary = "Fijar precio y descuento de rectificación",
        description = "Fija el precio por kg para un estado de cuenta. " +
                      "Solo se puede ejecutar una vez (AV-003 si ya fue fijado). " +
                      "Hace avanzar la jornada hacia CIERRE_PARCIAL."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Precio fijado. Deuda calculada devuelta."),
        @ApiResponse(responseCode = "400", description = "Validación de campos fallida"),
        @ApiResponse(responseCode = "404", description = "Estado de cuenta no encontrado"),
        @ApiResponse(responseCode = "422", description = "[AV-003] Precio ya fijado previamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado — se requiere rol ADMIN")
    })
    @PatchMapping("/{estadoCuentaId}/precio")
    public ResponseEntity<FijarPrecioResponseDTO> fijarPrecioYDescuento(
            @PathVariable UUID estadoCuentaId,
            @Valid @RequestBody FijarPrecioRequestDTO request) {

        log.info("[CobranzaController] PATCH /{}/precio | precio={} | descuento={}",
                estadoCuentaId, request.getPrecio(), request.getDescuento());

        FijarPrecioResponseDTO response = cobranzaService.fijarPrecioYDescuento(estadoCuentaId, request);

        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------------------
    // POST /api/v1/avicola/cobranzas/pagos
    // Registra un abono (puede ser parcial). Se puede llamar múltiples veces
    // hasta que el saldo llegue a cero (cuadre final de caja).
    // -------------------------------------------------------------------------
    @Operation(
        summary = "Registrar transacción de pago",
        description = "Abona un pago contra el estado de cuenta. Puede ser parcial. " +
                      "Si el tipo es TRIANGULACION_MAYORISTA, el campo proveedorDestinoId es obligatorio (AV-006). " +
                      "Requiere que el precio haya sido fijado previamente (AV-008)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pago registrado. Saldo pendiente actualizado."),
        @ApiResponse(responseCode = "400", description = "Validación de campos fallida"),
        @ApiResponse(responseCode = "404", description = "Estado de cuenta o proveedor no encontrado"),
        @ApiResponse(responseCode = "422", description = "[AV-006] Triangulación sin proveedor | [AV-008] Precio no fijado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado — se requiere rol ADMIN")
    })
    @PostMapping("/pagos")
    public ResponseEntity<TransaccionPagoResponseDTO> registrarPago(
            @Valid @RequestBody TransaccionPagoRequestDTO request) {

        log.info("[CobranzaController] POST /pagos | estadoCuentaId={} | tipo={} | monto={}",
                request.getEstadoCuentaId(), request.getTipoPago(), request.getMonto());

        TransaccionPagoResponseDTO response = cobranzaService.registrarPago(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
