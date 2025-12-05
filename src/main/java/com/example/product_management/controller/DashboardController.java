package com.example.product_management.controller;

import com.example.product_management.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    private final ProductService productService;

    @Autowired
    public DashboardController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public String showDashboard(Model model) {
        // Statistics
        model.addAttribute("totalCount", productService.getTotalCount());
        model.addAttribute("totalValue", productService.getTotalValue());
        model.addAttribute("averagePrice", productService.getAveragePrice());
        
        // Lists
        model.addAttribute("lowStockProducts", productService.getLowStockProducts(10));
        model.addAttribute("recentProducts", productService.getRecentProducts());
        model.addAttribute("categories", productService.getAllCategories());
        
        return "dashboard";
    }
}