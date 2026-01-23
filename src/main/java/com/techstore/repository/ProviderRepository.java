package com.techstore.repository;

import com.techstore.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
}
