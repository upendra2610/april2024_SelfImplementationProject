package org.scaler.productservice.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.scaler.productservice.dtos.CreateProductRequestDto;
import org.scaler.productservice.exceptions.NotFoundException;
import org.scaler.productservice.models.Category;
import org.scaler.productservice.models.Product;
import org.scaler.productservice.service.Productservice;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductControllerTest {

    private final Productservice productservice = Mockito.mock(Productservice.class);

    private final ProductController productController = new ProductController(productservice);

    @Test
    public void testGetProductByIdWhenFound() throws NotFoundException {
//        Arrange
        Category c = new Category();
        c.setId(1L);
        c.setTitle("phone");
        Product dummy = new Product();
        dummy.setId(1L);
        dummy.setImage("phone image");
        dummy.setCategory(c);
        dummy.setPrice(20000.00);
        dummy.setDescription("gamming phone");
        dummy.setTitle("asus rog 7");

        when(productservice.getProductById(1L)).thenReturn(dummy);

//        ACT

        ResponseEntity<Product> response = productController.getProductById(1L);

//        ASSERT

        assertEquals("phone image", response.getBody().getImage());
        assertEquals(20000.00, response.getBody().getPrice());
        assertEquals("gamming phone", response.getBody().getDescription());
        assertEquals("asus rog 7", response.getBody().getTitle());
        assertEquals("phone", response.getBody().getCategory().getTitle());


    }

    @Test
    public void testGetproductByIdWhenThrowsNotfoundException() throws NotFoundException {
//        ARRANGE
        when(productController.getProductById(any())).thenThrow(NotFoundException.class);

//        ACT AND ASSERT
        assertThrows(NotFoundException.class, () -> productController.getProductById(any()));
    }

    @Test
    public void testGetAllProductsReturningListOfProduct() throws NotFoundException {

//        ARRANGE
        Product p1 = new Product();
        p1.setId(1L);
        p1.setDescription("Gamming laptop");
        p1.setImage("image of laptop");
        p1.setPrice(50000.00);
        Category c1 = new Category();
        c1.setId(1L);
        c1.setTitle("Laptop");
        p1.setCategory(c1);

        Product p2 = new Product();
        p2.setId(2L);
        p2.setDescription("Camera phone");
        p2.setImage("image of phone");
        p2.setPrice(40000.00);
        Category c2 = new Category();
        c2.setId(2L);
        c2.setTitle("Phone");
        p2.setCategory(c2);

        List<Product> dummyProducts = new ArrayList<>();
        dummyProducts.add(p1);
        dummyProducts.add(p2);

        when(productservice.getAllProduct()).thenReturn(dummyProducts);

//        ACT

        ResponseEntity<List<Product>> response = productController.getAllProduct();

        List<Product> products = response.getBody();

//        ASSERT
        assertEquals(2,products.size());
        assertEquals("Gamming laptop", products.get(0).getDescription());
        assertEquals("image of laptop",products.get(0).getImage());
        assertEquals(50000.00,products.get(0).getPrice());
        assertEquals("Laptop",products.get(0).getCategory().getTitle());

        assertEquals("Camera phone", products.get(1).getDescription());
        assertEquals("image of phone",products.get(1).getImage());
        assertEquals(40000.00,products.get(1).getPrice());
        assertEquals("Phone",products.get(1).getCategory().getTitle());


    }

    @Test
    public void testGetAllProductsWhenThrowsNotFoundException() throws NotFoundException {
//        ARRANGE
        when(productservice.getAllProduct()).thenThrow(NotFoundException.class);

//        ACT and ASSERT

        assertThrows(NotFoundException.class, ()->productController.getAllProduct());

    }

    @Test
    public void testCreateProductSuccess(){
//        ARRANGE
        CreateProductRequestDto p1 = new CreateProductRequestDto();
        p1.setDescription("Gamming laptop");
        p1.setImage("image of laptop");
        p1.setPrice(50000.00);
        p1.setCategory("Laptop");

        Product p2 = new Product();
        p2.setId(1L);
        p2.setDescription("Gamming laptop");
        p2.setImage("image of laptop");
        p2.setPrice(50000.00);
        Category c2 = new Category();
        c2.setId(1L);
        c2.setTitle("Laptop");
        p2.setCategory(c2);

        when(productservice.createProduct(p1.getTitle(),p1.getPrice(),p1.getDescription(),p1.getImage(),p1.getCategory())).thenReturn(p2);

//        ACT
        Product response = productController.createProduct(p1);

//        ASSERT
        assertEquals("Gamming laptop", response.getDescription());
        assertEquals("image of laptop",response.getImage());
        assertEquals(50000.00,response.getPrice());
        assertEquals("Laptop",response.getCategory().getTitle());

    }

    @Test
    public void testDeleteProductWhenSuccess() throws NotFoundException {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setDescription("Gamming laptop");
        p1.setImage("image of laptop");
        p1.setPrice(50000.00);
        Category c1 = new Category();
        c1.setId(1L);
        c1.setTitle("Laptop");
        p1.setCategory(c1);

        when(productservice.deleteProduct(1L)).thenReturn(p1);

//        ACT
        ResponseEntity<Product> response = productController.deleteProduct(1L);

//        ASSERT
        assertEquals(1L, response.getBody().getId());
        assertEquals("Gamming laptop", response.getBody().getDescription());
        assertEquals("image of laptop",response.getBody().getImage());
        assertEquals(50000.00,response.getBody().getPrice());
        assertEquals("Laptop",response.getBody().getCategory().getTitle());
    }

    @Test
    public void testDeleteProductWhenThrowNotfoundException() throws NotFoundException {
        when(productservice.deleteProduct(1L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, ()-> productController.deleteProduct(1L));
    }

    @Test
    public void testUpdateProductWhenSuccess() throws NotFoundException {
//        ARRANGE
        CreateProductRequestDto p1 = new CreateProductRequestDto();
        p1.setDescription("Gamming laptop");
        p1.setImage("image of laptop");
        p1.setPrice(50000.00);
        p1.setCategory("Laptop");
        Long id = 1L;

        when(productservice.updateProduct(id,p1.getTitle(),
                p1.getPrice(),
                p1.getDescription(),
                p1.getImage(),
                p1.getCategory())).thenThrow(NotFoundException.class);

//        ACT & ASSERT
        assertThrows(NotFoundException.class, ()->productController.updateProduct(id,p1));

    }

    @Test
    public void testGetAllProductByCategoryWhenSuccess() throws NotFoundException {
//        ARRANGE
        Product p1 = new Product();
        p1.setId(1L);
        p1.setDescription("Gamming phone");
        p1.setImage("image of phone");
        p1.setPrice(50000.00);
        Category c1 = new Category();
        c1.setId(1L);
        c1.setTitle("Phone");
        p1.setCategory(c1);

        Product p2 = new Product();
        p2.setId(2L);
        p2.setDescription("Camera phone");
        p2.setImage("image of phone");
        p2.setPrice(40000.00);
        Category c2 = new Category();
        c2.setId(1L);
        c2.setTitle("Phone");
        p2.setCategory(c2);

        List<Product> products = new ArrayList<>();
        products.add(p1);
        products.add(p2);

        when(productservice.getAllProductByCategory("Phone")).thenReturn(products);

//        ACT
        ResponseEntity<List<Product>> response = productController.getAllProductByCategory("Phone");

//        ASSERT
        assertEquals(2,response.getBody().size());
        assertEquals("Gamming phone", response.getBody().get(0).getDescription());
        assertEquals("image of phone",response.getBody().get(0).getImage());
        assertEquals(50000.00,response.getBody().get(0).getPrice());
        assertEquals("Phone",response.getBody().get(0).getCategory().getTitle());

        assertEquals("Camera phone", response.getBody().get(1).getDescription());
        assertEquals("image of phone",response.getBody().get(1).getImage());
        assertEquals(40000.00,response.getBody().get(1).getPrice());
        assertEquals("Phone",response.getBody().get(1).getCategory().getTitle());

    }

    @Test
    public void testGetAllProductsByCategoryWhenThrowNotfoundException() throws NotFoundException {
//        ARRANGE
        when(productservice.getAllProductByCategory("Phone")).thenThrow(NotFoundException.class);

//        ACT & ASSERT
        assertThrows(NotFoundException.class, ()->productController.getAllProductByCategory("Phone"));
    }


}