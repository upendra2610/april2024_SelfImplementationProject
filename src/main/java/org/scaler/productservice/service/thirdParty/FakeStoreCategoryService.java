package org.scaler.productservice.service.thirdParty;

import org.scaler.productservice.exceptions.NotFoundException;
import org.scaler.productservice.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("fakeStoreCategoryService")
public class FakeStoreCategoryService implements CategoryService {
    private final RestTemplate restTemplate;

    public FakeStoreCategoryService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }
    @Override
    public String[] getAllCategory() throws NotFoundException {
        String[] stringResponse = restTemplate.getForObject(
                "https://fakestoreapi.com/products/categories",
                String[].class
        );
        if(stringResponse == null){
            throw new NotFoundException("There is no values");
        }
        return stringResponse;
    }
}
