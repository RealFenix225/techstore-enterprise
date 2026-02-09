package com.techstore.controller;

import com.techstore.dto.ProductDto;
import com.techstore.dto.ProductResponseDto;
import com.techstore.service.ProductImportService;
import com.techstore.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // IMPORTANTE
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Validated
@Slf4j // Habilita el logger
@Tag(name = "Product Management", description = "Inventory management operations")
public class ProductController {

    private final ProductService productService;
    private final ProductImportService productImportService;

    @Operation(summary = "List products")
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @Operation(summary = "Get By ID")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product doesn't exist")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Simple search")
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam @NotBlank String query,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(query, pageable));
    }

    @Operation(summary = "Register product")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody ProductDto productDto,
            UriComponentsBuilder uriBuilder) {

        log.info("Admin creating product: {}", productDto.getName());
        ProductDto createdProduct = productService.createProduct(productDto);

        URI location = uriBuilder.path("/api/products/{id}").buildAndExpand(createdProduct.getId()).toUri();
        return ResponseEntity.created(location).body(createdProduct);
    }

    @Operation(summary = "Update product")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable @Positive Long id,
            @Valid @RequestBody ProductDto productDto) {
        log.info("Admin updating product ID: {}", id);
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @Operation(summary = "Delete product")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable @Positive Long id) {
        log.warn("Admin deleting product ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Upload (Excel)")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> uploadProducts(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Starting bulk upload from file: {}", file.getOriginalFilename());
        productImportService.importProducts(file);
        return ResponseEntity.ok("File uploaded successfully");
    }

    @Operation(summary = "Management stock")
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDto> reduceStock(
            @PathVariable Long id,
            @RequestParam @Positive Integer quantity) {
        log.info("Reducing stock for product ID: {} by {} units", id, quantity);
        return ResponseEntity.ok(productService.reduceStock(id, quantity));
    }

    // --- Endpoints de Búsqueda (Sin logs explícitos para no saturar) ---

    @GetMapping("/search/low-stock")
    public ResponseEntity<List<ProductDto>> getLowStock(@RequestParam @Min(1) Integer limit) {
        return ResponseEntity.ok(productService.getProductsLowStock(limit));
    }

    @GetMapping("/search/expensive")
    public ResponseEntity<List<ProductDto>> getExpensiveProducts(@RequestParam @Positive BigDecimal min) {
        return ResponseEntity.ok(productService.getProductsByMinPrice(min));
    }

    @GetMapping("/search/quick")
    public ResponseEntity<List<ProductDto>> search(@RequestParam @NotBlank String term) {
        return ResponseEntity.ok(productService.searchProductsByTerm(term));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductResponseDto>> filterProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(name, minPrice, maxPrice, category, pageable));
    }
}