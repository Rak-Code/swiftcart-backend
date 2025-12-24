package com.ecommerce.project.service;

import com.ecommerce.project.dto.ProductRequestDTO;
import com.ecommerce.project.dto.ProductResponseDTO;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// Redis caching disabled - imports commented out
// import org.springframework.cache.annotation.CacheEvict;
// import org.springframework.cache.annotation.Cacheable;
// import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ImageStorageService imageStorageService;

    /**
     * Create product (Redis caching disabled)
     */
    @Override
    // @Caching(evict = {
    //         @CacheEvict(value = "productsAll", allEntries = true),
    //         @CacheEvict(value = "productsPage", allEntries = true),
    //         @CacheEvict(value = "productsByCategory", allEntries = true),
    //         @CacheEvict(value = "productsBySearch", allEntries = true),
    //         @CacheEvict(value = "product", allEntries = true)
    // })
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setCategoryId(dto.categoryId());
        product.setPrice(dto.price());
        product.setStockQuantity(dto.stockQuantity());
        product.setImageUrls(dto.imageUrls());
        product.setSize(dto.size());
        product.setColor(dto.color());

        Product saved = productRepository.save(product);
        log.info("Created product with ID: {}", saved.getId());

        return toDTO(saved);
    }

    /**
     * Create product with images (Redis caching disabled)
     */
    @Override
    // @Caching(evict = {
    //         @CacheEvict(value = "productsAll", allEntries = true),
    //         @CacheEvict(value = "productsPage", allEntries = true),
    //         @CacheEvict(value = "productsByCategory", allEntries = true),
    //         @CacheEvict(value = "productsBySearch", allEntries = true),
    //         @CacheEvict(value = "product", allEntries = true)
    // })
    public ProductResponseDTO createProductWithImages(ProductRequestDTO dto, MultipartFile[] images) {
        List<String> imageUrls = new ArrayList<>();
        
        // Upload images if provided
        if (images != null && images.length > 0) {
            try {
                List<MultipartFile> imageList = Arrays.asList(images);
                imageUrls = imageStorageService.uploadImages(imageList, "products");
                log.info("Uploaded {} images for new product", imageUrls.size());
            } catch (Exception e) {
                log.error("Failed to upload images: {}", e.getMessage());
                throw new RuntimeException("Failed to upload product images: " + e.getMessage());
            }
        }
        
        // Create product with uploaded image URLs
        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setCategoryId(dto.categoryId());
        product.setPrice(dto.price());
        product.setStockQuantity(dto.stockQuantity());
        product.setImageUrls(imageUrls);
        product.setSize(dto.size());
        product.setColor(dto.color());

        Product saved = productRepository.save(product);
        log.info("Created product with ID: {} and {} images", saved.getId(), imageUrls.size());

        return toDTO(saved);
    }

    /**
     * Update product (Redis caching disabled)
     */
    @Override
    // @Caching(evict = {
    //         @CacheEvict(value = "product", key = "#id"),
    //         @CacheEvict(value = "productsAll", allEntries = true),
    //         @CacheEvict(value = "productsPage", allEntries = true),
    //         @CacheEvict(value = "productsByCategory", allEntries = true),
    //         @CacheEvict(value = "productsBySearch", allEntries = true)
    // })
    public ProductResponseDTO updateProduct(String id, ProductRequestDTO dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setCategoryId(dto.categoryId());
        product.setPrice(dto.price());
        product.setStockQuantity(dto.stockQuantity());
        product.setImageUrls(dto.imageUrls());
        product.setSize(dto.size());
        product.setColor(dto.color());

        Product updated = productRepository.save(product);
        log.info("Updated product {}", id);

        return toDTO(updated);
    }

    /**
     * Update product with images (Redis caching disabled)
     */
    @Override
    // @Caching(evict = {
    //         @CacheEvict(value = "product", key = "#id"),
    //         @CacheEvict(value = "productsAll", allEntries = true),
    //         @CacheEvict(value = "productsPage", allEntries = true),
    //         @CacheEvict(value = "productsByCategory", allEntries = true),
    //         @CacheEvict(value = "productsBySearch", allEntries = true)
    // })
    public ProductResponseDTO updateProductWithImages(String id, ProductRequestDTO dto, MultipartFile[] images, boolean keepExistingImages) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        List<String> imageUrls = new ArrayList<>();
        
        // Keep existing images if requested
        if (keepExistingImages && product.getImageUrls() != null) {
            imageUrls.addAll(product.getImageUrls());
        } else if (!keepExistingImages && product.getImageUrls() != null) {
            // Delete old images from R2
            try {
                imageStorageService.deleteImages(product.getImageUrls());
                log.info("Deleted {} old images for product {}", product.getImageUrls().size(), id);
            } catch (Exception e) {
                log.warn("Failed to delete old images: {}", e.getMessage());
            }
        }
        
        // Upload new images if provided
        if (images != null && images.length > 0) {
            try {
                List<MultipartFile> imageList = Arrays.asList(images);
                List<String> newImageUrls = imageStorageService.uploadImages(imageList, "products");
                imageUrls.addAll(newImageUrls);
                log.info("Uploaded {} new images for product {}", newImageUrls.size(), id);
            } catch (Exception e) {
                log.error("Failed to upload new images: {}", e.getMessage());
                throw new RuntimeException("Failed to upload product images: " + e.getMessage());
            }
        }

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setCategoryId(dto.categoryId());
        product.setPrice(dto.price());
        product.setStockQuantity(dto.stockQuantity());
        product.setImageUrls(imageUrls);
        product.setSize(dto.size());
        product.setColor(dto.color());

        Product updated = productRepository.save(product);
        log.info("Updated product {} with {} total images", id, imageUrls.size());

        return toDTO(updated);
    }

    /**
     * Delete product (Redis caching disabled)
     */
    @Override
    // @Caching(evict = {
    //         @CacheEvict(value = "product", key = "#id"),
    //         @CacheEvict(value = "productsAll", allEntries = true),
    //         @CacheEvict(value = "productsPage", allEntries = true),
    //         @CacheEvict(value = "productsByCategory", allEntries = true),
    //         @CacheEvict(value = "productsBySearch", allEntries = true)
    // })
    public void deleteProduct(String id) {
        // Get product to delete its images
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        // Delete images from R2 storage
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            try {
                imageStorageService.deleteImages(product.getImageUrls());
                log.info("Deleted {} images for product {}", product.getImageUrls().size(), id);
            } catch (Exception e) {
                log.warn("Failed to delete images for product {}: {}", id, e.getMessage());
            }
        }
        
        productRepository.deleteById(id);
        log.info("Deleted product {}", id);
    }

    /**
     * Get products by category (Redis caching disabled)
     */
    @Override
    // @Cacheable(cacheNames = "productsByCategory", key = "#categoryId")
    public List<ProductResponseDTO> getProductsByCategory(String categoryId) {
        log.info("Fetching products for category: {}", categoryId);
        return productRepository.findByCategoryId(categoryId)
                .stream().map(this::toDTO).toList();
    }

    /**
     * Search products by keyword (Redis caching disabled)
     */
    @Override
    // @Cacheable(cacheNames = "productsBySearch", key = "#keyword")
    public List<ProductResponseDTO> searchProducts(String keyword) {
        log.info("Searching products with keyword: {}", keyword);
        return productRepository.findByNameContainingIgnoreCase(keyword)
                .stream().map(this::toDTO).toList();
    }

    /**
     * Get single product by id (Redis caching disabled)
     */
    @Override
    // @Cacheable(cacheNames = "product", key = "#id")
    public ProductResponseDTO getProduct(String id) {
        log.info("Fetching product with ID: {}", id);
        return productRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    /**
     * Get all products (Redis caching disabled)
     */
    @Override
    // @Cacheable(cacheNames = "productsAll")
    public List<ProductResponseDTO> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Get all products with pagination (Redis caching disabled)
     */
    @Override
    // @Cacheable(cacheNames = "productsPage", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + (#pageable.sort != null ? #pageable.sort.toString() : '')")
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        log.info("Fetching products with pagination - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(this::toDTO);
    }

    private ProductResponseDTO toDTO(Product p) {
        return new ProductResponseDTO(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStockQuantity(),
                p.getCategoryId(),
                p.getColor(),
                p.getSize(),
                p.getImageUrls()
        );
    }
}
