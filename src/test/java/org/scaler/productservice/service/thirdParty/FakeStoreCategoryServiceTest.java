package org.scaler.productservice.service.thirdParty;

import org.junit.jupiter.api.Test;
import org.scaler.productservice.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class FakeStoreCategoryServiceTest {
    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private FakeStoreCategoryService fakeStoreCategoryService = new FakeStoreCategoryService(restTemplate);

    @Test
    public void testGetAllCategoriesWhenThereIsData() throws NotFoundException {
        String c1 = "Phone";
        String c2 = "Laptop";
        String c3 = "Toys";
        String c4 = "Electronic";

        String[] categories = {c1,c2,c3,c4};

        when(restTemplate.getForObject("https://fakestoreapi.com/products/categories",
                String[].class)).thenReturn(categories);

        String[] response = fakeStoreCategoryService.getAllCategory();

        assertNotNull(response);
        assertEquals(4,response.length);
        assertEquals("Phone",response[0]);
        assertEquals("Laptop",response[1]);
        assertEquals("Toys",response[2]);
        assertEquals("Electronic",response[3]);
        verify(restTemplate,times(1)).getForObject("https://fakestoreapi.com/products/categories",
                String[].class);

    }

    @Test
    public void testGetAllCategoriesWhenThrowsNotfoundException(){
        when(restTemplate.getForObject("https://fakestoreapi.com/products/categories",
                String[].class)).thenReturn(null);

        assertThrows(NotFoundException.class, ()-> fakeStoreCategoryService.getAllCategory());
        verify(restTemplate,times(1)).getForObject("https://fakestoreapi.com/products/categories",
                String[].class);
    }

}