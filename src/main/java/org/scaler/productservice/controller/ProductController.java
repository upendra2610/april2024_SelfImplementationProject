package org.scaler.productservice.controller;

import org.scaler.productservice.dtos.CreateProductRequestDto;
import org.scaler.productservice.models.Product;
import org.scaler.productservice.service.Productservice;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    Productservice productService;

    public ProductController(Productservice productService){
        this.productService = productService;
    }

    @GetMapping("/products/{id}")
    public Product getProductById(@PathVariable("id") Long id){
        return productService.getProductById(id);
    }

    @GetMapping("/products")
    public List<Product> getAllProduct(){
        return productService.getAllProduct();
    }

    @PostMapping("/products")
    public Product createProduct(@RequestBody CreateProductRequestDto productRequestDto){
        return productService.createProduct(
                productRequestDto.getTitle(),
                productRequestDto.getPrice(),
                productRequestDto.getDescription(),
                productRequestDto.getImage(),
                productRequestDto.getCategory()
        );
    }

    @DeleteMapping("/products/{id}")
    public Product deleteProduct(@PathVariable("id") Long id){

        return productService.deleteProduct(id);
    }

    @GetMapping("/products/categories")
    public String[] getAllCategory(){
        return productService.getAllCategory();
    }

    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable("id") Long id, @RequestBody CreateProductRequestDto productRequestDto){
        return productService.updateProduct(id,
                productRequestDto.getTitle(),
                productRequestDto.getPrice(),
                productRequestDto.getDescription(),
                productRequestDto.getImage(),
                productRequestDto.getCategory());
    }

    @GetMapping("/products/category/{title}")
    public List<Product> getAllProductByCategory(@PathVariable("title") String title){
        return productService.getAllProductByCategory(title);
    }
}
