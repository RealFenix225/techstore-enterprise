package com.techstore.dto.response;

import com.techstore.model.enums.TipoEmpaque;
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
@Schema(description = "Línea de despacho con el peso neto calculado por el backend")
public class LineaDespachoResponseDTO {

    private UUID id;
    private UUID estadoCuentaId;
    private BigDecimal pesoBrutoKg;
    private TipoEmpaque tipoEmpaque;
    private Integer cantidadEmpaque;
    private Integer pollosCt;
    private Integer pollosSt;
    private Boolean flagPesoFantasma;

    // Calculado por DespachoService:
    // pesoNeto = pesoBruto - (cantidadEmpaque * taraEmpaque) + (flagPesoFantasma ? (pollosSt * 0.35) : 0)
    @Schema(description = "Peso neto final calculado por el backend (incluye ajuste fantasma si aplica)",
            example = "17.25")
    private BigDecimal pesoNetoKg;

    private LocalDateTime createdAt;
}
