package com.techstore.service;

import com.techstore.dto.ProviderDto;
import com.techstore.exception.ResourceNotFoundException;
import com.techstore.mapper.ProviderMapper;
import com.techstore.model.Provider;
import com.techstore.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;

    @Transactional(readOnly = true)
    public List<ProviderDto> getAllProviders() {
        return providerRepository.findAll().stream()
                .map(providerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProviderDto getProviderById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", "id", id));
        return providerMapper.toDto(provider);
    }

    @Transactional
    public ProviderDto createProvider(ProviderDto providerDto) {
        Provider provider = providerMapper.toEntity(providerDto);
        Provider savedProvider = providerRepository.save(provider);
        return providerMapper.toDto(savedProvider);
    }

    @Transactional
    public void deleteProvider(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Provider", "id", id);
        }
        providerRepository.deleteById(id);
    }
}