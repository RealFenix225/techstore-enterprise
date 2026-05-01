package com.techstore.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Vista consolidada del estado de cuenta de un cliente en una jornada. " +
        "El precio puede ser nulo durante la madrugada.")
public class EstadoCuentaResponseDTO {

    private UUID id;
    private UUID jornadaId;
    private UUID clienteId;

    @Schema(description = "Alias de batalla del cliente", example = "El Chino Covida")
    private String clienteNombreAlias;

    // NULL hasta que el ADMIN fije el precio al mediodía (CIERRE_PARCIAL)
    @Schema(description = "Precio por kg fijado al mediodía. NULL durante la madrugada.", example = "7.50")
    private BigDecimal precioFijado;

    @Schema(description = "Descuento manual aplicado por el ADMIN", example = "20.00")
    private BigDecimal descuentoRectificacion;

    private List<LineaDespachoResponseDTO> lineasDespacho;

    // Calculados por el backend al momento de construir la respuesta
    @Schema(description = "Suma del peso neto de todas las líneas de despacho", example = "145.80")
    private BigDecimal pesoNetoTotalKg;

    // NULL si precioFijado aún es NULL (madrugada)
    @Schema(description = "Deuda bruta calculada: pesoNetoTotal * precioFijado. NULL si precio no fijado.",
            example = "1093.50")
    private BigDecimal deudaBrutaCalculada;

    // NULL si precioFijado aún es NULL
    @Schema(description = "Deuda final: deudaBruta - descuentoRectificacion. NULL si precio no fijado.",
            example = "1073.50")
    private BigDecimal deudaFinalCalculada;
}
