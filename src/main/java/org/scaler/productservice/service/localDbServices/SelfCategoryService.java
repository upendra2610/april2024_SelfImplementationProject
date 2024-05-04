package org.scaler.productservice.service.localDbServices;

import org.scaler.productservice.exceptions.NotFoundException;
import org.scaler.productservice.models.Category;
import org.scaler.productservice.repository.CategoryRepository;
import org.scaler.productservice.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("selfCategoryService")
public class SelfCategoryService implements CategoryService {
    private final CategoryRepository categoryRepository;

    public SelfCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public String[] getAllCategory() throws NotFoundException {
        List<Category> response = categoryRepository.findAll();
        if (!response.isEmpty()) {
            String[] categories = new String[response.size()];
            int i = 0;
            for (Category category : response) {
                categories[i++] = category.getTitle();
            }
            return categories;
        }
        throw new NotFoundException("There is no categories");
    }

}
