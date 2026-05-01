package com.techstore.service;

import com.techstore.dto.request.FijarPrecioRequestDTO;
import com.techstore.dto.request.LineaDespachoRequestDTO;
import com.techstore.dto.request.TransaccionPagoRequestDTO;
import com.techstore.dto.response.FijarPrecioResponseDTO;
import com.techstore.dto.response.TransaccionPagoResponseDTO;
import com.techstore.exception.ResourceNotFoundException;
import com.techstore.exception.global.AvBusinessException;
import com.techstore.model.Provider;
import com.techstore.model.avicola.AvEstadoCuenta;
import com.techstore.model.avicola.AvLineaDespacho;
import com.techstore.model.avicola.AvTransaccionPago;
import com.techstore.model.enums.TipoPago;
import com.techstore.repository.ProviderRepository;
import com.techstore.repository.avicola.AvEstadoCuentaRepository;
import com.techstore.repository.avicola.AvTransaccionPagoRepository;
import com.techstore.service.avicola.component.PesoNetoCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CobranzaServiceImpl implements CobranzaService {

    private static final String ENTIDAD_ESTADO_CUENTA = "AvEstadoCuenta";
    private static final String ENTIDAD_PROVEEDOR     = "Provider";
    private static final int    ESCALA_DINERO         = 2;

    private final AvEstadoCuentaRepository estadoCuentaRepository;
    private final AvTransaccionPagoRepository transaccionPagoRepository;
    private final ProviderRepository        providerRepository;
    private final PesoNetoCalculator pesoNetoCalculator;

    // =========================================================================
    // OPERACIÓN 1 — Fijar Precio y Descuento (mediodía, CIERRE_PARCIAL)
    // =========================================================================

    @Override
    @Transactional
    public FijarPrecioResponseDTO fijarPrecioYDescuento(UUID estadoCuentaId,
                                                        FijarPrecioRequestDTO request) {

        log.info("[CobranzaService] Fijando precio para estadoCuentaId={} | precio={} | descuento={}",
                estadoCuentaId, request.getPrecio(), request.getDescuento());

        // PASO 1 — Recuperar o lanzar 404
        AvEstadoCuenta estadoCuenta = obtenerEstadoCuentaOLanzar(estadoCuentaId);

        // PASO 2 — Guardia AV-003: el precio solo se fija una vez.
        // Si ya fue fijado, el flujo de rectificación es un endpoint separado (ADMIN lo decide).
        // Aquí la regla es: madrugada = NULL, mediodía = valor. Nunca retrocede.
        if (estadoCuenta.getPrecioFijado() != null) {
            log.warn("[CobranzaService] Intento de re-fijar precio en estadoCuentaId={}. " +
                     "Precio actual: {}", estadoCuentaId, estadoCuenta.getPrecioFijado());
            throw new AvBusinessException(
                    AvBusinessException.ERR_PRECIO_YA_FIJADO,
                    String.format(
                        "El precio para este estado de cuenta ya fue fijado en S/ %.2f. " +
                        "Use el endpoint de rectificación si necesita aplicar un descuento adicional.",
                        estadoCuenta.getPrecioFijado()
                    )
            );
        }

        // PASO 3 — Actualizar la entidad (dirty checking de Hibernate persiste al commit)
        estadoCuenta.setPrecioFijado(request.getPrecio());
        estadoCuenta.setDescuentoRectificacion(request.getDescuento());
        AvEstadoCuenta guardado = estadoCuentaRepository.save(estadoCuenta);

        log.info("[CobranzaService] Precio fijado correctamente para estadoCuentaId={}", estadoCuentaId);

        // PASO 4 — Calcular totales para la respuesta de confirmación
        BigDecimal pesoNetoTotal = calcularPesoNetoTotal(guardado);
        BigDecimal deudaBruta    = pesoNetoTotal.multiply(request.getPrecio())
                                                .setScale(ESCALA_DINERO, RoundingMode.HALF_UP);
        BigDecimal deudaFinal    = deudaBruta.subtract(request.getDescuento())
                                             .setScale(ESCALA_DINERO, RoundingMode.HALF_UP);

        return FijarPrecioResponseDTO.builder()
                .estadoCuentaId(guardado.getId())
                .clienteNombreAlias(guardado.getCliente().getNombreAlias())
                .precioFijado(guardado.getPrecioFijado())
                .descuentoRectificacion(guardado.getDescuentoRectificacion())
                .pesoNetoTotalKg(pesoNetoTotal)
                .deudaBruta(deudaBruta)
                .deudaFinal(deudaFinal)
                .build();
    }

    // =========================================================================
    // OPERACIÓN 2 — Registrar Pago
    // =========================================================================

    @Override
    @Transactional
    public TransaccionPagoResponseDTO registrarPago(TransaccionPagoRequestDTO request) {

        log.info("[CobranzaService] Registrando pago: estadoCuentaId={} | tipo={} | monto={}",
                request.getEstadoCuentaId(), request.getTipoPago(), request.getMonto());

        // PASO 1 — Recuperar estado de cuenta o lanzar 404
        AvEstadoCuenta estadoCuenta = obtenerEstadoCuentaOLanzar(request.getEstadoCuentaId());

        // PASO 2 — Guardia AV-008: no se puede cobrar sin precio fijado.
        // Un pago sin precio es una deuda que no se puede cuadrar.
        if (estadoCuenta.getPrecioFijado() == null) {
            throw new AvBusinessException(
                    AvBusinessException.ERR_PRECIO_NO_FIJADO,
                    "No se puede registrar un pago sin que el precio por kg haya sido fijado. " +
                    "Ejecute el cierre parcial (fijación de precio) antes de cobrar."
            );
        }

        // PASO 3 — Guardia AV-006: triangulación exige proveedor destino.
        // Esta validación ocurre en la capa de servicio ANTES de tocar la BD,
        // cumpliendo el principio de fail-fast y evitando que el constraint de BD sea
        // la primera línea de defensa.
        if (TipoPago.TRIANGULACION_MAYORISTA.equals(request.getTipoPago())
                && request.getProveedorDestinoId() == null) {
            log.warn("[CobranzaService] Triangulación sin proveedorDestinoId para estadoCuentaId={}",
                    request.getEstadoCuentaId());
            throw new AvBusinessException(
                    AvBusinessException.ERR_TRIANGULACION_SIN_PROVEEDOR,
                    "El tipo de pago TRIANGULACION_MAYORISTA requiere especificar el proveedor " +
                    "que recibirá el dinero (proveedorDestinoId). " +
                    "Indique el ID del proveedor (ej: Redondos, San Fernando, etc.)."
            );
        }

        // PASO 4 — Guardia inversa: si NO es triangulación, el proveedor destino debe ser nulo.
        // Evita que un pago en efectivo "lleve" un proveedor por error del cliente.
        if (!TipoPago.TRIANGULACION_MAYORISTA.equals(request.getTipoPago())
                && request.getProveedorDestinoId() != null) {
            log.warn("[CobranzaService] tipoPago={} con proveedorDestinoId no nulo. " +
                     "Se ignorará el proveedor destino.", request.getTipoPago());
            // Decisión de diseño: warning + corrección silenciosa en lugar de error,
            // porque el ADMIN puede haber enviado el campo por accidente sin intención fraudulenta.
            request.setProveedorDestinoId(null);
        }

        // PASO 5 — Resolver el proveedor destino solo si aplica
        Provider proveedorDestino = resolverProveedorDestino(request);

        // PASO 6 — Construir y persistir la transacción
        AvTransaccionPago transaccion = AvTransaccionPago.builder()
                .estadoCuenta(estadoCuenta)
                .tipoPago(request.getTipoPago())
                .monto(request.getMonto())
                .proveedorDestino(proveedorDestino)
                .build();

        AvTransaccionPago persistida = transaccionPagoRepository.save(transaccion);

        log.info("[CobranzaService] Pago registrado con id={}. Tipo={}, Monto={}",
                persistida.getId(), persistida.getTipoPago(), persistida.getMonto());

        // PASO 7 — Calcular totales de cobranza para la respuesta
        return construirResponse(persistida, estadoCuenta, proveedorDestino);
    }

    // =========================================================================
    // MÉTODOS PRIVADOS
    // =========================================================================

    private AvEstadoCuenta obtenerEstadoCuentaOLanzar(UUID estadoCuentaId) {
        return estadoCuentaRepository.findById(estadoCuentaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ENTIDAD_ESTADO_CUENTA, "id", estadoCuentaId));
    }

    /**
     * Calcula el peso neto total del estado de cuenta reutilizando PesoNetoCalculator.
     * No duplica la fórmula: un único lugar de verdad para la matemática del negocio.
     */
    private BigDecimal calcularPesoNetoTotal(AvEstadoCuenta estadoCuenta) {
        return estadoCuenta.getLineasDespacho().stream()
                .map(linea -> pesoNetoCalculator.calcular(construirRequestDesdeEntidad(linea)))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(ESCALA_DINERO, RoundingMode.HALF_UP);
    }

    /**
     * Reconstruye un LineaDespachoRequestDTO desde una entidad persistida
     * para poder reutilizar PesoNetoCalculator sin duplicar la fórmula.
     */
    private LineaDespachoRequestDTO construirRequestDesdeEntidad(AvLineaDespacho linea) {
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
     * Resuelve el proveedor destino desde BD solo cuando el tipo de pago lo requiere.
     * Devuelve null para EFECTIVO y PUCHO_ARRASTRADO.
     */
    private Provider resolverProveedorDestino(TransaccionPagoRequestDTO request) {
        if (!TipoPago.TRIANGULACION_MAYORISTA.equals(request.getTipoPago())) {
            return null;
        }
        return providerRepository.findById(request.getProveedorDestinoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ENTIDAD_PROVEEDOR, "id", request.getProveedorDestinoId()));
    }

    /**
     * Calcula totales acumulados de pago y saldo pendiente para la respuesta.
     * El saldo solo existe si el precio ya fue fijado.
     */
    private TransaccionPagoResponseDTO construirResponse(AvTransaccionPago persistida,
                                                          AvEstadoCuenta estadoCuenta,
                                                          Provider proveedorDestino) {

        // Total pagado: suma de todas las transacciones (incluida la recién persistida)
        BigDecimal totalPagado = estadoCuenta.getTransacciones().stream()
                .map(AvTransaccionPago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(persistida.getMonto()) // la nueva aún no está en la colección lazy
                .setScale(ESCALA_DINERO, RoundingMode.HALF_UP);

        // Saldo pendiente = deudaFinal - totalPagado (solo si hay precio fijado)
        BigDecimal saldoPendiente = null;
        if (estadoCuenta.getPrecioFijado() != null) {
            BigDecimal pesoNetoTotal = calcularPesoNetoTotal(estadoCuenta);
            BigDecimal deudaFinal = pesoNetoTotal
                    .multiply(estadoCuenta.getPrecioFijado())
                    .subtract(estadoCuenta.getDescuentoRectificacion())
                    .setScale(ESCALA_DINERO, RoundingMode.HALF_UP);
            saldoPendiente = deudaFinal.subtract(totalPagado)
                    .setScale(ESCALA_DINERO, RoundingMode.HALF_UP);

            log.info("[CobranzaService] Saldo pendiente tras pago id={}: S/ {}",
                    persistida.getId(), saldoPendiente);
        }

        return TransaccionPagoResponseDTO.builder()
                .id(persistida.getId())
                .estadoCuentaId(estadoCuenta.getId())
                .tipoPago(persistida.getTipoPago())
                .monto(persistida.getMonto())
                .proveedorDestinoNombre(proveedorDestino != null ? proveedorDestino.getName() : null)
                .totalPagadoAcumulado(totalPagado)
                .saldoPendiente(saldoPendiente)
                .createdAt(persistida.getCreatedAt())
                .build();
    }
}
