package com.techstore.mapper;

import com.techstore.dto.ProductDto;
import com.techstore.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    // ENTITY -> DTO (Salida: Lo que ve en Postman)
    public ProductDto toDto(Product entity) {
        if (entity == null) return null;

        return ProductDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())

                // --- RELACIONES (INFORMACIÓN VISUAL) ---
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "Sin Categoría")
                .providerName(entity.getProvider() != null ? entity.getProvider().getName() : "Sin Proveedor")

                // --- RELACIONES (IDS PARA LA LÓGICA) - ¡ESTO ME FALTABA! ---
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .providerId(entity.getProvider() != null ? entity.getProvider().getId() : null)

                // --- AUDITORÍA ---
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // DTO -> ENTITY (Entrada: Lo que llega para guardar)
    public Product toEntity(ProductDto dto) {
        if (dto == null) return null;

        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                // NOTA: No mapep category/provider aquí.
                // Eso lo hace el ProductService buscando los IDs reales en la BD.
                .build();
    }
}