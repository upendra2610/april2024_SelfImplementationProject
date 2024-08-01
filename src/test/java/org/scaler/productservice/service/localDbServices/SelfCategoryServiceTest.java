package org.scaler.productservice.service.localDbServices;

import org.junit.jupiter.api.Test;
import org.scaler.productservice.exceptions.NotFoundException;
import org.scaler.productservice.models.Category;
import org.scaler.productservice.models.Product;
import org.scaler.productservice.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SelfCategoryServiceTest {
    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private SelfCategoryService selfCategoryService = new SelfCategoryService(categoryRepository);

    @Test
    public void testGetAllProduct() throws NotFoundException {
//        ARRANGE
        Category c1 = new Category();
        c1.setId(1L);
        c1.setTitle("Phone");
        Category c2 = new Category();
        c2.setId(2L);
        c2.setTitle("Laptop");
        Category c3 = new Category();
        c3.setId(3L);
        c3.setTitle("Toys");

        List<Category> categories = new ArrayList<>();
        categories.add(c1);
        categories.add(c2);
        categories.add(c3);

        when(categoryRepository.findAll()).thenReturn(categories);

//        ACT
        String[] response = selfCategoryService.getAllCategory();

//        ASSERT
        assertNotNull(response);
        assertEquals("Phone",response[0]);
        assertEquals("Laptop",response[1]);
        assertEquals("Toys",response[2]);
        verify(categoryRepository,times(1)).findAll();


    }

    @Test
    public void testGetAllCategoriesWhenThrowNotfoundExceptions(){
//        ARRANGE
        when(categoryRepository.findAll()).thenReturn(new ArrayList<>());

//        ACT & ASSERT
        assertThrows(NotFoundException.class, ()->selfCategoryService.getAllCategory());
        verify(categoryRepository,times(1)).findAll();
    }


}