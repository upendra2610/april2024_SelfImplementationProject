package org.scaler.productservice.service;

import org.scaler.productservice.dtos.CategoryResponseDto;
import org.scaler.productservice.models.Product;

import java.util.List;


public interface Productservice {
    public Product getProductById(Long id);

    public List<Product> getAllProduct();

    public Product createProduct(String title, double price, String description,
                                 String image, String category);

    public Product deleteProduct(Long id);

    public List<CategoryResponseDto> getAllCategory();

    public Product updateProduct(Long id, String title, double price, String description,
                                 String image, String category);

    public List<Product> getAllProductByCategory(String title);



}
