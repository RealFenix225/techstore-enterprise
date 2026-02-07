package com.techstore.service;

import com.techstore.model.Category;
import com.techstore.model.Product;
import com.techstore.model.Provider;
import com.techstore.repository.CategoryRepository;
import com.techstore.repository.ProductRepository;
import com.techstore.repository.ProviderRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductImportServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProductImportService productImportService;

    // --- TEST 1: IMPORTACIÓN EXITOSA (HAPPY PATH) ---
    @Test
    @DisplayName("Should parse Excel and save products when file is valid")
    void shouldImportProducts_whenFileIsValid() throws IOException {
        // 1. ARRANGE (Preparar el terreno)

        // Mockear las dependencias maestras (Categoría y Proveedor ID 1 deben existir)
        Category mockCategory = new Category();
        mockCategory.setId(1L);
        Provider mockProvider = new Provider();
        mockProvider.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(providerRepository.findById(1L)).thenReturn(Optional.of(mockProvider));

        // Esto crea un Excel FALSO en memoria (Qué locura)
        byte[] excelBytes = createMockExcelBytes("Gamer Mouse", "High precision", "50.50");
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "import.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                excelBytes
        );

        // 2. ACT (Ejecutar la importación)
        productImportService.importProducts(mockFile);

        // 3. ASSERT (Verificar)
        // Verificamos que se llamó al repositorio para guardar una lista
        verify(productRepository, times(1)).saveAll(anyList());
    }

    // --- TEST 2: ARCHIVO VACÍO O INVÁLIDO ---
    @Test
    @DisplayName("Should not save anything when file is empty")
    void shouldNotSave_whenFileIsEmpty() throws IOException {
        // ARRANGE
        // Simulamos que SÍ existen las categorías (para que no falle ahí)
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));
        when(providerRepository.findById(1L)).thenReturn(Optional.of(new Provider()));

        byte[] emptyExcel = createMockExcelBytes(null, null, null); // Excel sin datos (solo cabecera)
        MockMultipartFile mockFile = new MockMultipartFile("file", "empty.xlsx", "application/vnd.ms-excel", emptyExcel);

        // ACT
        productImportService.importProducts(mockFile);

        // ASSERT
        // saveAll NUNCA debió ser llamado
        verify(productRepository, never()).saveAll(anyList());
    }

    // --- TEST 3: FALLO DE DEPENDENCIAS MAESTRAS ---
    @Test
    @DisplayName("Should throw exception if Default Category (ID 1) is missing")
    void shouldThrowException_whenCategoryMissing() {
        // ARRANGE
        MockMultipartFile mockFile = new MockMultipartFile("file", new byte[0]);
        // Simulamos que la base de datos NO encuentra la categoría 1
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> productImportService.importProducts(mockFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe la Categoría ID 1");
    }

    // ==========================================
    // MÉTODO AUXILIAR: FABRICA DE EXCELS
    // ==========================================
    private byte[] createMockExcelBytes(String name, String desc, String price) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            // Crear Cabecera (Fila 0) - Tu código la salta
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Description");
            header.createCell(2).setCellValue("Price");

            // Crear Datos (Fila 1) - Solo si pasamos datos
            if (name != null) {
                Row row = sheet.createRow(1);
                row.createCell(0).setCellValue(name);
                row.createCell(1).setCellValue(desc);
                row.createCell(2).setCellValue(price); // Pasamos el precio como String para simular el CSV/Excel
            }

            // Convertir el Workbook a array de bytes
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }
}