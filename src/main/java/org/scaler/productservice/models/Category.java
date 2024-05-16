package org.scaler.productservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Category extends BaseModel {
    private String title;

    @OneToMany(mappedBy = "category", cascade = {CascadeType.REMOVE})
    //here we need to tell this(mappedBy) otherwise orm creates mapping table
    @JsonIgnore
    //when we hit the getSingleProductbyId then this will infinite call recursively to avoid this we need this annotation
    private List<Product> products;
}
