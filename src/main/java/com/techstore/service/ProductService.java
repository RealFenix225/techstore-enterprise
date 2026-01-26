package com.techstore.service;

import com.techstore.dto.ProductDto;
import com.techstore.exception.ResourceNotFoundException;
import com.techstore.mapper.ProductMapper;
import com.techstore.model.Category;
import com.techstore.model.Product;
import com.techstore.model.Provider;
import com.techstore.repository.CategoryRepository;
import com.techstore.repository.ProductRepository;
import com.techstore.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // Constructor Injection automática
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProviderRepository providerRepository;

    // --- LECTURA (READ) ---

    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        // El EntityGraph en el repositorio ya optimiza esto
        return productRepository.findAll(pageable)
                .map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toDto(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> searchProducts(String query, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(query, pageable)
                .map(productMapper::toDto);
    }

    // --- ESCRITURA (CREATE) ---

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        // 1. Validar existencias de relaciones
        Category category = getCategoryOrThrow(productDto.getCategoryId());
        Provider provider = getProviderOrThrow(productDto.getProviderId());

        // 2. Mappear y asignar
        Product product = productMapper.toEntity(productDto);
        product.setCategory(category);
        product.setProvider(provider);

        // 3. Guardar
        return productMapper.toDto(productRepository.save(product));
    }

    // --- ACTUALIZACIÓN (UPDATE) ---

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        // 1. Recuperar producto existente
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // 2. Actualizar campos simples
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());

        // 3. Actualizar Relaciones (Si cambiaron)
        // Solo buscamos en BD si el ID que viene es diferente al que ya tiene el producto
        if (!product.getCategory().getId().equals(productDto.getCategoryId())) {
            Category newCategory = getCategoryOrThrow(productDto.getCategoryId());
            product.setCategory(newCategory);
        }

        if (product.getProvider() == null || !product.getProvider().getId().equals(productDto.getProviderId())) {
            Provider newProvider = getProviderOrThrow(productDto.getProviderId());
            product.setProvider(newProvider);
        }

        // 4. Guardar (Hibernate detecta cambios automáticamente por ser Transactional, pero el save es explícito)
        return productMapper.toDto(productRepository.save(product));
    }

    // --- ELIMINACIÓN (DELETE) ---

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
    }

    // --- MÉTODOS AUXILIARES PRIVADOS (CLEAN CODE) ---
    // Extraemos la lógica de búsqueda para no repetir código y cumplir DRY (Don't Repeat Yourself)

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    private Provider getProviderOrThrow(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", "id", id));
    }
}