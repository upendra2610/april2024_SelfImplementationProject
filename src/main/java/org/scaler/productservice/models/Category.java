package org.scaler.productservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Category extends BaseModel{
    private String title;

    @OneToMany(mappedBy = "category") //here we need to tell this otherwise orm creates mapping table
    private List<Product> products;
}
