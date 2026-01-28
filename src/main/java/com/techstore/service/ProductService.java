package com.techstore.service;

import com.techstore.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    // --- MÉTODOS EXISTENTES (CRUD & Paginación) ---
    Page<ProductDto> getAllProducts(Pageable pageable);

    ProductDto getProductById(Long id);

    // Este es tu buscador paginado actual
    Page<ProductDto> searchProducts(String query, Pageable pageable);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(Long id, ProductDto productDto);

    void deleteProduct(Long id);

    // --- NUEVOS MÉTODOS DE INTELIGENCIA (DÍA 5) ---
    // Sin paginación, para alertas rápidas y filtros
    List<ProductDto> getProductsLowStock(Integer stockLimit);

    List<ProductDto> getProductsByMinPrice(BigDecimal minPrice);

    List<ProductDto> searchProductsByTerm(String term);
}