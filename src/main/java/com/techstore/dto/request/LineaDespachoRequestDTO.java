package com.techstore.dto.request;

import com.techstore.model.enums.TipoEmpaque;
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
@Schema(description = "Línea de despacho operable desde la PWA móvil durante la madrugada")
public class LineaDespachoRequestDTO {

    @Schema(description = "ID del estado de cuenta al que pertenece esta línea", example = "uuid-...")
    @NotNull(message = "El ID del estado de cuenta es obligatorio")
    private java.util.UUID estadoCuentaId;

    @Schema(description = "Peso bruto registrado en balanza (incluye tara de empaque)", example = "18.50")
    @NotNull(message = "El peso bruto es obligatorio")
    @Positive(message = "El peso bruto debe ser mayor a cero")
    @Digits(integer = 6, fraction = 2, message = "Formato inválido para peso bruto (máx. XXXXXX.XX)")
    private BigDecimal pesoBrutoKg;

    @Schema(description = "Tipo de empaque usado: BANDEJA o BOLSA")
    @NotNull(message = "El tipo de empaque es obligatorio")
    private TipoEmpaque tipoEmpaque;

    @Schema(description = "Cantidad de unidades de empaque (para cálculo de tara dinámica)", example = "2")
    @NotNull(message = "La cantidad de empaque es obligatoria")
    @Min(value = 0, message = "La cantidad de empaque no puede ser negativa")
    private Integer cantidadEmpaque;

    @Schema(description = "Cantidad de aves con tripa despachadas en este lote", example = "5")
    @NotNull(message = "La cantidad de pollos con tripa (CT) es obligatoria")
    @Min(value = 0, message = "Los pollos CT no pueden ser negativos")
    private Integer pollosCt;

    @Schema(description = "Cantidad de aves sin tripa despachadas en este lote", example = "3")
    @NotNull(message = "La cantidad de pollos sin tripa (ST) es obligatoria")
    @Min(value = 0, message = "Los pollos ST no pueden ser negativos")
    private Integer pollosSt;

    @Schema(description = "Si es true, el backend suma +350g por cada pollo sin tripa al peso neto final",
            example = "true")
    @NotNull(message = "El flag de peso fantasma es obligatorio (use false si no aplica)")
    private Boolean flagPesoFantasma;
}
