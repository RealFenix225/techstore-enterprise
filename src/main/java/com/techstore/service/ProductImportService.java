package com.techstore.service;

import com.techstore.model.Category;
import com.techstore.model.Product;
import com.techstore.model.Provider;
import com.techstore.repository.CategoryRepository;
import com.techstore.repository.ProductRepository;
import com.techstore.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // IMPORTANTE (9/02)
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
@Slf4j // Habilita el logger profesional
public class ProductImportService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final ProviderRepository providerRepo;

    @Transactional
    public void importProducts(MultipartFile file) throws IOException {
        log.info("--- STARTING IMPORT --- File: {} | Size: {} bytes", file.getOriginalFilename(), file.getSize());

        // 1. Cargar Maestros (Validación Crítica)
        // Logs antes de lanzar la excepción para que quede registrado el error grave
        Category defaultCategory = categoryRepo.findById(1L)
                .orElseThrow(() -> {
                    log.error("CRITICAL: Default Category (ID 1) not found in DB");
                    return new RuntimeException("Critical Error: Category ID 1 missing");
                });

        Provider defaultProvider = providerRepo.findById(1L)
                .orElseThrow(() -> {
                    log.error("CRITICAL: Default Provider (ID 1) not found in DB");
                    return new RuntimeException("Critical Error: Provider ID 1 missing");
                });

        List<Product> productsToSave = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            log.info("Reading Sheet: '{}' with approx {} rows", sheet.getSheetName(), sheet.getPhysicalNumberOfRows());

            for (Row row : sheet) {
                // Saltar cabecera
                if (row.getRowNum() == 0) continue;

                String name = dataFormatter.formatCellValue(row.getCell(0));
                String priceStr = dataFormatter.formatCellValue(row.getCell(2));

                // Validación básica
                if (name == null || name.trim().isEmpty()) {
                    log.warn("Row {} ignored: Name is empty", row.getRowNum());
                    continue;
                }

                try {
                    BigDecimal price = new BigDecimal(priceStr.replace(",", "."));
                    int stock = 10; // Valor por defecto temporal

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
                    log.error("Error parsing Row {}: {}", row.getRowNum(), e.getMessage());
                }
            }
        }

        log.info("--- SUMMARY --- Valid products found: {}", productsToSave.size());

        if (!productsToSave.isEmpty()) {
            productRepo.saveAll(productsToSave);
            log.info("SUCCESS: {} products saved to Database", productsToSave.size());
        } else {
            log.warn("ALERT: Import list is EMPTY. Nothing was saved.");
        }
    }
}