package com.techstore.controller;

import com.techstore.dto.ProviderDto;
import com.techstore.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @GetMapping
    public ResponseEntity<List<ProviderDto>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @PostMapping
    public ResponseEntity<ProviderDto> createProvider(
            @Valid @RequestBody ProviderDto providerDto,
            UriComponentsBuilder uriBuilder) {

        ProviderDto created = providerService.createProvider(providerDto);
        URI location = uriBuilder.path("/api/providers/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id) {
        providerService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }
}