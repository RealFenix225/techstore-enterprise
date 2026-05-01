package com.techstore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Fijación del precio por kg y descuento de rectificación para un estado de cuenta. " +
                      "Solo accesible por ADMIN al mediodía (CIERRE_PARCIAL de jornada).")
public class FijarPrecioRequestDTO {

    @Schema(description = "Precio por kg fijado por el ADMIN", example = "7.50")
    @NotNull(message = "El precio por kg es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    @Digits(integer = 4, fraction = 2, message = "Formato inválido para precio (máx. XXXX.XX)")
    private BigDecimal precio;

    @Schema(description = "Descuento de rectificación manual aplicado a la deuda total. " +
                          "Use 0 si no aplica ningún descuento.", example = "20.00")
    @NotNull(message = "El descuento es obligatorio (use 0.00 si no aplica)")
    @PositiveOrZero(message = "El descuento no puede ser negativo")
    @Digits(integer = 8, fraction = 2, message = "Formato inválido para descuento (máx. XXXXXXXX.XX)")
    private BigDecimal descuento;
}
