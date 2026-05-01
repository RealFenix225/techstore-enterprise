package com.techstore.service.avicola.component;

import com.techstore.exception.global.AvBusinessException;
import com.techstore.model.enums.TipoEmpaque;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

/**
 * Resuelve la tara por unidad de empaque según el tipo.
 *
 * DISEÑO INTENCIONAL:
 * Hoy los valores están en un EnumMap interno para no bloquear el sprint por la
 * tabla av_tara_empaque (que fue identificada como gap en el análisis de DBA).
 * El contrato del método resolveTara(TipoEmpaque) NO cambia cuando se migre a BD:
 * solo se reemplaza el EnumMap por un repositorio en esta misma clase.
 * El DespachoServiceImpl no necesitará modificarse — principio Open/Closed.
 *
 * TODO: Cuando se cree AvTaraEmpaqueRepository, inyectarlo aquí y reemplazar
 *       el EnumMap por: repo.findVigenteByTipoEmpaque(tipo).orElseThrow(...)
 */
@Slf4j
@Component
public class TaraEmpaqueResolver {

    // Tara en kg por unidad de empaque. Fuente: datos operacionales del cliente.
    private static final Map<TipoEmpaque, BigDecimal> TARAS_CONFIGURADAS = new EnumMap<>(TipoEmpaque.class);

    static {
        TARAS_CONFIGURADAS.put(TipoEmpaque.BANDEJA, new BigDecimal("0.35"));
        TARAS_CONFIGURADAS.put(TipoEmpaque.BOLSA,   new BigDecimal("0.05"));
    }

    /**
     * @param tipoEmpaque tipo de empaque de la línea de despacho
     * @return tara unitaria en kg configurada para ese tipo
     * @throws AvBusinessException si el tipo no tiene tara configurada (AV-004)
     */
    public BigDecimal resolveTara(TipoEmpaque tipoEmpaque) {
        BigDecimal tara = TARAS_CONFIGURADAS.get(tipoEmpaque);
        if (tara == null) {
            log.error("[TaraEmpaqueResolver] Tara no configurada para tipo de empaque: {}", tipoEmpaque);
            throw new AvBusinessException(
                    AvBusinessException.ERR_TARA_NO_CONFIGURADA,
                    "No existe tara configurada para el tipo de empaque: " + tipoEmpaque
            );
        }
        log.debug("[TaraEmpaqueResolver] Tara resuelta para {}: {} kg/unidad", tipoEmpaque, tara);
        return tara;
    }
}
