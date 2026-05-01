package com.techstore.dto.response;

import com.techstore.model.enums.TipoPago;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Transacción de pago registrada, con saldo pendiente actualizado.")
public class TransaccionPagoResponseDTO {

    private UUID id;
    private UUID estadoCuentaId;
    private TipoPago tipoPago;
    private BigDecimal monto;

    @Schema(description = "Nombre del proveedor que recibió el dinero. " +
                          "Nulo si el tipo de pago no es TRIANGULACION_MAYORISTA.")
    private String proveedorDestinoNombre;

    @Schema(description = "Suma total de pagos registrados para este estado de cuenta hasta ahora.",
            example = "700.00")
    private BigDecimal totalPagadoAcumulado;

    @Schema(description = "Saldo pendiente de cobro tras este pago. " +
                          "Nulo si el precio aún no fue fijado.", example = "393.50")
    private BigDecimal saldoPendiente;

    private LocalDateTime createdAt;
}
