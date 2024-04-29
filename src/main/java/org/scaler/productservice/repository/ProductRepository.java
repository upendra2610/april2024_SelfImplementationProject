package org.scaler.productservice.repository;

import org.scaler.productservice.models.Category;
import org.scaler.productservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByIdIs(Long id);

    Product save(Product product);

    List<Product> findAll();

    void deleteById(Long id);

    List<Product> findByCategoryTitle(String title);
}
