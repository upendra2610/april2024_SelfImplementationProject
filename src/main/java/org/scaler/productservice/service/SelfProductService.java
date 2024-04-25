package org.scaler.productservice.service;

import org.scaler.productservice.dtos.CategoryResponseDto;
import org.scaler.productservice.models.Product;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SelfProductService implements Productservice{
    @Override
    public Product getProductById(Long id) {
        return null;
    }

    @Override
    public List<Product> getAllProduct() {
        return null;
    }

    @Override
    public Product createProduct(String title, double price, String description, String image, String category) {
        return null;
    }

    @Override
    public Product deleteProduct(Long id) {
        return null;
    }

    @Override
    public String[] getAllCategory() {
        return null;
    }

    @Override
    public Product updateProduct(Long id, String title, double price, String description, String image, String category) {
        return null;
    }

    @Override
    public List<Product> getAllProductByCategory(String title) {
        return null;
    }
}
