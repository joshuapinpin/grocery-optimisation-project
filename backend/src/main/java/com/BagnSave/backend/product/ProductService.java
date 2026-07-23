package com.BagnSave.backend.product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.BagnSave.backend.product.dto.ProductDTO;

@Service
public class ProductService {

     private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(this::toDTO)
                .toList();
    }

    public ProductDTO toDTO(Product product) {
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getBrand(),
            product.getUnit(),
            product.getSize(),
            product.getRedirectedTo()
        );
    }
}
