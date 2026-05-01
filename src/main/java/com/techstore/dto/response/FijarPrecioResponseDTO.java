package com.techstore.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Confirmación del estado de cuenta tras fijar precio y descuento.")
public class FijarPrecioResponseDTO {

    private UUID estadoCuentaId;
    private String clienteNombreAlias;

    private BigDecimal precioFijado;
    private BigDecimal descuentoRectificacion;

    @Schema(description = "Peso neto total calculado de todas las líneas de despacho.", example = "145.80")
    private BigDecimal pesoNetoTotalKg;

    @Schema(description = "Deuda bruta: pesoNetoTotal × precioFijado.", example = "1093.50")
    private BigDecimal deudaBruta;

    @Schema(description = "Deuda final: deudaBruta − descuento.", example = "1073.50")
    private BigDecimal deudaFinal;
}
