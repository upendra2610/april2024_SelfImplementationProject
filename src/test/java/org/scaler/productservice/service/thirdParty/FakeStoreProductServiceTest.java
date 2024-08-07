package org.scaler.productservice.service.thirdParty;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.scaler.productservice.dtos.FakeStoreProductDto;
import org.scaler.productservice.exceptions.NotFoundException;
import org.scaler.productservice.models.Product;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FakeStoreProductServiceTest {


    private RestTemplate restTemplate = mock(RestTemplate.class);
    private final RedisTemplate redisTemplate = mock(RedisTemplate.class);
    private final RedisTemplate categoryRedisTemplate = mock(RedisTemplate.class);


    private final ValueOperations valueOperations = Mockito.mock(ValueOperations.class);

    private final FakeStoreProductService fakeStoreProductService =
            new FakeStoreProductService(restTemplate, redisTemplate, categoryRedisTemplate);

    @Test
    public void testGetSingleProductWhenCacheHit() throws NotFoundException {
//        ARRANGE
        FakeStoreProductDto testProduct = new FakeStoreProductDto();
        testProduct.setId(1L);
        testProduct.setTitle("Gaming Laptop");
        testProduct.setDescription("Laptop");
        testProduct.setPrice(55000.00);
        testProduct.setImage("Image of product");
        testProduct.setCategory("Laptop");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(1L)).thenReturn(testProduct);

//        ACT
        Product productFromCache = fakeStoreProductService.getProductById(1L);

//        ASSERT
        assertNotNull(productFromCache);
        assertEquals(1L, productFromCache.getId());
        assertEquals("Gaming Laptop", productFromCache.getTitle());
        assertEquals("Laptop", productFromCache.getDescription());
        assertEquals(55000.00, productFromCache.getPrice());
        assertEquals("Image of product", productFromCache.getImage());
        assertEquals("Laptop", productFromCache.getCategory().getTitle());

        verify(valueOperations, times(1)).get(1L);
        verify(valueOperations, times(0)).set(any(), any());
        verify(restTemplate, times(0)).getForObject("https://fakestoreapi.com/products/" + 1L,
                FakeStoreProductDto.class);

    }

    @Test
    public void getProductByIdWhenCacheMiss() throws NotFoundException {
//        ARRANGE
        Long id = 1L;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(1L)).thenReturn(null);

        FakeStoreProductDto fakeStoreProductDto = new FakeStoreProductDto();
        fakeStoreProductDto.setId(1L);
        fakeStoreProductDto.setTitle("Gaming Laptop");
        fakeStoreProductDto.setDescription("Laptop");
        fakeStoreProductDto.setPrice(55000.00);
        fakeStoreProductDto.setImage("Image of product");
        fakeStoreProductDto.setCategory("Laptop");

        when(restTemplate.getForObject(
                "https://fakestoreapi.com/products/" + id, FakeStoreProductDto.class
        )).thenReturn(fakeStoreProductDto);

//        ACT
        Product product = fakeStoreProductService.getProductById(1L);

//        ASSERT
        assertNotNull(product);
        assertEquals(1L, product.getId());
        assertEquals("Gaming Laptop", product.getTitle());
        assertEquals("Laptop", product.getDescription());
        assertEquals(55000.00, product.getPrice());
        assertEquals("Image of product", product.getImage());
        assertEquals("Laptop", product.getCategory().getTitle());

        verify(valueOperations, times(1)).get(id);
        verify(restTemplate, times(1)).getForObject("https://fakestoreapi.com/products/" + id, FakeStoreProductDto.class);
        verify(valueOperations, times(1)).set(id, fakeStoreProductDto);
    }

    @Test
    public void getProductByIdWhenThrowsNotfoundException() {
//        ARRANGE
        Long id = 1L;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(id)).thenReturn(null);

        when(restTemplate.getForObject("https://fakestoreapi.com/products/" + id, FakeStoreProductDto.class))
                .thenReturn(null);

