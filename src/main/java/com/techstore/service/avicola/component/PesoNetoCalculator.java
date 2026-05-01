package com.techstore.service.avicola.component;

import com.techstore.dto.request.LineaDespachoRequestDTO;
import com.techstore.exception.global.AvBusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Encapsula TODA la matemática del peso neto de despacho.
 *
 * Fórmula oficial (documentada en arquitectura técnica):
 *   pesoNeto = pesoBruto
 *              - (cantidadEmpaque × taraUnitaria)
 *              + (flagPesoFantasma ? (pollosSt × 0.350) : 0)
 *
 * Reglas de integridad que valida ANTES de calcular:
 *   AV-001 — peso fantasma activado sin pollos ST registrados.
 *   AV-005 — peso neto resultante negativo (peso bruto insuficiente para cubrir la tara).
 *
 * Se extrae del servicio para:
 *   a) Testeo unitario sin contexto Spring (sin mocks de repositorio).
 *   b) Reutilización futura desde IngresoCargaService si se añade lógica similar.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PesoNetoCalculator {

    private static final BigDecimal PESO_FANTASMA_POR_POLLO_ST = new BigDecimal("0.350");
    private static final int        ESCALA_OPERACIONAL          = 3; // precisión interna
    private static final int        ESCALA_RESULTADO            = 2; // escala de retorno al negocio

    private final TaraEmpaqueResolver taraEmpaqueResolver;

    /**
     * Calcula el peso neto a partir de los datos crudos del request de despacho.
     *
     * @param request DTO validado por Jakarta (pesos positivos, enums no nulos)
     * @return peso neto en kg con 2 decimales (HALF_UP)
     * @throws AvBusinessException AV-001 si flagPesoFantasma=true y pollosSt=0
     * @throws AvBusinessException AV-005 si el peso neto resultante es negativo o cero
     */
    public BigDecimal calcular(LineaDespachoRequestDTO request) {

        // Regla AV-001: guardia temprana antes de cualquier cálculo
        if (Boolean.TRUE.equals(request.getFlagPesoFantasma()) && request.getPollosSt() == 0) {
            throw new AvBusinessException(
                    AvBusinessException.ERR_PESO_FANTASMA_SIN_POLLOS_ST,
                    "No se puede activar el peso fantasma sin registrar pollos sin tripa (ST). " +
                    "Registre la cantidad de pollos ST o desactive el flag."
            );
        }

        BigDecimal taraUnitaria = taraEmpaqueResolver.resolveTara(request.getTipoEmpaque());

        BigDecimal taraTotalEmpaque = taraUnitaria
                .multiply(BigDecimal.valueOf(request.getCantidadEmpaque()))
                .setScale(ESCALA_OPERACIONAL, RoundingMode.HALF_UP);

        BigDecimal ajustePesoFantasma = Boolean.TRUE.equals(request.getFlagPesoFantasma())
                ? PESO_FANTASMA_POR_POLLO_ST
                        .multiply(BigDecimal.valueOf(request.getPollosSt()))
                        .setScale(ESCALA_OPERACIONAL, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal pesoNeto = request.getPesoBrutoKg()
                .subtract(taraTotalEmpaque)
                .add(ajustePesoFantasma)
                .setScale(ESCALA_RESULTADO, RoundingMode.HALF_UP);

        log.debug(
            "[PesoNetoCalculator] pesoBruto={} kg | taraTotal={} kg | ajusteFantasma={} kg => pesoNeto={} kg",
            request.getPesoBrutoKg(), taraTotalEmpaque, ajustePesoFantasma, pesoNeto
        );

        // Regla AV-005: el peso bruto no alcanza para cubrir la tara (datos incorrectos del operario)
        if (pesoNeto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AvBusinessException(
                    AvBusinessException.ERR_PESO_NETO_NEGATIVO,
                    String.format(
                        "El peso neto calculado (%.2f kg) es inválido. " +
                        "Verifique el peso bruto (%.2f kg) y la cantidad de empaques (%d).",
                        pesoNeto, request.getPesoBrutoKg(), request.getCantidadEmpaque()
                    )
            );
        }

        return pesoNeto;
    }
}
