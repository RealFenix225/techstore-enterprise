package com.techstore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalle de una partida de jabas dentro de un ingreso de carga")
public class LineaIngresoRequestDTO {

    @Schema(description = "Cantidad de jabas con pollos macho", example = "10")
    @NotNull(message = "La cantidad de jabas macho es obligatoria")
    @Min(value = 0, message = "Las jabas macho no pueden ser negativas")
    private Integer jabasMacho;

    @Schema(description = "Cantidad de jabas con pollos hembra", example = "8")
    @NotNull(message = "La cantidad de jabas hembra es obligatoria")
    @Min(value = 0, message = "Las jabas hembra no pueden ser negativas")
    private Integer jabasHembra;

    @Schema(description = "Peso bruto total de la partida en kg (sin descontar tara)", example = "245.60")
    @NotNull(message = "El peso bruto es obligatorio")
    @Positive(message = "El peso bruto debe ser mayor a cero")
    @Digits(integer = 6, fraction = 2, message = "Formato inválido para peso bruto (máx. XXXXXX.XX)")
    private BigDecimal pesoBrutoKg;

    @Schema(description = "Tara por jaba en kg. Por defecto 7.00 kg. Editable si hay mezcla de proveedores.",
            example = "7.00")
    @NotNull(message = "La tara unitaria de jaba es obligatoria")
    @DecimalMin(value = "5.00", message = "La tara de jaba no puede ser menor a 5.00 kg (valor irreal)")
    @DecimalMax(value = "15.00", message = "La tara de jaba no puede exceder 15.00 kg (valor irreal)")
    @Digits(integer = 2, fraction = 2, message = "Formato inválido para tara (máx. XX.XX)")
    private BigDecimal taraUnitariaJaba;

    @Schema(description = "Cantidad de pollos muertos o asfixiados en tránsito. Se devuelven al proveedor.",
            example = "3")
    @NotNull(message = "El campo pollos ahogados/muertos es obligatorio (use 0 si no aplica)")
    @Min(value = 0, message = "Los pollos ahogados/muertos no pueden ser negativos")
    private Integer pollosAhogadosMuertos;
}
