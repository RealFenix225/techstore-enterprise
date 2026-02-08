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

/**
 * Controller para la gestión de productos en TechStore Enterprise.
 * Implementa estándares de documentación OpenAPI y blindaje de seguridad.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product Management", description = "Operaciones relacionadas con el catálogo e inventario de productos")
public class ProductController {

    private final ProductService productService;
    private final ProductImportService productImportService;

    @Operation(summary = "Listar productos", description = "Obtiene una página de productos con soporte para ordenación dinámica.")
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @Operation(summary = "Obtener por ID", description = "Busca un producto específico mediante su identificador único.")
    @ApiResponse(responseCode = "200", description = "Producto encontrado")
    @ApiResponse(responseCode = "404", description = "Producto no existe")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(
            @PathVariable @Positive(message = "ID must be positive") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Búsqueda simple", description = "Filtra productos que contengan el nombre proporcionado.")
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam @NotBlank(message = "Query cannot be empty") String query,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(query, pageable));
    }

    @Operation(summary = "Registrar producto", description = "Crea un nuevo producto en el sistema. Requiere privilegios de ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Producto creado con éxito")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody ProductDto productDto,
            UriComponentsBuilder uriBuilder) {
        ProductDto createdProduct = productService.createProduct(productDto);
        URI location = uriBuilder.path("/api/products/{id}").buildAndExpand(createdProduct.getId()).toUri();
        return ResponseEntity.created(location).body(createdProduct);
    }

    @Operation(summary = "Actualizar producto", description = "Modifica los datos de un producto existente. Requiere privilegios de ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @Operation(summary = "Eliminar producto", description = "Borra permanentemente un producto del catálogo. Requiere privilegios de ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable @Positive(message = "ID must be positive") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Carga masiva (Excel)", description = "Importa productos desde un archivo Excel (.xlsx).")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> uploadProducts(@RequestParam("file") MultipartFile file) throws IOException {
        productImportService.importProducts(file);
        return ResponseEntity.ok("File uploaded successfully");
    }

    @Operation(summary = "Gestionar Stock", description = "Reduce el stock de un producto tras una venta.")
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDto> reduceStock(
            @PathVariable Long id,
            @RequestParam @Positive(message = "Quantity must be positive") Integer quantity) {
        return ResponseEntity.ok(productService.reduceStock(id, quantity));
    }

    @Operation(summary = "Alertas de Stock Bajo", description = "Lista productos con existencias por debajo del límite especificado.")
    @GetMapping("/search/low-stock")
    public ResponseEntity<List<ProductDto>> getLowStock(
            @RequestParam @Min(value = 1, message = "Limit must be at least 1") Integer limit) {
        return ResponseEntity.ok(productService.getProductsLowStock(limit));
    }

    @Operation(summary = "Productos Premium", description = "Lista productos cuyo precio es mayor al mínimo indicado.")
    @GetMapping("/search/expensive")
    public ResponseEntity<List<ProductDto>> getExpensiveProducts(
            @RequestParam @Positive(message = "Minimum price must be positive") BigDecimal min) {
        return ResponseEntity.ok(productService.getProductsByMinPrice(min));
    }

    @Operation(summary = "Búsqueda rápida", description = "Búsqueda optimizada por término general.")
    @GetMapping("/search/quick")
    public ResponseEntity<List<ProductDto>> search(
            @RequestParam @NotBlank(message = "Search term cannot be empty") String term) {
        return ResponseEntity.ok(productService.searchProductsByTerm(term));
    }

    @Operation(summary = "Filtro Avanzado", description = "Búsqueda multicriterio por nombre, rango de precios y categoría.")
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