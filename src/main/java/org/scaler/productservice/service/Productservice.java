package org.scaler.productservice.service;

import org.scaler.productservice.models.Product;

import java.util.List;


public interface Productservice {
    Product getProductById(Long id);

    List<Product> getAllProduct();

    Product createProduct(String title, double price, String description,
                                 String image, String category);
    Product deleteProduct(Long id);

    String[] getAllCategory();

    Product updateProduct(Long id, String title, double price, String description,
                                 String image, String category);

    List<Product> getAllProductByCategory(String title);



}
