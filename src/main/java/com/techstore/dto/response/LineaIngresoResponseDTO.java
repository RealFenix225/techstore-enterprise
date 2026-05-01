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
@Schema(description = "Detalle de una línea de ingreso con pesos calculados por el backend")
public class LineaIngresoResponseDTO {

    @Schema(description = "ID único de la línea", example = "uuid-...")
    private UUID id;

    private Integer jabasMacho;
    private Integer jabasHembra;
    private BigDecimal pesoBrutoKg;
    private BigDecimal taraUnitariaJaba;
    private Integer pollosAhogadosMuertos;

    // Calculado por el backend: pesoBruto - ((jabasMacho + jabasHembra) * taraUnitariaJaba)
    @Schema(description = "Peso neto calculado después de descontar la tara total de jabas", example = "177.60")
    private BigDecimal pesoNetoKg;

    // Calculado por InventarioService: (jabasMacho * CONST_MACHO) + (jabasHembra * CONST_HEMBRA) - pollosAhogados
    @Schema(description = "Aves teóricas vivas disponibles (descuenta muertos/asfixiados)", example = "215")
    private Integer avesViajesTeoricas;
}
