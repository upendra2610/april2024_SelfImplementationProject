package org.scaler.productservice.service.localDbServices;

import org.scaler.productservice.exceptions.NotFoundException;
import org.scaler.productservice.models.Category;
import org.scaler.productservice.models.Product;
import org.scaler.productservice.repository.CategoryRepository;
import org.scaler.productservice.repository.ProductRepository;
import org.scaler.productservice.service.Productservice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service("selfProductService")
public class SelfProductService implements Productservice {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    public SelfProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product getProductById(Long id) throws NotFoundException {
        Optional<Product> response = productRepository.findById(id);
        if(response.isPresent()){
            return response.get();
        }
        throw new NotFoundException("Product with id:"+id+" doesn't exist");
    }

    @Override
    public List<Product> getAllProduct() throws NotFoundException {
        List<Product> response = productRepository.findAll();
        if(!response.isEmpty()){
            return response;
        }
        throw new NotFoundException("There is no products");
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
    public Product deleteProduct(Long id) throws NotFoundException {
        Optional<Product> productFromDb = productRepository.findById(id);
        if(productFromDb.isPresent()) {
            productRepository.deleteById(id);
            return productFromDb.get();
        }
        throw new NotFoundException("Product with id:"+id+" doesn't exist");
    }

    @Override
    public Product updateProduct(Long id, String title, Double price, String description, String image, String categoryName) throws NotFoundException {
        Optional<Product> response = productRepository.findById(id);
        if(response.isPresent()) {
            Product currentProduct = response.get();
            if (title != null) {
                currentProduct.setTitle(title);
            }
            if (price != null) {
                currentProduct.setPrice(price);
            }
            if (description != null) {
                currentProduct.setDescription(description);
            }
            if (image != null) {
                currentProduct.setImage(image);
            }
            if (categoryName == null) {
                currentProduct.setCategory(currentProduct.getCategory());
            } else {
                Category categoryFromDb = categoryRepository.findByTitle(categoryName);
                if (categoryFromDb == null) {
                    categoryFromDb = new Category();
                    categoryFromDb.setTitle(categoryName);
                }
                currentProduct.setCategory(categoryFromDb);
            }
            return productRepository.save(currentProduct);
        }

        throw new NotFoundException("Product with id:"+id+" doesn't exist");
    }

    @Override
    public List<Product> getAllProductByCategory(String title) throws NotFoundException {
        List<Product> response = productRepository.findByCategoryTitle(title);
        if(!response.isEmpty()){
            return response;
        }
        throw new NotFoundException("There is no product in "+title+" category");
    }
}
