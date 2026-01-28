package com.techstore.controller;

import com.techstore.dto.ProductDto;
import com.techstore.service.ProductImportService;
import com.techstore.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated // <--- EL ESCUDO: Activa validaciones en parámetros simples (query params, path variables)
public class ProductController {

    private final ProductService productService;
    private final ProductImportService productImportService;

    // 1. OBTENER TODOS (Paginado)
    // GET /api/products?page=0&size=10
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @PageableDefault(size = 10) Pageable pageable) {

        Page<ProductDto> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    // 2. OBTENER POR ID
    // GET /api/products/1
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(
            @PathVariable @Positive(message = "ID must be positive") Long id) { // BLINDAJE: ID positivo
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // 3. BUSCAR POR NOMBRE
    // GET /api/products/search?query=Laptop
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam @NotBlank(message = "Query cannot be empty") String query, // BLINDAJE: No vacío
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(query, pageable));
    }

    // 4. CREAR (POST)
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody ProductDto productDto, // BLINDAJE: Valida el JSON completo contra el DTO
            UriComponentsBuilder uriBuilder) {

        ProductDto createdProduct = productService.createProduct(productDto);

        URI location = uriBuilder
                .path("/api/products/{id}")
                .buildAndExpand(createdProduct.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdProduct);
    }

    // 5. ACTUALIZAR (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable @Positive(message = "ID must be positive") Long id, // BLINDAJE
            @Valid @RequestBody ProductDto productDto // BLINDAJE
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    // 6. ELIMINAR (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable @Positive(message = "ID must be positive") Long id) { // BLINDAJE
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // 7. IMPORTAR PRODUCTOS
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadProducts(@RequestParam("file") MultipartFile file) throws IOException {
        productImportService.importProducts(file);
        return ResponseEntity.ok("File uploaded successfully");
    }

    // --- ENDPOINTS DE BUSQUEDA AVANZADA ---

    // 1. GET /api/products/search/low-stock?limit=10
    @GetMapping("/search/low-stock")
    public ResponseEntity<List<ProductDto>> getLowStock(
            @RequestParam @Min(value = 1, message = "Limit must be at least 1") Integer limit) { // BLINDAJE: Mínimo 1
        return ResponseEntity.ok(productService.getProductsLowStock(limit));
    }

    // 2. GET /api/products/search/expensive?min=1000
    @GetMapping("/search/expensive")
    public ResponseEntity<List<ProductDto>> getExpensiveProducts(
            @RequestParam @Positive(message = "Minimum price must be positive") BigDecimal min) { // BLINDAJE: Positivo
        return ResponseEntity.ok(productService.getProductsByMinPrice(min));
    }

    // 3. GET /api/products/search/quick?term=gamer
    @GetMapping("/search/quick")
    public ResponseEntity<List<ProductDto>> search(
            @RequestParam @NotBlank(message = "Search term cannot be empty") String term) { // BLINDAJE: No vacío
        return ResponseEntity.ok(productService.searchProductsByTerm(term));
    }
}