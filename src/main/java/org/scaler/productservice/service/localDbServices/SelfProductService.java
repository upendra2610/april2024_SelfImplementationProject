package org.scaler.productservice.service.localDbServices;

import org.scaler.productservice.models.Category;
import org.scaler.productservice.models.Product;
import org.scaler.productservice.repository.CategoryRepository;
import org.scaler.productservice.repository.ProductRepository;
import org.scaler.productservice.service.Productservice;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("selfProductService")
public class SelfProductService implements Productservice {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    public SelfProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
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
    public Product createProduct(String title, Double price, String description, String image, String category) {
        Product product = new Product();
        product.setTitle(title);
        product.setPrice(price);
        product.setDescription(description);
        product.setImage(image);
        Category categoryFromDatabase = categoryRepository.findByTitle(category);
        if (categoryFromDatabase == null) {
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
    public Product updateProduct(Long id, String title, Double price, String description, String image, String categoryName) {
        Product currentProduct = productRepository.findByIdIs(id);
        if(title != null){
            currentProduct.setTitle(title);
        }
        if(price != null){
            currentProduct.setPrice(price);
        }
        if(description != null){
            currentProduct.setDescription(description);
        }
        if(image != null){
            currentProduct.setImage(image);
        }
        if(categoryName == null){
            currentProduct.setCategory(currentProduct.getCategory());
        }
        else {
            Category categoryFromDb = categoryRepository.findByTitle(categoryName);
            if (categoryFromDb == null) {
                categoryFromDb = new Category();
                categoryFromDb.setTitle(categoryName);
            }
            currentProduct.setCategory(categoryFromDb);
        }


        return productRepository.save(currentProduct);
    }

    @Override
    public List<Product> getAllProductByCategory(String title) {
        return productRepository.findByCategoryTitle(title);
    }
}
