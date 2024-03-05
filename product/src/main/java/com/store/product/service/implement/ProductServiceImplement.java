package com.store.product.service.implement;

import com.store.product.domain.Product;
import com.store.product.repository.ProductRepository;
import com.store.product.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImplement extends GenericServiceImplement<Product, Long, ProductRepository> implements ProductService {
    public ProductServiceImplement(ProductRepository repository) {
        super(repository);
    }
}
