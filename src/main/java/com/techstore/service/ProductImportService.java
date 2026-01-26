package com.techstore.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*; // Interfaz genérica (sirve para xls y xlsx)
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // Implementación específica para .xlsx
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Service
@Slf4j // Usaremos Logs en lugar de System.out (Estándar Enterprise)
public class ProductImportService {

    private static final String EXCEL_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public void importProducts(MultipartFile file) {
        // 1. Validaciones previas (Ya las tenías)
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo importado no puede estar vacío.");
        }
        if (!Objects.equals(file.getContentType(), EXCEL_TYPE)) {
            throw new IllegalArgumentException("Formato inválido. Use Excel (.xlsx).");
        }

        // 2. PROCESAMIENTO CON APACHE POI
        try (InputStream inputStream = file.getInputStream()) {
            // "Try with resources" asegura que el InputStream se cierre solo al terminar.

            // A. CREAR EL LIBRO (WORKBOOK)
            // Le pasamos el chorro de bytes para que reconstruya el Excel en memoria.
            Workbook workbook = new XSSFWorkbook(inputStream);

            // B. OBTENER LA HOJA (SHEET)
            // Normalmente la data está en la primera pestaña (índice 0).
            Sheet sheet = workbook.getSheetAt(0);

            log.info("Iniciando lectura de hoja: {}", sheet.getSheetName());

            // C. ITERAR FILAS (ROWS)
            // Usamos un DataFormatter para leer cualquier tipo de dato como String seguro.
            DataFormatter dataFormatter = new DataFormatter();

            for (Row row : sheet) {
                // SALTAMOS EL ENCABEZADO
                if (row.getRowNum() == 0) {
                    continue; // Si es la fila 0, pasa a la siguiente vuelta del bucle.
                }

                // D. LEER CELDAS (CELLS)
                // Imaginemos que tu Excel tiene: Nombre (0), Descripción (1), Precio (2), Stock (3)

                // Leemos las celdas usando el índice de columna (0, 1, 2...)
                String name = dataFormatter.formatCellValue(row.getCell(0));
                String description = dataFormatter.formatCellValue(row.getCell(1));
                String priceStr = dataFormatter.formatCellValue(row.getCell(2));
                String stockStr = dataFormatter.formatCellValue(row.getCell(3));

                // VALIDACIÓN BÁSICA: Si no hay nombre, saltamos la fila (puede ser una fila vacía al final)
                if (name == null || name.isBlank()) {
                    continue;
                }

                // IMPRIMIR EN CONSOLA (SIMULACRO)
                log.info("Fila {}: Producto='{}', Precio='{}', Stock='{}'",
                        row.getRowNum(), name, priceStr, stockStr);

                // NOTA: Mañana aquí convertiremos estos Strings a ProductDto y guardaremos en BD.
            }

            // Cerramos el workbook para liberar memoria (muy importante)
            workbook.close();

        } catch (IOException e) {
            log.error("Error crítico leyendo el archivo Excel", e);
            throw new RuntimeException("Error al procesar el archivo: " + e.getMessage());
        }
    }
}