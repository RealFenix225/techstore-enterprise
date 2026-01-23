package com.techstore.service;

import com.techstore.exception.ResourceNotFoundException;
import com.techstore.dto.ProductDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProviderRepository providerRepository;

    // --- LECTURA (READ) ---

    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable){
        Page<Product> productsPage = productRepository.findAll(pageable);
        return productsPage.map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> searchProducts(String name, Pageable pageable) {
        Page<Product> products = productRepository.findByNameContainingIgnoreCase(name, pageable);
        return products.map(productMapper::toDto);
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toDto(product);
    }

    // --- ESCRITURA (CREATE) ---

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        // 1. Convertir a Entidad (Mapea nombre, precio, stock...)
        Product product = productMapper.toEntity(productDto);

        // 2. BUSCAR RELACIONES REALES (Adiós a la trampa)
        // Ahora buscamos EXACTAMENTE el ID que usted pidió.

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productDto.getCategoryId()));

        Provider provider = providerRepository.findById(productDto.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider", "id", productDto.getProviderId()));

        // 3. Asignar las relaciones
        product.setCategory(category);
        product.setProvider(provider);

        // 4. Guardar
        Product savedProduct = productRepository.save(product);

        // 5. Retornar
        return productMapper.toDto(savedProduct);
    }

    @Transactional
    public List<ProductDto> bulkInsertProducts(List<ProductDto> productDtos) {
        if (productDtos == null || productDtos.isEmpty()) {
            throw new IllegalArgumentException("Product list for bulk insert cannot be empty");
        }

        List<Product> products = new ArrayList<>();

        for (ProductDto dto : productDtos) {
            Product product = Product.builder()
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .price(dto.getPrice())
                    .stock(dto.getStock())
                    // Asignación segura de padres
                    .category(categoryRepository.findAll().stream().findFirst().orElse(null))
                    .provider(providerRepository.findAll().stream().findFirst().orElse(null))
                    .build();

            products.add(product);
        }

        List<Product> savedProducts = productRepository.saveAll(products);

        return savedProducts.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    // --- ACTUALIZACIÓN (UPDATE) ---

    // Este es el método que tenías roto. Lo he limpiado para que solo actualice PRECIO (PATCH)
    @Transactional
    public void updateProductPrice(Long id, BigDecimal newPrice){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Solo actualizamos el precio, nada más
        product.setPrice(newPrice);
        productRepository.save(product);
    }

    // ESTE ES EL NUEVO: Actualización completa (PUT)
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        // 1. Verificar existencia
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // 2. Actualizar campos (Menos ID y Fechas)
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());

        // Nota: Por ahora no cambiamos la categoría ni el proveedor en el Update

        // 3. Guardar y retornar
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    // --- ELIMINACIÓN (DELETE) ---

    @Transactional
    public void deleteProduct(Long id) {
        // 1. Verificar antes de borrar
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        // 2. Ejecutar borrado
        productRepository.deleteById(id);
    }
}