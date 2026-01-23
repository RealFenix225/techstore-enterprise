package com.techstore;

import com.techstore.model.Category;
import com.techstore.model.Product;
import com.techstore.model.Provider;
import com.techstore.repository.CategoryRepository;
import com.techstore.repository.ProductRepository;
import com.techstore.repository.ProviderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
//@EnableJpaAuditing
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    @Bean
    CommandLineRunner init(ProductRepository productRepo,
                           CategoryRepository categoryRepo,
                           ProviderRepository providerRepo){
        return args ->{
            System.out.println("=== STARTING DAY 5: REPOSITORY INTEGRATION TEST ===");
            // STEP 1: CLEANUP (Optional: Start fresh for this test)
            //We delete child (products) first, then parents to avoid Constraint Violations
            productRepo.deleteAll();
            categoryRepo.deleteAll();
            providerRepo.deleteAll();

            // STEP 2: CREATE PARENTS (Category & Provider)
            System.out.println("=== CREATING PARENTS ===");

            Category electronics = Category.builder()
                    .name("Electronics")
                    .build();

            //We MUST save the parent first!
            categoryRepo.save(electronics);

            Provider techCorp = Provider.builder()
                    .name("Tech Corp International")
                    .taxId("B12345678")
                    .build();

            providerRepo.save(techCorp);
            System.out.println("Parents saved: "+electronics.getName()+" & " + techCorp.getName());

            // STEP 3: CREATE CHILD (Product) LINKED TO PARENTS
            System.out.println("=== CREATING LINKED PRODUCT ===");

            Product laptop = Product.builder()
                    .name("MackBook Pro M3")
                    .description("Apple Laptop for Professionals")
                    .price(new BigDecimal("2500.00"))
                    .stock(5)
                    .category(electronics)
                    .provider(techCorp)
                    .build();

            productRepo.save(laptop);
            System.out.println("Product saved linked to Category and Provider!");

            for (int i = 1; i <= 20; i++) {
                Product p = Product.builder()
                        .name("Product Test " + i)
                        .description("Description " + i)
                        .price(new BigDecimal("100.00").add(new BigDecimal(i))) // Precios variados: 101, 102...
                        .stock(10)
                        .category(electronics) // Usamos la categoría que ya creaste
                        .provider(techCorp)    // Usamos el proveedor que ya creaste
                        .build();
                productRepo.save(p);
            }

            //STEP 4: VERIFICATION (The Magic of JPA)
            System.out.println("=== VERIFYING DATA ===");
            List<Product> products = productRepo.findAll();

            products.forEach(p ->{
                System.out.println("Found Product: "+p.getName());
                //Notice we can navigate the object graph: p.getCategory().getName()
                System.out.println("  -> Product ID: " +p.getId());
                System.out.println("  -> Price: " +p.getPrice());
            });
        };
    }
}