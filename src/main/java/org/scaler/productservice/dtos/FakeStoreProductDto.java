package org.scaler.productservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.scaler.productservice.models.Category;
import org.scaler.productservice.models.Product;

import java.io.Serializable;

@Getter
@Setter
public class FakeStoreProductDto implements Serializable {
    private Long id;
    private String title;
    private Double price;
    private String description;
    private String image;
    private String category;

    public Product toProduct(){
        Product product = new Product();
        product.setId(getId());
        product.setImage(getImage());
        product.setDescription(getDescription());
        product.setPrice(getPrice());
        product.setTitle(getTitle());

        Category category1 = new Category();
        category1.setTitle(getCategory());
        product.setCategory(category1);

        return product;


    }
}
