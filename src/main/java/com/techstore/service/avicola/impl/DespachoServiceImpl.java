package com.techstore.service.avicola.impl;

import com.techstore.dto.request.LineaDespachoRequestDTO;
import com.techstore.dto.response.EstadoCuentaResponseDTO;
import com.techstore.dto.response.LineaDespachoResponseDTO;
import com.techstore.exception.ResourceNotFoundException;
import com.techstore.exception.global.AvBusinessException;
import com.techstore.model.avicola.AvEstadoCuenta;
import com.techstore.model.avicola.AvLineaDespacho;
import com.techstore.model.enums.EstadoJornada;
import com.techstore.repository.avicola.AvEstadoCuentaRepository;
import com.techstore.repository.avicola.AvLineaDespachoRepository;
import com.techstore.service.avicola.DespachoService;
import com.techstore.service.avicola.component.PesoNetoCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DespachoServiceImpl implements DespachoService {

    private static final String ENTIDAD_ESTADO_CUENTA = "AvEstadoCuenta";

    private final AvEstadoCuentaRepository estadoCuentaRepository;
    private final AvLineaDespachoRepository lineaDespachoRepository;
    private final PesoNetoCalculator pesoNetoCalculator;

    // -------------------------------------------------------------------------
    // OPERACIÓN PRINCIPAL: Motor de Madrugada
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public LineaDespachoResponseDTO registrarLineaDespacho(LineaDespachoRequestDTO request) {

        log.info("[DespachoService] Iniciando registro de línea de despacho para estadoCuentaId={}",
                request.getEstadoCuentaId());

        // PASO 1 — Recuperar el estado de cuenta. Lanza 404 si no existe.
        AvEstadoCuenta estadoCuenta = obtenerEstadoCuentaOLanzar(request.getEstadoCuentaId());

        // PASO 2 — Validar que la jornada está operativa.
        // Una jornada CERRADA ya cuadró caja; no se admiten más líneas.
        validarJornadaAbierta(estadoCuenta);

        // PASO 3 — Calcular el peso neto.
        // PesoNetoCalculator contiene la guardia AV-001 y AV-005.
        // Separado aquí del save() deliberadamente: si el cálculo falla, no hay escritura.
        BigDecimal pesoNetoKg = pesoNetoCalculator.calcular(request);

        log.info("[DespachoService] Peso neto calculado: {} kg para estadoCuentaId={}",
                pesoNetoKg, request.getEstadoCuentaId());

        // PASO 4 — Mapear DTO → Entidad.
        // El peso neto NO se persiste en av_linea_despacho (es calculado en runtime).
        // Se persiste el estado crudo que permite auditarlo y recalcular si cambia la tara.
        AvLineaDespacho lineaDespacho = mapearRequestAEntidad(request, estadoCuenta);

        // PASO 5 — Persistir dentro de la transacción.
        AvLineaDespacho lineaPersistida = lineaDespachoRepository.save(lineaDespacho);

        log.info("[DespachoService] Línea de despacho persistida con id={}. pesoNeto={} kg.",
                lineaPersistida.getId(), pesoNetoKg);

        // PASO 6 — Mapear Entidad + pesoNeto calculado → ResponseDTO.
        return mapearEntidadAResponse(lineaPersistida, pesoNetoKg);
    }

    // -------------------------------------------------------------------------
    // CONSULTA: Vista consolidada del estado de cuenta
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public EstadoCuentaResponseDTO consultarEstadoCuenta(UUID estadoCuentaId) {

        log.debug("[DespachoService] Consultando estado de cuenta id={}", estadoCuentaId);

        AvEstadoCuenta estadoCuenta = obtenerEstadoCuentaOLanzar(estadoCuentaId);

        // Recalcular el peso neto de cada línea en tiempo de consulta (no se persiste)
        List<LineaDespachoResponseDTO> lineasDTO = estadoCuenta.getLineasDespacho().stream()
                .map(linea -> {
                    LineaDespachoRequestDTO requestFicticioParaCalculo = construirRequestDesdEntidad(linea);
                    BigDecimal pesoNeto = pesoNetoCalculator.calcular(requestFicticioParaCalculo);
                    return mapearEntidadAResponse(linea, pesoNeto);
                })
                .toList();

        BigDecimal pesoNetoTotalKg = lineasDTO.stream()
                .map(LineaDespachoResponseDTO::getPesoNetoKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // La deuda solo puede calcularse si el ADMIN ya fijó el precio (mediodía)
        BigDecimal deudaBruta   = calcularDeudaBruta(estadoCuenta.getPrecioFijado(), pesoNetoTotalKg);
        BigDecimal deudaFinal   = calcularDeudaFinal(deudaBruta, estadoCuenta.getDescuentoRectificacion());

        return EstadoCuentaResponseDTO.builder()
                .id(estadoCuenta.getId())
                .jornadaId(estadoCuenta.getJornada().getId())
                .clienteId(estadoCuenta.getCliente().getId())
                .clienteNombreAlias(estadoCuenta.getCliente().getNombreAlias())
                .precioFijado(estadoCuenta.getPrecioFijado())
                .descuentoRectificacion(estadoCuenta.getDescuentoRectificacion())
                .lineasDespacho(lineasDTO)
                .pesoNetoTotalKg(pesoNetoTotalKg)
                .deudaBrutaCalculada(deudaBruta)
                .deudaFinalCalculada(deudaFinal)
                .build();
    }

    // =========================================================================
    // MÉTODOS PRIVADOS — cada uno con una responsabilidad única
    // =========================================================================

    /**
     * Recupera el estado de cuenta o lanza ResourceNotFoundException (404).
     * Centralizado para no duplicar el mensaje en cada método.
     */
    private AvEstadoCuenta obtenerEstadoCuentaOLanzar(UUID estadoCuentaId) {
        return estadoCuentaRepository.findById(estadoCuentaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ENTIDAD_ESTADO_CUENTA, "id", estadoCuentaId));
    }

    /**
     * Valida que la jornada padre del estado de cuenta no esté cerrada.
     * Lanza AV-002 si el estado es CERRADA.
     *
     * NOTA: una jornada en CIERRE_PARCIAL (precios fijados) todavía admite
     * despachos porque el operario puede haber olvidado registrar algún cliente.
     * Solo CERRADA es bloqueante.
     */
    private void validarJornadaAbierta(AvEstadoCuenta estadoCuenta) {
        EstadoJornada estadoJornada = estadoCuenta.getJornada().getEstado();
        if (EstadoJornada.CERRADA.equals(estadoJornada)) {
            log.warn("[DespachoService] Intento de despacho sobre jornada CERRADA. jornadaId={}",
                    estadoCuenta.getJornada().getId());
            throw new AvBusinessException(
                    AvBusinessException.ERR_JORNADA_CERRADA,
                    "No se pueden registrar despachos en una jornada que ya fue cerrada. " +
                    "Jornada: " + estadoCuenta.getJornada().getFechaOperativa()
            );
        }
    }

    /**
     * Mapea el DTO de request a la entidad JPA.
     * El pesoNeto calculado NO entra en la entidad (se persiste el estado crudo auditables).
     */
    private AvLineaDespacho mapearRequestAEntidad(LineaDespachoRequestDTO request,
                                                  AvEstadoCuenta estadoCuenta) {
        return AvLineaDespacho.builder()
                .estadoCuenta(estadoCuenta)
                .pesoBrutoKg(request.getPesoBrutoKg())
                .tipoEmpaque(request.getTipoEmpaque())
                .cantidadEmpaque(request.getCantidadEmpaque())
                .pollosCt(request.getPollosCt())
                .pollosSt(request.getPollosSt())
                .flagPesoFantasma(request.getFlagPesoFantasma())
                .build();
    }

    /**
     * Construye un LineaDespachoRequestDTO "ficticio" a partir de una entidad persistida.
     * Necesario para reutilizar PesoNetoCalculator en la consulta de estado de cuenta,
     * sin duplicar la fórmula de cálculo.
     */
    private LineaDespachoRequestDTO construirRequestDesdEntidad(AvLineaDespacho linea) {
        return LineaDespachoRequestDTO.builder()
                .estadoCuentaId(linea.getEstadoCuenta().getId())
                .pesoBrutoKg(linea.getPesoBrutoKg())
                .tipoEmpaque(linea.getTipoEmpaque())
                .cantidadEmpaque(linea.getCantidadEmpaque())
                .pollosCt(linea.getPollosCt())
                .pollosSt(linea.getPollosSt())
                .flagPesoFantasma(linea.getFlagPesoFantasma())
                .build();
    }

    /**
     * Mapea entidad + peso neto (calculado en runtime) al ResponseDTO final.
     */
    private LineaDespachoResponseDTO mapearEntidadAResponse(AvLineaDespacho linea,
                                                             BigDecimal pesoNetoKg) {
        return LineaDespachoResponseDTO.builder()
                .id(linea.getId())
                .estadoCuentaId(linea.getEstadoCuenta().getId())
                .pesoBrutoKg(linea.getPesoBrutoKg())
                .tipoEmpaque(linea.getTipoEmpaque())
                .cantidadEmpaque(linea.getCantidadEmpaque())
                .pollosCt(linea.getPollosCt())
                .pollosSt(linea.getPollosSt())
                .flagPesoFantasma(linea.getFlagPesoFantasma())
                .pesoNetoKg(pesoNetoKg)
                .createdAt(linea.getCreatedAt())
                .build();
    }

    /**
     * Calcula la deuda bruta.
     * Devuelve null si el precio aún no fue fijado (madrugada activa).
     */
    private BigDecimal calcularDeudaBruta(BigDecimal precioFijado, BigDecimal pesoNetoTotalKg) {
        if (precioFijado == null) {
            return null;
        }
        return pesoNetoTotalKg.multiply(precioFijado);
    }

    /**
     * Calcula la deuda final aplicando el descuento de rectificación del ADMIN.
     * Devuelve null si la deuda bruta aún no existe.
     */
    private BigDecimal calcularDeudaFinal(BigDecimal deudaBruta, BigDecimal descuentoRectificacion) {
        if (deudaBruta == null) {
            return null;
        }
        BigDecimal descuento = descuentoRectificacion != null ? descuentoRectificacion : BigDecimal.ZERO;
        return deudaBruta.subtract(descuento);
    }
}
