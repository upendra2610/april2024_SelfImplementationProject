package org.scaler.productservice.controller;

import org.scaler.productservice.dtos.CreateProductRequestDto;
import org.scaler.productservice.models.Product;
import org.scaler.productservice.service.Productservice;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    Productservice productService;

    public ProductController(@Qualifier("selfProductService") Productservice productService){
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable("id") Long id){
        return productService.getProductById(id);
    }

    @GetMapping()
    public List<Product> getAllProduct(){
        return productService.getAllProduct();
    }

    @PostMapping()
    public Product createProduct(@RequestBody CreateProductRequestDto productRequestDto){
        return productService.createProduct(
                productRequestDto.getTitle(),
                productRequestDto.getPrice(),
                productRequestDto.getDescription(),
                productRequestDto.getImage(),
                productRequestDto.getCategory()
        );
    }

    @DeleteMapping("/{id}")
    public Product deleteProduct(@PathVariable("id") Long id){

        return productService.deleteProduct(id);
    }


    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable("id") Long id, @RequestBody CreateProductRequestDto productRequestDto){
        return productService.updateProduct(id,
                productRequestDto.getTitle(),
                productRequestDto.getPrice(),
                productRequestDto.getDescription(),
                productRequestDto.getImage(),
                productRequestDto.getCategory());
    }

    @GetMapping("/categories/{title}")
    public List<Product> getAllProductByCategory(@PathVariable("title") String title){
        return productService.getAllProductByCategory(title);
    }

}
