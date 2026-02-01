package com.techstore.service;

import com.techstore.dto.ProductDto;
import com.techstore.dto.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    // --- MÉTODOS EXISTENTES (CRUD & Paginación) ---
    Page<ProductDto> getAllProducts(Pageable pageable);

    ProductDto getProductById(Long id);

    Page<ProductDto> searchProducts(String query, Pageable pageable);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(Long id, ProductDto productDto);

    void deleteProduct(Long id);

    // --- MÉTODOS DE BÚSQUEDA AVANZADA ---
    List<ProductDto> getProductsLowStock(Integer stockLimit);

    List<ProductDto> getProductsByMinPrice(BigDecimal minPrice);

    List<ProductDto> searchProductsByTerm(String term);

    // --- NUEVO MÉTODO DÍA 3 (31/01): LÓGICA TRANSACCIONAL ---
    // Este método lanzará StockInsufficientException si falla la regla de negocio.
    ProductDto reduceStock(Long id, Integer quantity);

    //método de búsqueda avanzada ingresada el 01/02
    Page<ProductResponseDto> searchProducts(String name, BigDecimal minPrice, BigDecimal maxPrice, String category, Pageable pageable);
}