package com.BagnSave.backend.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.BagnSave.backend.product.dto.ProductDTO;

@Service
public class ProductService {

     private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<ProductDTO> getProducts(Pageable pageable) {

        return productRepository.findAll(pageable)
                .map(this::toDTO);
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
