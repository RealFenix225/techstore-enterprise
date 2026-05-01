package com.techstore.dto.request;

import com.techstore.model.enums.TipoPago;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Schema(description = "Registro de una transacción de pago contra un estado de cuenta. " +
                      "Si el tipo es TRIANGULACION_MAYORISTA, el campo proveedorDestinoId es obligatorio.")
public class TransaccionPagoRequestDTO {

    @Schema(description = "ID del estado de cuenta que se está saldando", example = "uuid-...")
    @NotNull(message = "El ID del estado de cuenta es obligatorio")
    private UUID estadoCuentaId;

    @Schema(description = "Tipo de pago: EFECTIVO | TRIANGULACION_MAYORISTA | PUCHO_ARRASTRADO")
    @NotNull(message = "El tipo de pago es obligatorio")
    private TipoPago tipoPago;

    @Schema(description = "Monto abonado en esta transacción", example = "350.00")
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    @Digits(integer = 8, fraction = 2, message = "Formato inválido para monto (máx. XXXXXXXX.XX)")
    private BigDecimal monto;

    @Schema(description = "ID del proveedor que recibe el dinero. " +
                          "OBLIGATORIO si tipoPago es TRIANGULACION_MAYORISTA, nulo en los demás casos.",
            example = "uuid-proveedor-...")
    private Long proveedorDestinoId;
}
