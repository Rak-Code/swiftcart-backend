package com.ecommerce.project.service;

import com.ecommerce.project.dto.ProductRequestDTO;
import com.ecommerce.project.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO dto);

    ProductResponseDTO createProductWithImages(ProductRequestDTO dto, MultipartFile[] images);

    ProductResponseDTO updateProduct(String id, ProductRequestDTO dto);

    ProductResponseDTO updateProductWithImages(String id, ProductRequestDTO dto, MultipartFile[] images, boolean keepExistingImages);

    void deleteProduct(String id);

    List<ProductResponseDTO> getProductsByCategory(String categoryId);

    List<ProductResponseDTO> searchProducts(String keyword);

    ProductResponseDTO getProduct(String id);

    List<ProductResponseDTO> getAllProducts();

    Page<ProductResponseDTO> getAllProducts(Pageable pageable);
}
