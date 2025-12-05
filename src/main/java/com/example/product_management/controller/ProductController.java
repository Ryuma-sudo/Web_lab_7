package com.example.product_management.controller;

import com.example.product_management.entity.Product;
import com.example.product_management.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    // Helper to create Pageable
    private Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(page - 1, size, sort);
    }

    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<Product> pageResult;
        
        if (keyword != null && !keyword.isEmpty()) {
            // Case 2: Quick Search with Pagination
            pageResult = productService.searchProductsPaginated(keyword, createPageable(page, size, sortBy, sortDir));
            model.addAttribute("keyword", keyword);
        } else {
            // Case 1: Normal List
            pageResult = productService.findPaginated(page, size, sortBy, sortDir);
        }

        // Common Data
        model.addAttribute("products", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("categories", productService.getAllCategories());
        
        // IMPORTANT: Tell the view to submit pagination links to /products
        model.addAttribute("baseUrl", "/products");
        
        return "product-list";
    }

    @GetMapping("/advanced-search")
    public String advancedSearch(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        Model model) {
        
        // Handle empty strings
        if (name != null && name.trim().isEmpty()) name = null;
        if (category != null && category.trim().isEmpty()) category = null;
        
        // Case 3: Advanced Search with Pagination
        Page<Product> pageResult = productService.advancedSearchPaginated(
                name, category, minPrice, maxPrice, createPageable(page, size, sortBy, sortDir));
        
        model.addAttribute("products", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("categories", productService.getAllCategories());

        // Preserve search filters in the view
        model.addAttribute("searchName", name);
        model.addAttribute("searchCategory", category);
        model.addAttribute("searchMinPrice", minPrice);
        model.addAttribute("searchMaxPrice", maxPrice);
        
        // IMPORTANT: Tell the view to submit pagination links to /products/advanced-search
        model.addAttribute("baseUrl", "/products/advanced-search");
        
        return "product-list";
    }

    // ... (Keep new/save/delete methods exactly as they were) ...
    
    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-form";
    }

    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product, 
                              BindingResult result, RedirectAttributes ra, Model model) {
        if (result.hasErrors()) return "product-form";
        try {
            productService.saveProduct(product);
            ra.addFlashAttribute("message", "Saved successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return productService.getProductById(id).map(p -> {
            model.addAttribute("product", p);
            return "product-form";
        }).orElseGet(() -> {
            ra.addFlashAttribute("error", "Product not found");
            return "redirect:/products";
        });
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        productService.deleteProduct(id);
        ra.addFlashAttribute("message", "Product deleted!");
        return "redirect:/products";
    }
}