//        ACT & ASSERT
        assertThrows(NotFoundException.class, () -> fakeStoreProductService.getProductById(id));


        verify(valueOperations, times(1)).get(id);
        verify(restTemplate, times(1)).getForObject("https://fakestoreapi.com/products/" + id, FakeStoreProductDto.class);
    }

    @Test
    public void testGetAllProductsWhenReturnListOfProduct() throws NotFoundException {
//        ARRANGE
        FakeStoreProductDto p1 = new FakeStoreProductDto();
        p1.setId(1L);
        p1.setDescription("Gaming Phone");
        p1.setTitle("Phone");
        p1.setImage("image of phone");
        p1.setPrice(35000.00);
        p1.setCategory("Phone");

        FakeStoreProductDto p2 = new FakeStoreProductDto();
        p2.setId(1L);
        p2.setDescription("Gaming Laptop");
        p2.setTitle("Laptop");
        p2.setImage("image of laptop");
        p2.setPrice(55000.00);
        p2.setCategory("Laptop");

        FakeStoreProductDto[] products = {p1, p2};

        when(restTemplate.getForObject("https://fakestoreapi.com/products",
                FakeStoreProductDto[].class)).thenReturn(products);

//        ACT
        List<Product> response = fakeStoreProductService.getAllProduct();

//        ASSERT
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Gaming Phone", response.get(0).getDescription());
        assertEquals("Phone", response.get(0).getTitle());
        assertEquals("image of phone", response.get(0).getImage());
        assertEquals(35000.00, response.get(0).getPrice());
        assertEquals("Phone", response.get(0).getCategory().getTitle());
        assertEquals("Gaming Laptop", response.get(1).getDescription());
        assertEquals("Laptop", response.get(1).getTitle());
        assertEquals("image of laptop", response.get(1).getImage());
        assertEquals(55000.00, response.get(1).getPrice());
        assertEquals("Laptop", response.get(1).getCategory().getTitle());

        verify(restTemplate, times(1)).getForObject("https://fakestoreapi.com/products",
                FakeStoreProductDto[].class);

    }

    @Test
    public void testGetAllProductsWhenThrowsNotFoundException() {
//        ARRANGE
        when(restTemplate.getForObject("https://fakestoreapi.com/products",
                FakeStoreProductDto[].class)).thenReturn(null);

//        ACT & ASSERT
        assertThrows(NotFoundException.class, () -> fakeStoreProductService.getAllProduct());

        verify(restTemplate, times(1)).getForObject("https://fakestoreapi.com/products",
                FakeStoreProductDto[].class);
    }

    @Test
    public void testCreateProduct(){
//        ARRANGE
        String title = "Phone";
        Double price = 35000.00;
        String description = "Gaming Phone";
        String image = "image of phone";
        String category = "Phone";
        FakeStoreProductDto fakeStoreProductDto = new FakeStoreProductDto();
        fakeStoreProductDto.setDescription(description);
        fakeStoreProductDto.setTitle(title);
        fakeStoreProductDto.setImage(image);
        fakeStoreProductDto.setPrice(price);
        fakeStoreProductDto.setCategory(category);

        FakeStoreProductDto responseDto = new FakeStoreProductDto();
        responseDto.setId(1L);
        responseDto.setDescription(description);
        responseDto.setTitle(title);
        responseDto.setImage(image);
        responseDto.setPrice(price);
        responseDto.setCategory(category);

        when(restTemplate.postForObject(
                anyString(),
                any(),
                any())).thenReturn(responseDto);

//        ACT
        Product responseTest = fakeStoreProductService.createProduct(title, price, description, image, category);

//        ASSERT
        assertNotNull(responseTest);
        assertEquals("Gaming Phone", responseTest.getDescription());
        assertEquals("Phone", responseTest.getTitle());
        assertEquals("image of phone", responseTest.getImage());
        assertEquals(35000.00, responseTest.getPrice());
        assertEquals("Phone", responseTest.getCategory().getTitle());
        verify(restTemplate,times(1)).postForObject(
                anyString(),
                any(),
                any());
    }


    @Test
    public void testDeleteProductWhenProductInCache() throws NotFoundException {
//        ARRANGE
        Long id = 1L;
        FakeStoreProductDto p1 = new FakeStoreProductDto();
        p1.setId(1L);
        p1.setDescription("Gaming Phone");
        p1.setTitle("Phone");
        p1.setImage("image of phone");
        p1.setPrice(35000.00);
        p1.setCategory("Phone");

        ResponseEntity<FakeStoreProductDto> resposneEntity = new ResponseEntity<>(p1, HttpStatus.OK);

        when(restTemplate.getForObject("https://fakestoreapi.com/products/" + id,
                org.scaler.productservice.dtos.FakeStoreProductDto.class)).thenReturn(p1);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        when(restTemplate.exchange(
                "https://fakestoreapi.com/products/{id}", HttpMethod.DELETE, requestEntity,
                FakeStoreProductDto.class, id)).thenReturn(resposneEntity);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(id)).thenReturn(p1);

