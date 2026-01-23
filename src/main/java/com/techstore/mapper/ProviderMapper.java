package com.techstore.mapper;

import com.techstore.dto.ProviderDto;
import com.techstore.model.Provider;
import org.springframework.stereotype.Component;

@Component
public class ProviderMapper {

    public ProviderDto toDto(Provider entity) {
        if (entity == null) return null;
        return ProviderDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .taxId(entity.getTaxId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Provider toEntity(ProviderDto dto) {
        if (dto == null) return null;
        return Provider.builder()
                .name(dto.getName())
                .taxId(dto.getTaxId())
                .build();
    }
}