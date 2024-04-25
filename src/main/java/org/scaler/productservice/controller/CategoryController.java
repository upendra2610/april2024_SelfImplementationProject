package org.scaler.productservice.controller;

import org.scaler.productservice.models.Product;
import org.scaler.productservice.service.Productservice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final Productservice productService;

    public CategoryController(Productservice productService){
        this.productService = productService;
    }

    @GetMapping
    public String[] getAllCategory(){
        return productService.getAllCategory();
    }

    @GetMapping("/{title}")
    public List<Product> getAllProductByCategory(@PathVariable("title") String title){
        return productService.getAllProductByCategory(title);
    }
}
