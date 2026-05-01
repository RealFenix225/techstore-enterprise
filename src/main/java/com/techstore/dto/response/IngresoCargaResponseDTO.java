package com.techstore.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta completa del ingreso de carga registrado")
public class IngresoCargaResponseDTO {

    private UUID id;
    private UUID jornadaId;

    @Schema(description = "Nombre comercial del proveedor")
    private String proveedorNombre;

    private List<LineaIngresoResponseDTO> lineas;

    // Totales agregados calculados por el backend para la confirmación del operario
    @Schema(description = "Peso bruto total sumado de todas las líneas", example = "512.40")
    private BigDecimal pesoBrutoTotalKg;

    @Schema(description = "Peso neto total después de descontar todas las taras", example = "394.40")
    private BigDecimal pesoNetoTotalKg;

    @Schema(description = "Total de aves vivas teóricas disponibles para despacho", example = "428")
    private Integer avesVivasTotales;

    @Schema(description = "Total de pollos muertos/asfixiados a devolver al proveedor", example = "5")
    private Integer totalPollosAhogadosMuertos;

    private LocalDateTime createdAt;
}
