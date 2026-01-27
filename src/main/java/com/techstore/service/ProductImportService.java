package com.techstore.service;

import com.techstore.model.Category;
import com.techstore.model.Product;
import com.techstore.model.Provider;
import com.techstore.repository.CategoryRepository;
import com.techstore.repository.ProductRepository;
import com.techstore.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImportService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final ProviderRepository providerRepo;

    @Transactional
    public void importProducts(MultipartFile file) throws IOException {
        System.out.println("--- INICIO DE IMPORTACIÓN ---");
        System.out.println("Archivo recibido: " + file.getOriginalFilename());
        System.out.println("Tamaño: " + file.getSize() + " bytes");

        // 1. Cargar Maestros (Asumimos ID 1 existe, si no, fallará visiblemente)
        Category defaultCategory = categoryRepo.findById(1L)
                .orElseThrow(() -> new RuntimeException("¡ERROR CRÍTICO! No existe la Categoría ID 1 en BBDD"));
        Provider defaultProvider = providerRepo.findById(1L)
                .orElseThrow(() -> new RuntimeException("¡ERROR CRÍTICO! No existe el Proveedor ID 1 en BBDD"));

        List<Product> productsToSave = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter(); // Para leer números como texto

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("Leyendo hoja: " + sheet.getSheetName());
            System.out.println("Total de filas estimadas: " + sheet.getPhysicalNumberOfRows());

            for (Row row : sheet) {
                // Saltar cabecera (Fila 0)
                if (row.getRowNum() == 0) {
                    System.out.println("Saltando cabecera...");
                    continue;
                }

                // Leer celdas (Mapeo estricto del Excel de 50 items)
                // A=Name, B=Desc, C=Price, D=Stock, E=CatId, F=ProvId
                String name = dataFormatter.formatCellValue(row.getCell(0));
                String priceStr = dataFormatter.formatCellValue(row.getCell(2));

                // Depuración de fila
                // System.out.println("Fila " + row.getRowNum() + " leída. Nombre: [" + name + "]");

                if (name == null || name.trim().isEmpty()) {
                    System.out.println(">>> Fila " + row.getRowNum() + " IGNORADA: Nombre vacío");
                    continue;
                }

                try {
                    BigDecimal price = new BigDecimal(priceStr.replace(",", ".")); // Asegurar formato decimal
                    // int stock = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(3)));
                    // Simplificamos stock a 10 para probar si falla el parseo
                    int stock = 10;

                    Product product = Product.builder()
                            .name(name)
                            .description(dataFormatter.formatCellValue(row.getCell(1)))
                            .price(price)
                            .stock(stock)
                            .category(defaultCategory)
                            .provider(defaultProvider)
                            .build();

                    productsToSave.add(product);
                } catch (Exception e) {
                    System.out.println(">>> ERROR en Fila " + row.getRowNum() + ": " + e.getMessage());
                    // e.printStackTrace(); // Descomenta si necesitas más detalle
                }
            }
        }

        System.out.println("--- RESUMEN ---");
        System.out.println("Productos detectados válidos: " + productsToSave.size());

        if (!productsToSave.isEmpty()) {
            productRepo.saveAll(productsToSave);
            System.out.println("¡GUARDADO EXITOSO EN BASE DE DATOS!");
        } else {
            System.out.println("¡ALERTA! La lista a guardar está VACÍA.");
        }
    }
}