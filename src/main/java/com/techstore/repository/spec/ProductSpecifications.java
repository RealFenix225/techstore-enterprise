package com.techstore.repository.spec;

import com.techstore.model.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public class ProductSpecifications {

    // Filtro por nombre (LIKE %name%)
    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(name)) {
                return null; // Si es nulo o vacío, no aplica filtro
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    // Filtro por precio mínimo (>= minPrice)
    public static Specification<Product> hasMinPrice(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    // Filtro por precio máximo (<= maxPrice)
    public static Specification<Product> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    // Filtro por categoría (category.name = category)
    public static Specification<Product> hasCategory(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(categoryName)) {
                return null;
            }
            // Navegamos: Product -> Category -> name
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("category").get("name")),
                    categoryName.toLowerCase()
            );
        };
    }
}