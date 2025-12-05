package com.example.product_management.service;

import com.example.product_management.entity.Product;
import com.example.product_management.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    // ... (Keep existing CRUD methods same as before) ...
    @Override
    public List<Product> getAllProducts() { return productRepository.findAll(); }
    @Override
    public List<Product> getAllProducts(Sort sort) { return productRepository.findAll(sort); }
    @Override
    public Optional<Product> getProductById(Long id) { return productRepository.findById(id); }
    @Override
    public Product saveProduct(Product product) { return productRepository.save(product); }
    @Override
    public void deleteProduct(Long id) { productRepository.deleteById(id); }
    @Override
    public List<String> getAllCategories() { return productRepository.findAllCategories(); }

    // --- NEW PAGINATED SEARCH IMPLEMENTATION ---

    @Override
    public Page<Product> searchProductsPaginated(String keyword, Pageable pageable) {
        return productRepository.findByNameContaining(keyword, pageable);
    }

    @Override
    public Page<Product> advancedSearchPaginated(String name, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.searchProducts(name, category, minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Product> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                    ? Sort.by(sortField).ascending() 
                    : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return productRepository.findAll(pageable);
    }

    // ... (Keep Statistics methods same as before) ...
    @Override
    public BigDecimal getTotalValue() { return productRepository.calculateTotalValue(); }
    @Override
    public BigDecimal getAveragePrice() { return productRepository.calculateAveragePrice(); }
    @Override
    public List<Product> getLowStockProducts(int threshold) { return productRepository.findLowStockProducts(threshold); }
    @Override
    public long getCountByCategory(String category) { return productRepository.countByCategory(category); }
    @Override
    public long getTotalCount() { return productRepository.count(); }
    @Override
    public List<Product> getRecentProducts() { return productRepository.findTop5ByOrderByCreatedAtDesc(); }
}