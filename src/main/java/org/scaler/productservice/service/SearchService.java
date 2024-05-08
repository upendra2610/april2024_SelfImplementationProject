package org.scaler.productservice.service;

import org.scaler.productservice.models.Product;
import org.scaler.productservice.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class SearchService {
    private final ProductRepository productRepository;

    public SearchService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }
    public Page<Product> search(String query, int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        return productRepository.findByTitleContaining(query,pageable);

    }
}
