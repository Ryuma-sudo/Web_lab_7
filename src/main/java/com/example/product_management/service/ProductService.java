package com.example.product_management.service;

import com.example.product_management.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    List<Product> getAllProducts();
    List<Product> getAllProducts(Sort sort);
    Optional<Product> getProductById(Long id);
    Product saveProduct(Product product);
    void deleteProduct(Long id);
    
    // Updated Search Methods
    List<String> getAllCategories();
    
    // Quick Search with Pagination
    Page<Product> searchProductsPaginated(String keyword, Pageable pageable);
    
    // Advanced Search with Pagination
    Page<Product> advancedSearchPaginated(String name, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    Page<Product> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);

    // Statistics
    BigDecimal getTotalValue();
    BigDecimal getAveragePrice();
    List<Product> getLowStockProducts(int threshold);
    long getCountByCategory(String category);
    long getTotalCount();
    List<Product> getRecentProducts();
}