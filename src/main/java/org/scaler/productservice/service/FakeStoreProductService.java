package org.scaler.productservice.service;

import org.scaler.productservice.dtos.FakeStoreProductDto;
import org.scaler.productservice.models.Product;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Primary
public class FakeStoreProductService implements Productservice {
    private final RestTemplate restTemplate;

    public FakeStoreProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Product getProductById(Long id) {
        FakeStoreProductDto fakeStoreProductDto = restTemplate.getForObject(
                "https://fakestoreapi.com/products/" + id,
                org.scaler.productservice.dtos.FakeStoreProductDto.class
        );
        return fakeStoreProductDto.toProduct();
    }

    @Override
    public List<Product> getAllProduct() {
        FakeStoreProductDto[] response = restTemplate.getForObject(
                "https://fakestoreapi.com/products",
                FakeStoreProductDto[].class
        );
        List<Product> products = new ArrayList<>();
        for (FakeStoreProductDto fakeStoreProductDto : response) {
            products.add(fakeStoreProductDto.toProduct());
        }
        return products;
    }

    @Override
    public Product createProduct(String title, double price, String description, String image, String category) {
        FakeStoreProductDto fakeStoreProductDto = new FakeStoreProductDto();
        fakeStoreProductDto.setTitle(title);
        fakeStoreProductDto.setPrice(price);
        fakeStoreProductDto.setDescription(description);
        fakeStoreProductDto.setImage(image);
        fakeStoreProductDto.setCategory(category);

        FakeStoreProductDto response = restTemplate.postForObject(
                "https://fakestoreapi.com/products",
                fakeStoreProductDto,
                FakeStoreProductDto.class
        );
        return response.toProduct();
    }

    @Override
    public Product deleteProduct(Long id) {

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<FakeStoreProductDto> response = restTemplate.exchange(
                "https://fakestoreapi.com/products/{id}", HttpMethod.DELETE, requestEntity,
                FakeStoreProductDto.class, id);

        return response.getBody().toProduct();
    }

    @Override
    public String[] getAllCategory() {
        String[] stringResponse = restTemplate.getForObject(
                "https://fakestoreapi.com/products/categories",
                String[].class
        );
        return stringResponse;
    }

    @Override
    public Product updateProduct(Long id, String title, double price, String description, String image, String category) {
        FakeStoreProductDto fakeStoreProductDto = new FakeStoreProductDto();
        fakeStoreProductDto.setTitle(title);
        fakeStoreProductDto.setPrice(price);
        fakeStoreProductDto.setDescription(description);
        fakeStoreProductDto.setImage(image);
        fakeStoreProductDto.setCategory(category);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FakeStoreProductDto> requestEntity = new HttpEntity<>(fakeStoreProductDto, headers);

        ResponseEntity<FakeStoreProductDto> response = restTemplate.exchange(
                "https://fakestoreapi.com/products/{id}",
                HttpMethod.PUT,
                requestEntity,
                FakeStoreProductDto.class,
                id
        );

        return response.getBody().toProduct();
    }

    @Override
    public List<Product> getAllProductByCategory(String title) {
        FakeStoreProductDto[] response = restTemplate.getForObject(
                "https://fakestoreapi.com/products/category/{title}",
                FakeStoreProductDto[].class,
                title);

        List<Product> products = new ArrayList<>();
        for (FakeStoreProductDto fakeStoreProductDto : response) {
            products.add(fakeStoreProductDto.toProduct());
        }
        return products;
    }


}
