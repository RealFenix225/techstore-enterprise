package com.techstore.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Esto será el PRIMERO en ejecutarse
@Slf4j // Lombok añade la variable 'log'
public class RequestLoggingFilter implements Filter {

    private static final String CORRELATION_ID_KEY = "correlationId";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 1. GENERAR IDENTIFICACIÓN ÚNICA (La "Placa" del soldado)
        String correlationId = UUID.randomUUID().toString();

        try {
            // 2. METERLO EN EL MDC (Mapped Diagnostic Context)
            // El MDC es como una "mochila" que lleva el hilo de ejecución.
            // Todo log que se haga en este hilo llevará este ID automáticamente.
            MDC.put(CORRELATION_ID_KEY, correlationId);

            // 3. AGREGAR A LA RESPUESTA
            // Así el Frontend (mi web) sabe qué ID tuvo su petición.
            res.setHeader(CORRELATION_ID_HEADER, correlationId);

            // 4. LOG DE ENTRADA (Start Timer)
            long startTime = System.currentTimeMillis();
            log.info("Incoming Request: [{} {}]", req.getMethod(), req.getRequestURI());

            // 5. Esto deja pasar LA PETICIÓN (Hacia el Controller)
            chain.doFilter(request, response);

            // 6. LOG DE SALIDA (Stop Timer)
            long duration = System.currentTimeMillis() - startTime;
            log.info("Completed Request: [{} {}] - Status: {} - Time: {}ms",
                    req.getMethod(), req.getRequestURI(), res.getStatus(), duration);

        } finally {
            // 7. LIMPIEZA OBLIGATORIA
            // Los hilos se reutilizan. Si no se limpia la mochila, el próximo usuario
            // tendrá el ID del anterior.
            MDC.remove(CORRELATION_ID_KEY);
        }
    }
}