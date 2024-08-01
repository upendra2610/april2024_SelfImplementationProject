package org.scaler.productservice.service.localDbServices;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.scaler.productservice.exceptions.NotFoundException;
import org.scaler.productservice.models.Category;
import org.scaler.productservice.models.Product;
import org.scaler.productservice.repository.CategoryRepository;
import org.scaler.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class SelfProductServiceTest {

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CategoryRepository categoryRepository;


    @Autowired
    private SelfProductService selfProductService = new SelfProductService(productRepository,categoryRepository);

    @Test
    public void testGetProductByIdWhenThereIsProduct() throws NotFoundException {
//        ARRANGE
        Long id = 1L;
        Product testProduct = new Product();
        testProduct.setId(id);
        testProduct.setTitle("Phone");
        testProduct.setDescription("Gaming Phone");
        testProduct.setPrice(55000.00);
        testProduct.setImage("image of phone");
        Category c = new Category();
        c.setId(1L);
        c.setTitle("Phone");
        testProduct.setCategory(c);

        when(productRepository.findById(id)).thenReturn(Optional.of(testProduct));

//        ACT
        Product response = selfProductService.getProductById(id);

//        ASSERT
        assertNotNull(response);
        assertEquals("Phone",response.getTitle());
        assertEquals("Gaming Phone",response.getDescription());
        assertEquals(55000.00,response.getPrice());
        assertEquals("image of phone",response.getImage());
        assertEquals("Phone",response.getCategory().getTitle());
        verify(productRepository,times(1)).findById(id);
    }

    @Test
    public void testGetProductByIdWhenThrowsNotfoundException(){
//        ARRANGE
        when(productRepository.findById(any())).thenReturn(Optional.empty());
//        If you give null instead of Optional.empty() when mocking the behavior of productRepository.findById(id),
//        the test will fail because the actual findById method of JpaRepository returns an Optional, not null.
//        Returning null would lead to a NullPointerException.

//        ACT & ASSERT
        assertThrows(NotFoundException.class, ()->selfProductService.getProductById(any()));
        verify(productRepository,times(1)).findById(any());
    }

    @Test
    public void testGetAllProductsWhenReturnsListOfProducts() throws NotFoundException {
//        ARRANGE
        Category c1 = new Category();
        c1.setId(1L);
        c1.setTitle("Phone");
        Category c2 = new Category();
        c2.setId(2L);
        c2.setTitle("Laptop");

        Product p1 = new Product();
        p1.setId(1L);
        p1.setTitle("Phone");
        p1.setDescription("Gaming Phone");
        p1.setPrice(55000.00);
        p1.setImage("image of phone");
        p1.setCategory(c1);


        Product p2 = new Product();
        p2.setId(2L);
        p2.setTitle("Laptop");
        p2.setDescription("Gaming Laptop");
        p2.setPrice(75000.00);
        p2.setImage("image of laptop");
        p2.setCategory(c2);

        List<Product> products = new ArrayList<>();
        products.add(p1);
        products.add(p2);

        when(productRepository.findAll()).thenReturn(products);

//        ACT
        List<Product> response = selfProductService.getAllProduct();

//        ASSERT
        assertNotNull(response);
        assertEquals(2,response.size());
        assertEquals("Phone",response.get(0).getTitle());
        assertEquals("Gaming Phone",response.get(0).getDescription());
        assertEquals(55000.00,response.get(0).getPrice());
        assertEquals("image of phone",response.get(0).getImage());
        assertEquals("Phone",response.get(0).getCategory().getTitle());

        assertEquals("Laptop",response.get(1).getTitle());
        assertEquals("Gaming Laptop",response.get(1).getDescription());
        assertEquals(75000.00,response.get(1).getPrice());
        assertEquals("image of laptop",response.get(1).getImage());
        assertEquals("Laptop",response.get(1).getCategory().getTitle());
        verify(productRepository,times(1)).findAll();
    }

    @Test
    public void testGetAllProductWhenThrowsNotfoundException(){
//        ARRANGE
        when(productRepository.findAll()).thenReturn(new ArrayList<>());

//        ACT & ASSERT
        assertThrows(NotFoundException.class, ()->selfProductService.getAllProduct());
        verify(productRepository,times(1)).findAll();
    }

    @Test
    public void testCreateProductWithExistingCategory(){

//        ARRANGE
        String title = "Phone";
        Double price = 30000.00;
        String description = "BestCamera phone";
        String image = "image of phone";
        String category = "Phone";
        Product p = new Product();
        p.setId(1L);
        p.setTitle(title);
        p.setDescription(description);
        p.setImage(image);
        p.setPrice(price);
        Category c = new Category();
        c.setId(1L);
        c.setTitle("Phone");
        p.setCategory(c);

        when(categoryRepository.findByTitle(any())).thenReturn(c);
        when(productRepository.save(any())).thenReturn(p);

//        ACT
        Product response = selfProductService.createProduct(title, price, description, image, category);

//        ASSERT
        assertNotNull(response);
        assertEquals("Phone",response.getTitle());
        assertEquals("BestCamera phone",response.getDescription());
        assertEquals(30000.00,response.getPrice());
        assertEquals("image of phone",response.getImage());
        assertEquals("Phone",response.getCategory().getTitle());
        verify(categoryRepository,times(1)).findByTitle(anyString());
        verify(productRepository,times(1)).save(any());
    }

    @Test
    public void testCreateProductWithNewCategory(){
//        ARRANGE
        String title = "Phone";
        Double price = 30000.00;
        String description = "BestCamera phone";
        String image = "image of phone";
        String category = "Phone";
        Product p = new Product();
        p.setId(1L);
        p.setTitle(title);
        p.setDescription(description);
        p.setImage(image);
        p.setPrice(price);
        Category c = new Category();
        c.setId(1L);
        c.setTitle("Phone");
        p.setCategory(c);

        when(categoryRepository.findByTitle(category)).thenReturn(null);
        when(productRepository.save(any(Product.class))).thenReturn(p);

//        ACT
        Product response = selfProductService.createProduct(title, price, description, image, category);

//        ASSERT
        assertNotNull(response);
        assertEquals("Phone",response.getTitle());
        assertEquals("BestCamera phone",response.getDescription());
        assertEquals(30000.00,response.getPrice());
        assertEquals("image of phone",response.getImage());
        assertEquals("Phone",response.getCategory().getTitle());
        verify(categoryRepository,times(1)).findByTitle(category);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());

//        The error when calling verify(productRepository, times(1)).save(p)
//        is likely because the exact instance of the Product object passed to the save method in the actual service method might not match
//        the instance you're creating in the test. Even if the attributes are the same, the instances might differ, leading to verification failure.
//        To solve this, you can use an ArgumentCaptor to capture the Product object passed to the save method and then verify its attributes.

    }

    @Test
    public void testDeleteProduct() throws NotFoundException {
//        ARRANGE
        Long id = 1L;
        Product testProduct = new Product();
        testProduct.setId(id);
        testProduct.setTitle("Phone");
        testProduct.setDescription("Gaming Phone");
        testProduct.setPrice(55000.00);
        testProduct.setImage("image of phone");
        Category c = new Category();
        c.setId(1L);
        c.setTitle("Phone");
        testProduct.setCategory(c);

        when(productRepository.findById(id)).thenReturn(Optional.of(testProduct));

//        ACT
        Product response = selfProductService.deleteProduct(id);

//        ASSERT
        assertNotNull(response);
        assertEquals("Phone",response.getTitle());
        assertEquals("Gaming Phone",response.getDescription());
        assertEquals(55000.00,response.getPrice());
        assertEquals("image of phone",response.getImage());
        assertEquals("Phone",response.getCategory().getTitle());
        verify(productRepository,times(1)).findById(id);

    }

    @Test
    public void testDeleteProductWhenThrowsNotfoundException(){
//        ARRANGE
        when(productRepository.findById(any())).thenReturn(Optional.empty());

//        ACT & ASSERT
        assertThrows(NotFoundException.class, ()->selfProductService.deleteProduct(any()));
        verify(productRepository,times(1)).findById(any());
    }

    @Test
    public void testGetAllProductByCategory() throws NotFoundException {
//        ARRANGE
        Category c1 = new Category();
        c1.setId(1L);
        c1.setTitle("Phone");
        Product p1 = new Product();
        p1.setId(1L);
        p1.setTitle("Phone");
        p1.setDescription("Gaming Phone");
        p1.setPrice(55000.00);
        p1.setImage("image of phone");
        p1.setCategory(c1);

        Product p2 = new Product();
        p2.setId(2L);
        p2.setTitle("Phone");
        p2.setDescription("Camera Phone");
        p2.setPrice(35000.00);
        p2.setImage("image of phone");
        p2.setCategory(c1);

        List<Product> products = new ArrayList<>();
        products.add(p1);
        products.add(p2);

        when(productRepository.findByCategoryTitle("Phone")).thenReturn(products);

//        ACT
        List<Product> response = selfProductService.getAllProductByCategory("Phone");

//        ASSERT
        assertNotNull(response);
        assertEquals(2,response.size());
        assertEquals("Phone",response.get(0).getTitle());
        assertEquals("Gaming Phone",response.get(0).getDescription());
        assertEquals(55000.00,response.get(0).getPrice());
        assertEquals("image of phone",response.get(0).getImage());
        assertEquals("Phone",response.get(0).getCategory().getTitle());

        assertEquals("Phone",response.get(1).getTitle());
        assertEquals("Camera Phone",response.get(1).getDescription());
        assertEquals(35000.00,response.get(1).getPrice());
        assertEquals("image of phone",response.get(1).getImage());
        assertEquals("Phone",response.get(1).getCategory().getTitle());
        verify(productRepository,times(1)).findByCategoryTitle("Phone");
    }

    @Test
    public void testGetAllProdutByCategoryWhenThrowsnotfoundException(){
        when(productRepository.findByCategoryTitle(anyString())).thenReturn(new ArrayList<>());

        assertThrows(NotFoundException.class,()->selfProductService.getAllProductByCategory(anyString()));
        verify(productRepository,times(1)).findByCategoryTitle(anyString());
    }

}