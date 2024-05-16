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
import java.util.List;

@Service("fakestoreProductService")
public class FakeStoreProductService implements Productservice {
    private final RestTemplate restTemplate;
    private final RedisTemplate<Long, FakeStoreProductDto> redisTemplate;

    public FakeStoreProductService(RestTemplate restTemplate,
                                   RedisTemplate<Long, FakeStoreProductDto> redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Product getProductById(Long id) throws NotFoundException {
        //checking in cache memory
        FakeStoreProductDto fakeStoreProductFromCache = redisTemplate.opsForValue().get(id);
        //for cache hit
        if(fakeStoreProductFromCache != null){
            return fakeStoreProductFromCache.toProduct();
        }

        //for cache miss
        FakeStoreProductDto fakeStoreProductDto = restTemplate.getForObject(
                "https://fakestoreapi.com/products/" + id,
                org.scaler.productservice.dtos.FakeStoreProductDto.class
        );
        //Handling Exception
        if(fakeStoreProductDto == null) {
            throw new NotFoundException("Product with id:"+id+" not exist");
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
        if(response == null){
            throw  new NotFoundException("Product Not Exist");
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

        FakeStoreProductDto fakeStoreProductDto = restTemplate.getForObject(
                "https://fakestoreapi.com/products/" + id,
                org.scaler.productservice.dtos.FakeStoreProductDto.class
        );
        //Handling exception
        if(fakeStoreProductDto == null){
            throw new NotFoundException("Product with id:"+id+" not exist");
        }

        //If response is not null(main logic)
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<FakeStoreProductDto> response = restTemplate.exchange(
                "https://fakestoreapi.com/products/{id}", HttpMethod.DELETE, requestEntity,
                FakeStoreProductDto.class, id);

        return response.getBody().toProduct();
    }


    @Override
    public Product updateProduct(Long id, String title, Double price, String description, String image, String category) throws NotFoundException {

        FakeStoreProductDto fakeStoreProduct = restTemplate.getForObject(
                "https://fakestoreapi.com/products/" + id,
                org.scaler.productservice.dtos.FakeStoreProductDto.class
        );
        //Handling exception
        if(fakeStoreProduct == null){
            throw new NotFoundException("Product with id:"+id+" not exist");
        }

        //if product exist with id(main logic)
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
    public List<Product> getAllProductByCategory(String title) throws NotFoundException {

        FakeStoreProductDto[] response = restTemplate.getForObject(
                "https://fakestoreapi.com/products/category/{title}",
                FakeStoreProductDto[].class,
                title);

        //Handling Exception
        if(response == null){
            throw new NotFoundException("There is no product in "+title+" category");
        }

        //when response is not null(main logic)
        List<Product> products = new ArrayList<>();
        for (FakeStoreProductDto fakeStoreProductDto : response) {
            products.add(fakeStoreProductDto.toProduct());
        }
        return products;
    }


}