//        ACT
        Product responseTest = fakeStoreProductService.deleteProduct(id);

//        ASSERT
        assertNotNull(responseTest);
        assertEquals(1L, responseTest.getId());
        assertEquals("Gaming Phone", responseTest.getDescription());
        assertEquals("Phone", responseTest.getTitle());
        assertEquals("image of phone", responseTest.getImage());
        assertEquals(35000.00, responseTest.getPrice());
        assertEquals("Phone", responseTest.getCategory().getTitle());
        verify(restTemplate, times(1)).exchange("https://fakestoreapi.com/products/{id}", HttpMethod.DELETE, requestEntity,
                FakeStoreProductDto.class, id);
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get(id);

        verify(redisTemplate, times(1)).delete(id);
        verify(restTemplate, times(1)).getForObject("https://fakestoreapi.com/products/" + id,
                org.scaler.productservice.dtos.FakeStoreProductDto.class);

    }

    @Test
    public void testDeleteProductWhenThrowsNotFoundException(){
//        ARRANGE
        Long id = 1L;
        when(restTemplate.getForObject("https://fakestoreapi.com/products/" + id,
                FakeStoreProductDto.class)).thenReturn(null);

//        ACT & ASSERT
        assertThrows(NotFoundException.class, ()->fakeStoreProductService.deleteProduct(id));
        verify(restTemplate,times(1)).getForObject("https://fakestoreapi.com/products/" + id,
                FakeStoreProductDto.class);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        verify(restTemplate,times(0)).exchange("https://fakestoreapi.com/products/{id}", HttpMethod.DELETE, requestEntity,
                FakeStoreProductDto.class, id);
        verify(valueOperations,times(0)).get(id);
    }

    @Test
    public void testGetAllProductBycategoryWhenCacheHit() throws NotFoundException {
//        ARRANGE
        String searchCategory = "Phone";
        FakeStoreProductDto p1 = new FakeStoreProductDto();
        p1.setId(1L);
        p1.setDescription("Gaming Phone");
        p1.setTitle("Phone");
        p1.setImage("image of phone");
        p1.setPrice(35000.00);
        p1.setCategory("Phone");

        FakeStoreProductDto p2 = new FakeStoreProductDto();
        p2.setId(1L);
        p2.setDescription("Camera Phone");
        p2.setTitle("Phone");
        p2.setImage("image of phone");
        p2.setPrice(45000.00);
        p2.setCategory("Phone");

        List<FakeStoreProductDto> products = new ArrayList<>();
        products.add(p1);
        products.add(p2);

        when(categoryRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(searchCategory)).thenReturn(products);

//        ACT
        List<Product> response = fakeStoreProductService.getAllProductByCategory(searchCategory);

//        ASSERT
        assertNotNull(response);
        assertEquals(2,response.size());
        assertEquals("Gaming Phone", response.get(0).getDescription());
        assertEquals("Phone", response.get(0).getTitle());
        assertEquals("image of phone", response.get(0).getImage());
        assertEquals(35000.00, response.get(0).getPrice());
        assertEquals("Phone", response.get(0).getCategory().getTitle());
        assertEquals("Camera Phone", response.get(1).getDescription());
        assertEquals("Phone", response.get(1).getTitle());
        assertEquals("image of phone", response.get(1).getImage());
        assertEquals(45000.00, response.get(1).getPrice());
        assertEquals("Phone", response.get(1).getCategory().getTitle());
        verify(valueOperations,times(1)).get(searchCategory);
        verify(restTemplate,times(0)).getForObject("https://fakestoreapi.com/products/category/"+searchCategory,
                FakeStoreProductDto[].class);
    }

    @Test
    public void testGetAllProductByCategoryWhenCacheMiss() throws NotFoundException {
//        ARRANGE
        String title = "Phone";
        FakeStoreProductDto p1 = new FakeStoreProductDto();
        p1.setId(1L);
        p1.setDescription("Gaming Phone");
        p1.setTitle("Phone");
        p1.setImage("image of phone");
        p1.setPrice(35000.00);
        p1.setCategory("Phone");

        FakeStoreProductDto p2 = new FakeStoreProductDto();
        p2.setId(1L);
        p2.setDescription("Camera Phone");
        p2.setTitle("Phone");
        p2.setImage("image of phone");
        p2.setPrice(45000.00);
        p2.setCategory("Phone");

        FakeStoreProductDto[] products = {p1,p2};

        when(categoryRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(title)).thenReturn(null);

        when(restTemplate.getForObject("https://fakestoreapi.com/products/category/{title}",
                FakeStoreProductDto[].class,
                title)).thenReturn(products);

//        ACT
        List<Product> response = fakeStoreProductService.getAllProductByCategory(title);

//        ASSEERT
        assertNotNull(response);
        assertEquals(2,response.size());
        assertEquals("Gaming Phone", response.get(0).getDescription());
        assertEquals("Phone", response.get(0).getTitle());
        assertEquals("image of phone", response.get(0).getImage());
        assertEquals(35000.00, response.get(0).getPrice());
        assertEquals("Phone", response.get(0).getCategory().getTitle());
        assertEquals("Camera Phone", response.get(1).getDescription());
        assertEquals("Phone", response.get(1).getTitle());
        assertEquals("image of phone", response.get(1).getImage());
        assertEquals(45000.00, response.get(1).getPrice());
        assertEquals("Phone", response.get(1).getCategory().getTitle());
        verify(valueOperations,times(1)).get(title);
        verify(restTemplate,times(1)).getForObject(
                "https://fakestoreapi.com/products/category/{title}",
                FakeStoreProductDto[].class,
                title);
        verify(valueOperations,times(1)).set(title, Arrays.stream(products).toList());
    }

    @Test
    public void testGetAllProductsByCategaoryWhenThrowsNotFoundException() {
//        ARRANGE
        when(categoryRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("Phone")).thenReturn(null);

        when(restTemplate.getForObject("https://fakestoreapi.com/products/category/{title}",
                FakeStoreProductDto[].class,
                "Phone")).thenReturn(null);

//        ACT & ASSEERT
        assertThrows(NotFoundException.class, () -> fakeStoreProductService.getAllProductByCategory("Phone"));
        verify(valueOperations, times(1)).get("Phone");
        verify(restTemplate,times(1)).getForObject("https://fakestoreapi.com/products/category/{title}",
                FakeStoreProductDto[].class,
                "Phone");
        verify(valueOperations,times(0)).set("Phone", new ArrayList<>());
    }

    @Test
    public void testUpdateExistingProductWithAllFields() throws NotFoundException {
//        ARRANGE
        FakeStoreProductDto existingProduct = new FakeStoreProductDto();
        existingProduct.setId(1L);
        existingProduct.setTitle("Old Title");
        existingProduct.setPrice(100.0);
        existingProduct.setDescription("Old Description");
        existingProduct.setImage("Old Image");
        existingProduct.setCategory("Old Category");

        FakeStoreProductDto updatedProduct = new FakeStoreProductDto();
        updatedProduct.setId(1L);
        updatedProduct.setTitle("New Title");
        updatedProduct.setPrice(200.00);
        updatedProduct.setDescription("New Description");
        updatedProduct.setImage("New Image");
        updatedProduct.setCategory("New Category");

        when(restTemplate.getForObject("https://fakestoreapi.com/products/1", FakeStoreProductDto.class))
                .thenReturn(existingProduct);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(1L)).thenReturn(existingProduct);
        when(restTemplate.exchange(eq("https://fakestoreapi.com/products/{id}"),
                        eq(HttpMethod.PUT),
                        any(HttpEntity.class),
                        eq(FakeStoreProductDto.class),
                        eq(1L)))
                .thenReturn(new ResponseEntity<>(updatedProduct, HttpStatus.OK));

