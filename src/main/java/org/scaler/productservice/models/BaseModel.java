package org.scaler.productservice.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@MappedSuperclass
public class BaseModel {
    @GeneratedValue(generator = "idgenerator")
    @GenericGenerator(name = "idgenerator", strategy = "increment")
    @Id
    private Long id;
}
