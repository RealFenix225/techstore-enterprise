package com.techstore.service;

import com.techstore.dto.ProductDto;
import com.techstore.dto.ProductResponseDto;
import com.techstore.exception.ResourceNotFoundException;
import com.techstore.exception.StockInsufficientException;
import com.techstore.mapper.ProductMapper;
import com.techstore.model.Category;
import com.techstore.model.Product;
import com.techstore.model.Provider;
import com.techstore.repository.CategoryRepository;
import com.techstore.repository.ProductRepository;
import com.techstore.repository.ProviderRepository;
import com.techstore.repository.spec.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final String ENTITY_NAME = "Product";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProviderRepository providerRepository;

    // --- LECTURA (READ) ---

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ENTITY_NAME, "id", id));
        return productMapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> searchProducts(String query, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(query, pageable)
                .map(productMapper::toDto);
    }

    // --- INTELIGENCIA DE NEGOCIO Y BÚSQUEDAS ---

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsLowStock(Integer stockLimit) {
        return productRepository.findByStockLessThan(stockLimit).stream()
                .map(productMapper::toDto)
                .toList(); // JAVA 17: Mucho más limpio que collect(Collectors.toList())
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByMinPrice(BigDecimal minPrice) {
        return productRepository.findByPriceGreaterThanEqual(minPrice).stream()
                .map(productMapper::toDto)
                .toList(); // JAVA 17 CLEAN CODE
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> searchProductsByTerm(String term) {
        return productRepository.searchByTerm(term).stream()
                .map(productMapper::toDto)
                .toList(); // JAVA 17 CLEAN CODE
    }

    // --- OPERACIÓN TÁCTICA DEL DÍA 3: CONTROL DE STOCK ---

    @Override
    @Transactional // <--- TRANSACCIÓN DE ESCRITURA (ACID)
    public ProductDto reduceStock(Long id, Integer quantity) {
        // 1. Buscar producto
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ENTITY_NAME, "id", id));

        // 2. VALIDACIÓN DE REGLA DE NEGOCIO
        if (product.getStock() < quantity) {
            throw new StockInsufficientException(
                    "Not enough stock for product ID: " + id +
                            ". Available: " + product.getStock() +
                            ", Requested: " + quantity
            );
        }

        // 3. Modificación del Estado
        product.setStock(product.getStock() - quantity);

        // 4. Guardado
        Product savedProduct = productRepository.save(product);

        // 5. Retorno mapeado
        return productMapper.toDto(savedProduct);
    }

    // --- ESCRITURA (CREATE) ---

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Category category = getCategoryOrThrow(productDto.getCategoryId());
        Provider provider = getProviderOrThrow(productDto.getProviderId());

        Product product = productMapper.toEntity(productDto);
        product.setCategory(category);
        product.setProvider(provider);

        return productMapper.toDto(productRepository.save(product));
    }

    // --- ACTUALIZACIÓN (UPDATE) ---

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ENTITY_NAME, "id", id));

        // Actualiza campos básicos
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());

        // Actualiza relaciones solo si cambiaron
        if (product.getCategory() == null || !product.getCategory().getId().equals(productDto.getCategoryId())) {
            product.setCategory(getCategoryOrThrow(productDto.getCategoryId()));
        }

        if (product.getProvider() == null || !product.getProvider().getId().equals(productDto.getProviderId())) {
            product.setProvider(getProviderOrThrow(productDto.getProviderId()));
        }

        return productMapper.toDto(productRepository.save(product));
    }

    // --- ELIMINACIÓN (DELETE) ---

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(ENTITY_NAME, "id", id);
        }
        productRepository.deleteById(id);
    }

    // --- MÉTODOS AUXILIARES ---

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    private Provider getProviderOrThrow(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", "id", id));
    }

    // Método auxiliar para convertir Entidad -> DTO de respuesta avanzada
    private ProductResponseDto convertToResponseDTO(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        // Manejo seguro de relaciones
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        if (product.getProvider() != null) {
            dto.setProviderId(product.getProvider().getId());
            dto.setProviderName(product.getProvider().getName());
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> searchProducts(String name, BigDecimal minPrice, BigDecimal maxPrice, String category, Pageable pageable) {

        Specification<Product> spec = Specification.where(ProductSpecifications.hasName(name))
                .and(ProductSpecifications.hasMinPrice(minPrice))
                .and(ProductSpecifications.hasMaxPrice(maxPrice))
                .and(ProductSpecifications.hasCategory(category));

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        return productPage.map(this::convertToResponseDTO);
    }
}