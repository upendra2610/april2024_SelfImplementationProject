package org.scaler.productservice.controller;

import org.scaler.productservice.dtos.SearchRequestDto;
import org.scaler.productservice.models.Product;
import org.scaler.productservice.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService){
        this.searchService = searchService;
    }


    @PostMapping("/search")
    public Page<Product> search(@RequestBody SearchRequestDto searchRequestDto){
        return searchService.search(searchRequestDto.getQuery(),
                searchRequestDto.getPageNumber(),
                searchRequestDto.getPageSize());
    }
}
