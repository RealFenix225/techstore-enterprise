package com.techstore.mapper;

import com.techstore.dto.ProductDto;
import com.techstore.model.Category;
import com.techstore.model.Product;
import com.techstore.model.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Import estático para fluidez (AssertJ)
import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    // La unidad bajo prueba (System Under Test)
    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        // ARRANGE COMÚN: Instanciamos la clase real.
        // Al no tener dependencias, hacemos 'new'.
        productMapper = new ProductMapper();
    }

    @Test
    @DisplayName("Should map Product Entity to DTO correctly including relationships IDs")
    void shouldMapProductToDto_whenEntityIsValid() {
        // 1. ARRANGE
        Category category = Category.builder().id(100L).name("Electronics").build();
        Provider provider = Provider.builder().id(200L).name("Sony").build();

        Product product = Product.builder()
                .id(1L)
                .name("PlayStation 5")
                .description("Console")
                .price(new BigDecimal("499.99"))
                .stock(50)
                .category(category)
                .provider(provider)
                .createdAt(LocalDateTime.now())
                .build();

        // 2. ACT
        ProductDto result = productMapper.toDto(product);

        // 3. ASSERT (Validamos campo por campo crítico)
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("PlayStation 5");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("499.99"));

        // Verificamos la lógica especial de tu Mapper (IDs planos)
        assertThat(result.getCategoryId()).isEqualTo(100L);
        assertThat(result.getProviderId()).isEqualTo(200L);
        assertThat(result.getCategoryName()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("Should return null when input Entity is null")
    void shouldReturnNull_whenEntityIsNull() {
        // ACT
        ProductDto result = productMapper.toDto(null);

        // ASSERT
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should map DTO to Entity correctly ignoring relations")
    void shouldMapDtoToEntity_whenDtoIsValid() {
        // 1. ARRANGE
        ProductDto dto = ProductDto.builder()
                .name("Xbox Series X")
                .description("Microsoft Console")
                .price(new BigDecimal("450.00"))
                .stock(20)
                .categoryId(5L) // Esto el mapper toEntity lo ignora según tu código actual
                .build();

        // 2. ACT
        Product resultEntity = productMapper.toEntity(dto);

        // 3. ASSERT
        assertThat(resultEntity).isNotNull();
        assertThat(resultEntity.getName()).isEqualTo("Xbox Series X");
        assertThat(resultEntity.getStock()).isEqualTo(20);

        // VERIFICACIÓN CRÍTICA: Tu mapper toEntity NO setea categoría ni proveedor
        // Debemos asegurar que eso sea NULL para no tener falsos positivos.
        assertThat(resultEntity.getCategory()).isNull();
        assertThat(resultEntity.getProvider()).isNull();
    }
}