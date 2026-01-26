package com.techstore.controller;

import com.techstore.service.ProductImportService;
import com.techstore.dto.ProductDto;
import com.techstore.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
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
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // 3. BUSCAR POR NOMBRE
    // GET /api/products/search?query=Laptop
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam String query,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(query, pageable));
    }

    // 4. CREAR (POST)
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody ProductDto productDto,
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
            @PathVariable Long id,
            @Valid @RequestBody ProductDto productDto
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    // 6. ELIMINAR (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }

    //7. IMPORTAR PRODUCTOS
    // POST /api/products/upload
    //Consumes = multipart/form-data
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadProducts(@RequestParam("file") MultipartFile file) {

        productImportService.importProducts(file);

        return ResponseEntity.ok("File uploaded");
    }
}