package com.techstore.exception.global;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción raíz para violaciones de reglas de negocio del módulo avícola.
 * Se mapea a HTTP 422 Unprocessable Entity: los datos son sintácticamente
 * válidos pero violan una restricción semántica del dominio.
 *
 * Diferencia intencional con IllegalArgumentException:
 *   - IllegalArgumentException → error del programador / precondición técnica.
 *   - AvBusinessException      → regla de negocio violada por el operario/ADMIN.
 */
@Getter
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class AvBusinessException extends RuntimeException {

    private final String codigoError;

    public AvBusinessException(String codigoError, String mensaje) {
        super(mensaje);
        this.codigoError = codigoError;
    }

    // -------------------------------------------------------------------------
    // CATÁLOGO DE ERRORES DE DOMINIO — un solo lugar para todos los códigos
    // -------------------------------------------------------------------------

    // Motor de Madrugada (Despacho)
    public static final String ERR_PESO_FANTASMA_SIN_POLLOS_ST = "AV-001";
    public static final String ERR_JORNADA_CERRADA             = "AV-002";
    public static final String ERR_TARA_NO_CONFIGURADA         = "AV-004";
    public static final String ERR_PESO_NETO_NEGATIVO          = "AV-005";

    // Motor de Casa (Cobranza)
    public static final String ERR_PRECIO_YA_FIJADO            = "AV-003";
    public static final String ERR_TRIANGULACION_SIN_PROVEEDOR = "AV-006";
    public static final String ERR_CUADRE_DEUDA_NO_CIERRA      = "AV-007";
    public static final String ERR_PRECIO_NO_FIJADO            = "AV-008";
}
