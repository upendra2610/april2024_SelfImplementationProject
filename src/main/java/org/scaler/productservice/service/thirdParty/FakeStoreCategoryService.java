package org.scaler.productservice.service.thirdParty;

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
    public String[] getAllCategory() {
        String[] stringResponse = restTemplate.getForObject(
                "https://fakestoreapi.com/products/categories",
                String[].class
        );
        return stringResponse;
    }
}
