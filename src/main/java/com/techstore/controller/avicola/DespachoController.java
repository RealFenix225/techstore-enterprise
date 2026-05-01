package com.techstore.controller.avicola;

import com.techstore.dto.request.LineaDespachoRequestDTO;
import com.techstore.dto.response.EstadoCuentaResponseDTO;
import com.techstore.dto.response.LineaDespachoResponseDTO;
import com.techstore.service.avicola.DespachoService;
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
@RequestMapping("/api/v1/avicola/despachos")
@RequiredArgsConstructor
@Tag(name = "Despacho", description = "Motor de Madrugada — registro de despachos por el operario en turno")
public class DespachoController {

    private final DespachoService despachoService;

    // -------------------------------------------------------------------------
    // POST /api/v1/avicola/despachos
    // Registra una línea de despacho durante el turno de madrugada.
    // Disponible para OPERARIO y ADMIN; los precios pueden estar aún sin fijar.
    // -------------------------------------------------------------------------
    @Operation(
        summary = "Registrar línea de despacho",
        description = "Persiste una línea de despacho calculando el peso neto " +
                      "(tara + ajuste fantasma) en el backend. " +
                      "El precio puede ser nulo si aún no es mediodía (CIERRE_PARCIAL)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Línea de despacho registrada correctamente"),
        @ApiResponse(responseCode = "400", description = "Validación de campos fallida"),
        @ApiResponse(responseCode = "404", description = "Estado de cuenta no encontrado"),
        @ApiResponse(responseCode = "422", description = "Regla de negocio violada (ej. peso fantasma sin pollos ST)"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado — rol insuficiente")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN', 'OPERARIO', 'ROLE_OPERARIO')")
    public ResponseEntity<LineaDespachoResponseDTO> registrarLineaDespacho(
            @Valid @RequestBody LineaDespachoRequestDTO request) {

        log.info("[DespachoController] POST /despachos — estadoCuentaId={}",
                request.getEstadoCuentaId());

        LineaDespachoResponseDTO response = despachoService.registrarLineaDespacho(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/avicola/despachos/estado-cuenta/{estadoCuentaId}
    // Vista consolidada: todas las líneas + totales calculados + deuda (si hay precio).
    // Solo ADMIN puede consultar el cuadre completo de un cliente.
    // -------------------------------------------------------------------------
    @Operation(
        summary = "Consultar estado de cuenta consolidado",
        description = "Devuelve todas las líneas de despacho del cliente en la jornada, " +
                      "con el peso neto total y la deuda calculada. " +
                      "La deuda será nula si el precio aún no ha sido fijado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado de cuenta devuelto correctamente"),
        @ApiResponse(responseCode = "404", description = "Estado de cuenta no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado — rol insuficiente")
    })
    @GetMapping("/estado-cuenta/{estadoCuentaId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<EstadoCuentaResponseDTO> consultarEstadoCuenta(
            @PathVariable UUID estadoCuentaId) {

        log.info("[DespachoController] GET /despachos/estado-cuenta/{}", estadoCuentaId);

        EstadoCuentaResponseDTO response = despachoService.consultarEstadoCuenta(estadoCuentaId);

        return ResponseEntity.ok(response);
    }
}
