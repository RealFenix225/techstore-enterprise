package com.techstore.service;

import com.techstore.dto.ProductDto;
import com.techstore.exception.ResourceNotFoundException;
import com.techstore.exception.StockInsufficientException;
import com.techstore.mapper.ProductMapper;
import com.techstore.model.Category;
import com.techstore.model.Product;
import com.techstore.model.Provider;
import com.techstore.repository.CategoryRepository;
import com.techstore.repository.ProductRepository;
import com.techstore.repository.ProviderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// 1. EXTENSIÓN: Habilita Mockito en JUnit 5
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    // 2. MOCKS: Los dobles que simulan comportamiento
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProviderRepository providerRepository;
    @Mock
    private ProductMapper productMapper; // Mockeamos también el mapper para aislar la lógica del servicio

    // 3. INJECT MOCKS
    // Mockito inyecta los @Mock de arriba dentro de esta instancia.
    @InjectMocks
    private ProductServiceImpl productService;

    // --- TEST 1: OBTENER TODOS (PAGINADO) ---
    @Test
    @DisplayName("Should return paged products when they exist")
    void shouldReturnAllProducts_whenCalled() {
        // ARRANGE
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");

        ProductDto dto = new ProductDto();
        dto.setName("Laptop");

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(any(PageRequest.class))).thenReturn(productPage);
        when(productMapper.toDto(product)).thenReturn(dto);

        // ACT
        Page<ProductDto> result = productService.getAllProducts(PageRequest.of(0, 10));

        // ASSERT
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Laptop");

        // Verificamos que el repositorio FUE LLAMADO 1 vez
        verify(productRepository, times(1)).findAll(any(PageRequest.class));
    }

    // --- TEST 2: GET BY ID (EXITO) ---
    @Test
    @DisplayName("Should return product by ID when it exists")
    void shouldReturnProduct_whenIdExists() {
        // ARRANGE
        Long id = 1L;
        Product product = new Product();
        product.setId(id);

        ProductDto expectedDto = new ProductDto();
        expectedDto.setId(id);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(expectedDto);

        // ACT
        ProductDto result = productService.getProductById(id);

        // ASSERT
        assertThat(result.getId()).isEqualTo(id);
    }

    // --- TEST 3: GET BY ID (FALLO - EXCEPCIÓN) ---
    @Test
    @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
    void shouldThrowException_whenIdDoesNotExist() {
        // ARRANGE
        Long id = 999L;
        // Simulamos que la BD devuelve "vacío"
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // ACT & ASSERT (AssertJ way)
        assertThatThrownBy(() -> productService.getProductById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product"); // Verifica parte del mensaje
    }

    // --- TEST 4: REDUCIR STOCK (EXITO) ---
    @Test
    @DisplayName("Should reduce stock and return updated DTO when stock is sufficient")
    void shouldReduceStock_whenStockIsSufficient() {
        // ARRANGE
        Long id = 1L;
        int currentStock = 10;
        int reduceAmount = 3;

        Product product = new Product();
        product.setId(id);
        product.setStock(currentStock); // Tiene 10

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDto dto = new ProductDto();
        dto.setStock(currentStock - reduceAmount); // 7
        when(productMapper.toDto(any(Product.class))).thenReturn(dto);

        // ACT
        ProductDto result = productService.reduceStock(id, reduceAmount);

        // ASSERT
        assertThat(result.getStock()).isEqualTo(7);
        // Verificamos que se llamó a save()
        verify(productRepository).save(any(Product.class));
    }

    // --- TEST 5: REDUCE STOCK (FALLO - INSUFICIENTE) ---
    @Test
    @DisplayName("Should throw StockInsufficientException when request exceeds available stock")
    void shouldThrowStockException_whenStockIsNotEnough() {
        // ARRANGE
        Long id = 1L;
        Product product = new Product();
        product.setId(id);
        product.setStock(5); // Solo hay 5

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // ACT & ASSERT
        // Intentamos reducir 10 (Más de lo que hay)
        assertThatThrownBy(() -> productService.reduceStock(id, 10))
                .isInstanceOf(StockInsufficientException.class)
                .hasMessageContaining("Not enough stock"); // Mensaje clave

        // CRÍTICO: Asegurar que NUNCA se guardó nada en la BD
        verify(productRepository, never()).save(any(Product.class));
    }

    // -------------------------------------------------------------------
    // NUEVOS REFUERZOS (DÍA 5 - MISIÓN PERFECCIONISTA)
    // -------------------------------------------------------------------

    // --- TEST 6: BÚSQUEDA SIMPLE ---
    @Test
    @DisplayName("Should search products by name query")
    void shouldSearchProducts_whenQueryProvided() {
        // ARRANGE
        String query = "Laptop";
        PageRequest pageable = PageRequest.of(0, 10);

        Product product = new Product();
        product.setId(1L);
        product.setName("Gaming Laptop");

        ProductDto dto = new ProductDto();
        dto.setName("Gaming Laptop");

        Page<Product> productPage = new PageImpl<>(List.of(product));

        // Enseñamos al mock del repo qué hacer
        when(productRepository.findByNameContainingIgnoreCase(query, pageable))
                .thenReturn(productPage);
        when(productMapper.toDto(product)).thenReturn(dto);

        // ACT
        Page<ProductDto> result = productService.searchProducts(query, pageable);

        // ASSERT
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Gaming Laptop");
    }

    // --- TEST 7: BÚSQUEDA AVANZADA (CRÍTICO PARA COBERTURA) ---
    // Este test activa el método privado convertToResponseDTO
    @Test
    @DisplayName("Should search products with specifications and convert to ResponseDTO")
    void shouldSearchProducts_whenAdvancedCriteriaProvided() {
        // ARRANGE
        // 1. Datos de entrada
        String name = "Gamer";
        BigDecimal min = BigDecimal.ZERO;
        BigDecimal max = BigDecimal.valueOf(100);
        String categoryName = "Electronics";
        PageRequest pageable = PageRequest.of(0, 10);

        // 2. Mock de Relaciones (Para que el convertidor privado no falle)
        Category mockCategory = new Category();
        mockCategory.setId(5L);
        mockCategory.setName("Electronics");

        Provider mockProvider = new Provider();
        mockProvider.setId(9L);
        mockProvider.setName("Sony");

        // 3. Mock del Producto encontrado
        Product product = new Product();
        product.setId(1L);
        product.setName("Gamer Mouse");
        product.setPrice(BigDecimal.valueOf(50));
        product.setCategory(mockCategory); // ¡Vital para entrar al if del privado!
        product.setProvider(mockProvider); // ¡Vital para entrar al if del privado!

        Page<Product> productPage = new PageImpl<>(List.of(product));

        // 4. Mock del Repositorio con Specification
        // Usamos any() porque Specification es un objeto complejo creado dentro del servicio
        when(productRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable)))
                .thenReturn(productPage);

        // ACT
        Page<com.techstore.dto.ProductResponseDto> result =
                productService.searchProducts(name, min, max, categoryName, pageable);

        // ASSERT
        assertThat(result.getContent()).hasSize(1);
        com.techstore.dto.ProductResponseDto dto = result.getContent().get(0);

        // Verificamos que el mapeo manual funcionó
        assertThat(dto.getName()).isEqualTo("Gamer Mouse");
        assertThat(dto.getCategoryName()).isEqualTo("Electronics");
        assertThat(dto.getProviderName()).isEqualTo("Sony");
    }
}