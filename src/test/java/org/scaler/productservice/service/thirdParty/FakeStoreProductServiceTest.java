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
                "https://fakestoreapi.com/products",
                fakeStoreProductDto,
                FakeStoreProductDto.class)).thenReturn(responseDto);

        Product responseTest = fakeStoreProductService.createProduct(title, price, description, image, category);

        assertNotNull(responseTest);
        assertEquals("Gaming Phone", responseTest.getDescription());
        assertEquals("Phone", responseTest.getTitle());
        assertEquals("image of phone", responseTest.getImage());
        assertEquals(35000.00, responseTest.getPrice());
        assertEquals("Phone", responseTest.getCategory().getTitle());
        verify(restTemplate,times(1)).postForObject(
                "https://fakestoreapi.com/products",
                fakeStoreProductDto,
                FakeStoreProductDto.class);
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
        Long id = 1L;
        when(restTemplate.getForObject("https://fakestoreapi.com/products/" + id,
                FakeStoreProductDto.class)).thenReturn(null);

        assertThrows(NotFoundException.class, ()->fakeStoreProductService.deleteProduct(id));
        verify(restTemplate,times(1)).getForObject("https://fakestoreapi.com/products/" + id,
                FakeStoreProductDto.class);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        verify(restTemplate,times(0)).exchange("https://fakestoreapi.com/products/{id}", HttpMethod.DELETE, requestEntity,
                FakeStoreProductDto.class, id);

        verify(valueOperations,times(0)).get(id);
    }

}