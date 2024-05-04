package org.scaler.productservice.service;

import org.scaler.productservice.exceptions.NotFoundException;
import org.scaler.productservice.models.Product;

import java.util.List;


public interface Productservice {
    Product getProductById(Long id) throws NotFoundException;

    List<Product> getAllProduct() throws NotFoundException;

    Product createProduct(String title, Double price, String description,
                                 String image, String category);
    Product deleteProduct(Long id) throws NotFoundException;


    Product updateProduct(Long id, String title, Double price, String description,
                                 String image, String category) throws NotFoundException;

    List<Product> getAllProductByCategory(String title) throws NotFoundException;



}
