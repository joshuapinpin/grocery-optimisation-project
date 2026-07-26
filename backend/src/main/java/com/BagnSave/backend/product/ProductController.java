package com.BagnSave.backend.product;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.BagnSave.backend.product.dto.ProductDTO;
import com.BagnSave.backend.shared.PaginationDefaults;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<ProductDTO> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + PaginationDefaults.DEFAULT_PAGE_SIZE) int size
    ) {
        int safeSize = Math.min(size, PaginationDefaults.MAX_PAGE_SIZE);
        return productService.getProducts(PageRequest.of(page, safeSize));
    }

}
