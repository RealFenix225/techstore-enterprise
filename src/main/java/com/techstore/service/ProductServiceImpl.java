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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

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
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> searchProducts(String query, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(query, pageable)
                .map(productMapper::toDto);
    }

    // --- INTELIGENCIA DE NEGOCIO (NUEVO - DÍA 5) ---
    // Usamos tu ProductMapper aquí también para mantener consistencia

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsLowStock(Integer stockLimit) {
        return productRepository.findByStockLessThan(stockLimit).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByMinPrice(BigDecimal minPrice) {
        return productRepository.findByPriceGreaterThanEqual(minPrice).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> searchProductsByTerm(String term) {
        return productRepository.searchByTerm(term).stream() // Usa el JPQL del Repo
                .map(productMapper::toDto)
                .collect(Collectors.toList());
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
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Esto actualiza campos básicos
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());

        // Esto actualiza relaciones solo si cambiaron
        if (!product.getCategory().getId().equals(productDto.getCategoryId())) {
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
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
    }

    // --- MÉTODOS AUXILIARES (Privados) ---

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    private Provider getProviderOrThrow(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", "id", id));
    }
}