package com.techstore.repository;

import com.techstore.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @EntityGraph(attributePaths = {"category", "provider"})
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByPriceBetweenAndCategoryName(BigDecimal min, BigDecimal max, String categoryName, Pageable pageable);

    //1. DERIVED QUERY (Magia de Spring)
    //Esto traduce automáticamente a: SELECT * FROM PRODUCT WHERE STOCK < ?
    List<Product> findByStockLessThan(Integer maxStock);

    //2. DERIVED QUERY (Más magia)
    //Esto traduce a: SELECT * FROM PRODUCT WHERE PRICE >=?
    List<Product> findByPriceGreaterThanEqual(BigDecimal minPrice);

    //3. JPQL (Java Persistence Query Language) - El Francotirador
    //Aquí debo escribir SQL pero usando las Clases (Product) no las tablas.
    //UPPER() sirve para ignorar mayúsculas/minúsculas (Oracle es estricto con esto)
    @Query("SELECT p FROM Product p WHERE UPPER(p.name) LIKE UPPER(CONCAT('%', :term, '%')) " +
            "OR UPPER(p.description) LIKE UPPER(CONCAT('%', :term, '%'))")
    List<Product> searchByTerm(@Param("term") String term);
}