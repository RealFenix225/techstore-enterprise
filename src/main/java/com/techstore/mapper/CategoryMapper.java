package com.techstore.mapper;

import com.techstore.dto.CategoryDto;
import com.techstore.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    // Entity -> DTO
    public CategoryDto toDto(Category entity) {
        if (entity == null) return null;

        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt()) // Don't forget the dates!
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // DTO -> Entity
    public Category toEntity(CategoryDto dto) {
        if (dto == null) return null;

        return Category.builder()
                .name(dto.getName())
                .build();
    }
}