//        ACT
        Product result = fakeStoreProductService.updateProduct(1L, "New Title", 200.0, "New Description", "New Image", "New Category");

//        ASSERT
        assertEquals("New Title", result.getTitle());
        assertEquals(200.0, result.getPrice());
        assertEquals("New Description", result.getDescription());
        assertEquals("New Image", result.getImage());
        assertEquals("New Category", result.getCategory().getTitle());
        verify(restTemplate,times(1)).getForObject("https://fakestoreapi.com/products/1", FakeStoreProductDto.class);
        verify(redisTemplate,times(2)).opsForValue();
        verify(valueOperations,times(1)).get(1L);
        verify(restTemplate,times(1)).exchange(eq("https://fakestoreapi.com/products/{id}"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(FakeStoreProductDto.class),
                eq(1L));
        verify(valueOperations,times(1)).set(any(),any());
    }

    @Test
    public void testUpdateExistingProductWithSomeNullFields() throws NotFoundException {
//        ARRNGE
        FakeStoreProductDto existingProduct = new FakeStoreProductDto();
        existingProduct.setId(1L);
        existingProduct.setTitle("Old Title");
        existingProduct.setPrice(100.0);
        existingProduct.setDescription("Old Description");
        existingProduct.setImage("Old Image");
        existingProduct.setCategory("Old Category");

        FakeStoreProductDto updatedProduct = new FakeStoreProductDto();
        updatedProduct.setId(1L);
        updatedProduct.setTitle("New Title");
        updatedProduct.setPrice(100.00);
        updatedProduct.setDescription("Old Description");
        updatedProduct.setImage("New Image");
        updatedProduct.setCategory("Old Category");

        when(restTemplate.getForObject("https://fakestoreapi.com/products/1", FakeStoreProductDto.class))
                .thenReturn(existingProduct);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(1L)).thenReturn(null);
        when(restTemplate.exchange(eq("https://fakestoreapi.com/products/{id}"),
                        eq(HttpMethod.PUT),
                        any(HttpEntity.class),
                        eq(FakeStoreProductDto.class),
                        eq(1L)))
                .thenReturn(new ResponseEntity<>(updatedProduct, HttpStatus.OK));

