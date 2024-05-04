package org.scaler.productservice.repository;

import org.scaler.productservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findById(Long id);

    Product save(Product product);

    List<Product> findAll();

    void deleteById(Long id);

    List<Product> findByCategoryTitle(String title);
}
