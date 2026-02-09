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
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestLoggingFilter implements Filter {

    private static final String CORRELATION_ID_KEY = "correlationId";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Generar ID único para trazar la petición
        String correlationId = UUID.randomUUID().toString();

        try {
            // Agregar ID al contexto de logs (MDC) y al header de respuesta
            MDC.put(CORRELATION_ID_KEY, correlationId);
            res.setHeader(CORRELATION_ID_HEADER, correlationId);

            // Log de entrada
            long startTime = System.currentTimeMillis();
            log.info("Incoming Request: [{} {}]", req.getMethod(), req.getRequestURI());

            // Procesar petición
            chain.doFilter(request, response);

            // Log de salida con tiempo de ejecución
            long duration = System.currentTimeMillis() - startTime;
            log.info("Completed Request: [{} {}] - Status: {} - Time: {}ms",
                    req.getMethod(), req.getRequestURI(), res.getStatus(), duration);

        } finally {
            // Limpiar el contexto para evitar mezclar datos entre hilos
            MDC.remove(CORRELATION_ID_KEY);
        }
    }
}