//        ACT
        Product result = fakeStoreProductService.updateProduct(1L, "New Title", null, null, "New Image", null);

//        ASSERT
        assertEquals("New Title", result.getTitle());
        assertEquals(100.0, result.getPrice());
        assertEquals("Old Description", result.getDescription());
        assertEquals("New Image", result.getImage());
        assertEquals("Old Category", result.getCategory().getTitle());
        verify(valueOperations,times(1)).get(1L);
        verify(valueOperations,times(0)).set(any(),any());
        verify(restTemplate,times(1)).getForObject("https://fakestoreapi.com/products/1", FakeStoreProductDto.class);
        verify(restTemplate,times(1)).exchange(eq("https://fakestoreapi.com/products/{id}"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(FakeStoreProductDto.class),
                eq(1L));
    }

    @Test
    public void testUpdateProductWhenCachedInRedis() throws NotFoundException {
//        ARRANGE
        FakeStoreProductDto existingProduct = new FakeStoreProductDto();
        existingProduct.setId(1L);
        existingProduct.setTitle("Old Title");
        existingProduct.setPrice(100.0);
        existingProduct.setDescription("Old Description");
        existingProduct.setImage("Old Image");
        existingProduct.setCategory("Old Category");

        FakeStoreProductDto updatedProduct = new FakeStoreProductDto();
        updatedProduct.setId(1L);
        updatedProduct.setTitle("New Title");
        updatedProduct.setPrice(200.00);
        updatedProduct.setDescription("New Description");
        updatedProduct.setImage("New Image");
        updatedProduct.setCategory("New Category");

        when(restTemplate.getForObject("https://fakestoreapi.com/products/1", FakeStoreProductDto.class))
                .thenReturn(existingProduct);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(1L)).thenReturn(existingProduct);
//        doNothing().when(valueOperations).set(1L, updatedProduct);
        when(restTemplate.exchange(eq("https://fakestoreapi.com/products/{id}"),
                        eq(HttpMethod.PUT),
                        any(HttpEntity.class),
                        eq(FakeStoreProductDto.class),
                        eq(1L)))
                .thenReturn(new ResponseEntity<>(updatedProduct, HttpStatus.OK));

//        ACT
        Product result = fakeStoreProductService.updateProduct(1L, "New Title", 200.0, "New Description", "New Image", "New Category");

//        ASSERT
        assertEquals("New Title", result.getTitle());
        assertEquals(200.0, result.getPrice(), 0.01);
        assertEquals("New Description", result.getDescription());
        assertEquals("New Image", result.getImage());
        assertEquals("New Category", result.getCategory().getTitle());
        verify(restTemplate,times(1)).getForObject("https://fakestoreapi.com/products/1", FakeStoreProductDto.class);
        verify(valueOperations,times(1)).get(1L);
        verify(valueOperations,times(1)).set(any(),any());
    }


    @Test
    public void testUpdateNonExistentProduct(){
//        ARRANGE
        when(restTemplate.getForObject("https://fakestoreapi.com/products/1", FakeStoreProductDto.class))
                .thenReturn(null);

//        ACT & ASSERT
        assertThrows(NotFoundException.class, ()->fakeStoreProductService.updateProduct(1L, "New Title", 200.0, "New Description", "New Image", "New Category"));
        verify(restTemplate,times(1)).getForObject("https://fakestoreapi.com/products/1", FakeStoreProductDto.class);
        verify(valueOperations,times(0)).get(any());
        verify(valueOperations,times(0)).set(any(),any());
    }


}