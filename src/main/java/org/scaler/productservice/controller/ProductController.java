package org.scaler.productservice.controller;

import org.scaler.productservice.dtos.CreateProductRequestDto;
import org.scaler.productservice.exceptions.NotFoundException;
import org.scaler.productservice.models.Product;
import org.scaler.productservice.service.Productservice;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    Productservice productService;

    public ProductController(@Qualifier("fakestoreProductService") Productservice productService){
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) throws NotFoundException {
        return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<Product>> getAllProduct() throws NotFoundException {
        return new ResponseEntity<>(productService.getAllProduct(), HttpStatus.OK);
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
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") Long id) throws NotFoundException {

        return new ResponseEntity<>(productService.deleteProduct(id), HttpStatus.NO_CONTENT);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long id, @RequestBody CreateProductRequestDto productRequestDto) throws NotFoundException {
        return new ResponseEntity<>(productService.updateProduct(id,
                productRequestDto.getTitle(),
                productRequestDto.getPrice(),
                productRequestDto.getDescription(),
                productRequestDto.getImage(),
                productRequestDto.getCategory()), HttpStatus.OK);
    }

    @GetMapping("/categories/{title}")
    public ResponseEntity<List<Product>> getAllProductByCategory(@PathVariable("title") String title) throws NotFoundException {
        return new ResponseEntity<>(productService.getAllProductByCategory(title),
                HttpStatus.OK);
    }

}
