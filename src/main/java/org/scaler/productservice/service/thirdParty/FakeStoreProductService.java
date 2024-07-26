package org.scaler.productservice.service.thirdParty;

import org.scaler.productservice.dtos.FakeStoreProductDto;
import org.scaler.productservice.exceptions.NotFoundException;
import org.scaler.productservice.models.Product;
import org.scaler.productservice.service.Productservice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("fakestoreProductService")
public class FakeStoreProductService implements Productservice {
    private final RestTemplate restTemplate;
    private final RedisTemplate<Long, FakeStoreProductDto> redisTemplate;
    private final RedisTemplate<String, List<FakeStoreProductDto>> categoryRedisTemplate;

    public FakeStoreProductService(RestTemplate restTemplate,
                                   RedisTemplate<Long, FakeStoreProductDto> redisTemplate,
                                   RedisTemplate<String, List<FakeStoreProductDto>> categoryRedisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
        this.categoryRedisTemplate = categoryRedisTemplate;
    }

    @Override
    public Product getProductById(Long id) throws NotFoundException {
        //checking in cache memory
        FakeStoreProductDto fakeStoreProductFromCache = redisTemplate.opsForValue().get(id);
        //for cache hit
        if (fakeStoreProductFromCache != null) {
            return fakeStoreProductFromCache.toProduct();
        }

        //for cache miss
        FakeStoreProductDto fakeStoreProductDto = restTemplate.getForObject(
                "https://fakestoreapi.com/products/" + id,
                FakeStoreProductDto.class
        );
        //Handling Exception
        if (fakeStoreProductDto == null) {
            throw new NotFoundException("Product with id:" + id + " not exist");
        }

        redisTemplate.opsForValue().set(id, fakeStoreProductDto);

        return fakeStoreProductDto.toProduct();

    }

    @Override
    public List<Product> getAllProduct() throws NotFoundException {
        FakeStoreProductDto[] response = restTemplate.getForObject(
                "https://fakestoreapi.com/products",
                FakeStoreProductDto[].class
        );
        //Handling Exception
        if (response == null) {
            throw new NotFoundException("Product Not Exist");
        }


        List<Product> products = new ArrayList<>();
        for (FakeStoreProductDto fakeStoreProductDto : response) {
            products.add(fakeStoreProductDto.toProduct());
        }
        return products;
    }

    @Override
    public Product createProduct(String title, Double price, String description, String image, String category) {
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
    public Product deleteProduct(Long id) throws NotFoundException {

        //checking if product with id exist or not
        FakeStoreProductDto fakeStoreProductDto = restTemplate.getForObject(
                "https://fakestoreapi.com/products/" + id,
                org.scaler.productservice.dtos.FakeStoreProductDto.class
        );
        //Handling exception
        if (fakeStoreProductDto == null) {
            throw new NotFoundException("Product with id:" + id + " not exist");
        }

        //If response is not null(main logic)
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<FakeStoreProductDto> response = restTemplate.exchange(
                "https://fakestoreapi.com/products/{id}", HttpMethod.DELETE, requestEntity,
                FakeStoreProductDto.class, id);

        //deleting in cache
        FakeStoreProductDto productFromCache = redisTemplate.opsForValue().get(id);
        if(productFromCache != null){
            redisTemplate.delete(id);
        }

        return response.getBody().toProduct();
    }


    @Override
    public Product updateProduct(Long id, String title, Double price, String description, String image, String categoryName) throws NotFoundException {

        FakeStoreProductDto currentProduct = restTemplate.getForObject(
                "https://fakestoreapi.com/products/" + id,
                org.scaler.productservice.dtos.FakeStoreProductDto.class
        );
        //Handling exception
        if (currentProduct == null) {
            throw new NotFoundException("Product with id:" + id + " not exist");
        }

        if (title != null) {
            currentProduct.setTitle(title);
        }
        if (price != null) {
            currentProduct.setPrice(price);
        }
        if (description != null) {
            currentProduct.setDescription(description);
        }
        if (image != null) {
            currentProduct.setImage(image);
        }
        if (categoryName == null) {
            currentProduct.setCategory(currentProduct.getCategory());
        } else {
            currentProduct.setCategory(categoryName);
        }

        //updating in cache as well
        FakeStoreProductDto fakeStoreProductFromCache = redisTemplate.opsForValue().get(id);
        if (fakeStoreProductFromCache != null) {
            redisTemplate.opsForValue().set(id, currentProduct);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FakeStoreProductDto> requestEntity = new HttpEntity<>(currentProduct, headers);

        ResponseEntity<FakeStoreProductDto> responseEntity = restTemplate.exchange(
                "https://fakestoreapi.com/products/{id}",
                HttpMethod.PUT,
                requestEntity,
                FakeStoreProductDto.class,
                id
        );

        return responseEntity.getBody().toProduct();
    }

    @Override
    public List<Product> getAllProductByCategory(String title) throws NotFoundException {

        //checking in cache
        List<FakeStoreProductDto> fakeStoreProducts = categoryRedisTemplate.opsForValue().get(title);
        //cache hit
        if(fakeStoreProducts != null){
            List<Product> products = new ArrayList<>();
            for (FakeStoreProductDto fakeStoreProductDto : fakeStoreProducts) {
                products.add(fakeStoreProductDto.toProduct());
            }
            return products;
        }
        //cache miss
        FakeStoreProductDto[] response = restTemplate.getForObject(
                "https://fakestoreapi.com/products/category/{title}",
                FakeStoreProductDto[].class,
                title);

        //Handling Exception
        if (response == null || response.length == 0) {
            throw new NotFoundException("There is no product in " + title + " category");
        }

        //when response is not null(main logic)
        List<Product> products = new ArrayList<>();
        for (FakeStoreProductDto fakeStoreProductDto : response) {
            products.add(fakeStoreProductDto.toProduct());
        }
        //for cache miss adding list of product into cache
        categoryRedisTemplate.opsForValue().set(title, Arrays.stream(response).toList());

        return products;
    }


}
