package org.scaler.productservice.controller;

import org.scaler.productservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(@Qualifier("selfCategoryService") CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping()
    public String[] getAllCategory(){
        return categoryService.getAllCategory();
    }

}
