package org.scaler.productservice.service;

import org.scaler.productservice.exceptions.NotFoundException;

public interface CategoryService {
    String[] getAllCategory() throws NotFoundException;
}
