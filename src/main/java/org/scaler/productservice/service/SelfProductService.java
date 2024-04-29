package org.scaler.productservice.service;

import org.scaler.productservice.models.Category;
import org.scaler.productservice.models.Product;
import org.scaler.productservice.repository.CategoryRepository;
import org.scaler.productservice.repository.ProductRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Primary
public class SelfProductService implements Productservice{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    public SelfProductService(ProductRepository productRepository, CategoryRepository categoryRepository){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    @Override
    public Product getProductById(Long id) {
        return productRepository.findByIdIs(id);
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public Product createProduct(String title, double price, String description, String image, String category) {
        Product product = new Product();
        product.setTitle(title);
        product.setPrice(price);
        product.setDescription(description);
        product.setImage(image);
        Category categoryFromDatabase = categoryRepository.findByTitle(category);
        if(categoryFromDatabase == null){
            categoryFromDatabase = new Category();
            categoryFromDatabase.setTitle(category);
        }
        product.setCategory(categoryFromDatabase);

        return productRepository.save(product);
    }

    @Override
    public Product deleteProduct(Long id) {
        Product productFromDb = productRepository.findByIdIs(id);
        productRepository.deleteById(id);
        return productFromDb;
    }

    @Override
    public String[] getAllCategory() {
        List<Category> response = categoryRepository.findAll();
        String[] categories = new String[response.size()];
        int i = 0;
        for(Category category : response){
            categories[i++] = category.getTitle();
        }

        return categories;
    }

    @Override
    public Product updateProduct(Long id, String title, double price, String description, String image, String categoryName) {
        Product currentProduct = productRepository.findByIdIs(id);
        currentProduct.setImage(image);
        currentProduct.setPrice(price);
        currentProduct.setDescription(description);
        currentProduct.setTitle(title);
        Category categoryFromDb = categoryRepository.findByTitle(categoryName);
        if(categoryFromDb == null){
            categoryFromDb = new Category();
            categoryFromDb.setTitle(categoryName);
        }
        currentProduct.setCategory(categoryFromDb);

        return productRepository.save(currentProduct);
    }

    @Override
    public List<Product> getAllProductByCategory(String title) {
        return productRepository.findByCategoryTitle(title);
    }
}
