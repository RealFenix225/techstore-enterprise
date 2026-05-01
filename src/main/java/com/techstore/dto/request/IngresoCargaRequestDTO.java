package com.techstore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cabecera del ingreso de carga mayorista. Agrupa las líneas de partida por proveedor.")
public class IngresoCargaRequestDTO {

    @Schema(description = "ID de la jornada diaria activa (estado ABIERTA)", example = "a1b2c3d4-...")
    @NotNull(message = "El ID de la jornada es obligatorio")
    private UUID jornadaId;

    @Schema(description = "ID del proveedor mayorista (tabla techstore_provider)", example = "e5f6g7h8-...")
    @NotNull(message = "El ID del proveedor es obligatorio")
    private UUID proveedorId;

    @Schema(description = "Líneas de partida de jabas. Debe contener al menos una.")
    @NotEmpty(message = "El ingreso de carga debe tener al menos una línea de partida")
    @Valid
    private List<LineaIngresoRequestDTO> lineas;
}
