# Product Management System

## Student Information
- **Name:** Nguyễn Quang Trực
- **Student ID:** ITCSIU23041
- **Class:** Group 2

## Technologies Used
- Spring Boot 3.3.x
- Spring Data JPA
- MySQL 8.0
- Thymeleaf
- Maven

## Setup Instructions
1. Import project into VS Code
2. Create database: `product_management`
3. Update `application.properties` with your MySQL credentials
4. Run: `mvn spring-boot:run`
5. Open browser: http://localhost:8080/products

## Completed Features
- [x] CRUD operations
- [x] Search functionality
- [x] Advanced search with filters
- [x] Validation
- [x] Sorting
- [x] Pagination
- [x] Statistics Dashboard
- [ ] REST API (Bonus)

## Project Structure
```
product-management/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/product_management/
│   │   │       ├── ProductManagementApplication.java (Main)
│   │   │       ├── entity/
│   │   │       │   └── Product.java
│   │   │       ├── repository/
│   │   │       │   └── ProductRepository.java
│   │   │       ├── service/
│   │   │       │   ├── ProductService.java
│   │   │       │   └── ProductServiceImpl.java
│   │   │       └── controller/
│   │   │           ├── ProductController.java
│   │   │           └── DashboardController.java 
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       │   └── css/
│   │       └── templates/
│   │           ├── product-list.html
│   │           ├── product-form.html
│   │           └── dashboard.html        
│   │
│   └── test/
│       └── java/
│
├── pom.xml (Maven dependencies)
└── README.md
```
## Code Flow

### **1. List Functionality (Read Flow)**

**Objective:** Retrieve and display the complete list of products.

1.  **Request Initiation:** The flow begins when a `GET` request is made to the root path `/products`.

2.  **Controller Layer (`ProductController.java`):**
    The `listProducts` method intercepts the request. It calls the service layer to fetch data and adds it to the `Model` for rendering.

    ```java
    // Web_lab_7/src/main/java/com/example/product_management/controller/ProductController.java
    @GetMapping
    public String listProducts(Model model) {
        // Retrieve all products from the service layer
        List<Product> products = productService.getAllProducts(); 
        // Bind the list to the model with key "products"
        model.addAttribute("products", products);
        // Return the view name "product-list.html"
        return "product-list";
    }
    ```

3.  **Service Layer (`ProductServiceImpl.java`):**
    The `getAllProducts` method delegates the data retrieval to the repository.

    ```java
    // Web_lab_7/src/main/java/com/example/product_management/service/ProductServiceImpl.java
    @Override
    public List<Product> getAllProducts() {
        // Execute SELECT * FROM products via JPA
        return productRepository.findAll();
    }
    ```

4.  **View Layer (`product-list.html`):**
    Thymeleaf iterates over the `products` list to render the table rows.

    ```html
    <tr th:each="product : ${products}">
        <td th:text="${product.id}">...</td>
        </tr>
    ```

<img width="2141" height="744" alt="image" src="https://github.com/user-attachments/assets/dcc385f3-a621-4be3-805f-e113624e6134" />

-----

### **2. Create Functionality (Create Flow)**

**Objective:** Instantiate and persist a new `Product` entity.

**Phase 1: Form Display (GET)**

1.  **Controller:** The `showNewForm` method maps to `/products/new`. It creates an empty `Product` instance to bind form data.
    ```java
    // Web_lab_7/src/main/java/com/example/product_management/controller/ProductController.java
    @GetMapping("/new")
    public String showNewForm(Model model) {
        // Create empty entity for form binding
        Product product = new Product();
        model.addAttribute("product", product);
        return "product-form";
    }
    ```

**Phase 2: Data Submission (POST)**

1.  **Controller:** The `saveProduct` method handles the `POST` request to `/products/save`.
2.  **Service & Repository:**
    Since the `id` of the new product is `null`, `JpaRepository.save()` identifies this as an **INSERT** operation.
    ```java
    // Web_lab_7/src/main/java/com/example/product_management/service/ProductServiceImpl.java
    @Override
    public Product saveProduct(Product product) {
        // Persist the entity (INSERT if ID is null)
        return productRepository.save(product);
    }
    ```
    
<img width="1097" height="1367" alt="image" src="https://github.com/user-attachments/assets/3bf36124-54e8-4431-9b44-5fb58dd59872" />
<img width="1851" height="117" alt="image" src="https://github.com/user-attachments/assets/4bb5f2f5-ea83-4344-98b7-3be978cbf740" />

-----

### **3. Update Functionality (Update Flow)**

**Objective:** Modify an existing `Product` entity. This reuses logic from the Create flow but with a populated ID.

**Phase 1: Form Pre-filling (GET)**

1.  **Controller:** The `showEditForm` method accepts the `id` via the URL path `/products/edit/{id}`.
2.  **Service:** It attempts to find the existing product.
    ```java
    // Web_lab_7/src/main/java/com/example/product_management/controller/ProductController.java
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, ...) {
        return productService.getProductById(id)
                .map(product -> {
                    // Add existing product to model to pre-fill inputs
                    model.addAttribute("product", product);
                    return "product-form";
                })
                .orElseGet(() -> {
                    // Handle non-existent ID
                    return "redirect:/products";
                });
    }
    ```

**Phase 2: Data Submission (POST)**

1.  **View:** The form includes a hidden input for the ID.

    ```html
    <input type="hidden" th:field="*{id}" />
    ```

2.  **Controller & Service:**
    The form submits to the same `/products/save` endpoint. Because the `product` object now contains a non-null `id`, the `save()` method in the repository executes an **UPDATE** SQL statement instead of an INSERT.

<img width="1093" height="1352" alt="image" src="https://github.com/user-attachments/assets/3bdb1fd7-5aa0-4f4d-9df8-9f0f646c9293" />
<img width="2062" height="432" alt="image" src="https://github.com/user-attachments/assets/734bd577-79ce-4206-87a9-f058ef1ba9a4" />

-----

### **4. Delete Functionality (Delete Flow)**

**Objective:** Remove a specific product from the database.

1.  **Request Initiation:** The user clicks "Delete," triggering a `GET` request to `/products/delete/{id}`.

2.  **Controller:**
    The `deleteProduct` method extracts the `id` and delegates to the service.

    ```java
    // Web_lab_7/src/main/java/com/example/product_management/controller/ProductController.java
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Initiate delete operation
            productService.deleteProduct(id);
            // ... (flash message logic)
        } catch (Exception e) {
            // ... (error handling)
        }
        return "redirect:/products";
    }
    ```

3.  **Service Layer:**
    The service method calls the repository's void delete method.

    ```java
    // Web_lab_7/src/main/java/com/example/product_management/service/ProductServiceImpl.java
    @Override
    public void deleteProduct(Long id) {
        // Execute DELETE FROM products WHERE id = ?
        productRepository.deleteById(id);
    }
    ```

<img width="2058" height="142" alt="image" src="https://github.com/user-attachments/assets/dc35d4c4-c760-498c-b9f4-be2d5af77d4d" />
<img width="819" height="257" alt="image" src="https://github.com/user-attachments/assets/a38f98cf-da26-40f3-bb6f-ee894fa77541" />
<img width="2156" height="924" alt="image" src="https://github.com/user-attachments/assets/4943a328-4fb3-4167-aec0-0e6b05f39e40" />

----

### **5. Advanced Search Functionality (Search Flow)**

**Objective:** Filter products based on multiple criteria (Name, Category, Price Range) combined.

1.  **Request Initiation:** The user submits the filter form, sending a `GET` request to `/products/advanced-search` with query parameters.

2.  **Controller:**
    The `advancedSearch` method captures optional parameters and sanitizes empty strings to `null` to ensure the repository query logic works correctly.

    ```java
    // Web_lab_7/src/main/java/com/example/product_management/controller/ProductController.java
    @GetMapping("/advanced-search")
    public String advancedSearch(@RequestParam(required = false) String name, ... ) {
        // Convert empty strings to null for JPA query compatibility
        if (name != null && name.trim().isEmpty()) name = null;
        // ... (call service)
        List<Product> products = productService.advancedSearch(name, category, minPrice, maxPrice);
        model.addAttribute("products", products);
        return "product-list";
    }
    ```

3.  **Repository Layer (`ProductRepository.java`):**
    A custom JPQL `@Query` handles the multi-criteria logic using check-for-null idioms (`:param IS NULL OR field = :param`).

    ```java
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR p.name LIKE %:name%) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> searchProducts(@Param("name") String name, ...);
    ```
    
<img width="2119" height="767" alt="image" src="https://github.com/user-attachments/assets/18b24966-2a42-4fea-bbc2-18b1df9da7b0" />

-----

### **6. Validation Functionality**

**Objective:** Ensure data integrity by validating user input before saving to the database.

1.  **Entity Layer (`Product.java`):**
    Jakarta Validation annotations are applied to entity fields.
    ```java
    @NotBlank(message = "Product name is required")
    private String name;
    
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    ```

2.  **Controller:**
    The `saveProduct` method uses `@Valid` to trigger validation and `BindingResult` to capture errors.
    ```java
    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product, 
                              BindingResult result, ...) {
        // If validation fails, return to form with error details
        if (result.hasErrors()) {
            return "product-form";
        }
        // Proceed to save
    }
    ```

3.  **View Layer (`product-form.html`):**
    Thymeleaf displays error messages next to invalid fields.
    ```html
    <span th:if="${#fields.hasErrors('name')}" th:errors="*{name}" class="error-message">Error</span>
    ```
    
<img width="1205" height="1603" alt="image" src="https://github.com/user-attachments/assets/92d24a3f-9945-4de6-a435-de8a02e6b5cf" />

-----

### **7. Sorting Functionality**

**Objective:** Allow users to sort the product list by any column (ID, Name, Price, etc.) in Ascending or Descending order.

1.  **Request Initiation:** Clicking a table header sends a request with `sortBy` and `sortDir` parameters (e.g., `?sortBy=price&sortDir=desc`).

2.  **Controller:**
    The `listProducts` method converts these strings into a Spring Data `Sort` object.
    ```java
    // Web_lab_7/src/main/java/com/example/product_management/controller/ProductController.java
    Sort sort = sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    // Pass sort object to service
    List<Product> products = productService.getAllProducts(sort);
    ```

3.  **Service Layer:**
    Passes the `Sort` object directly to the repository.
    ```java
    @Override
    public List<Product> getAllProducts(Sort sort) {
        return productRepository.findAll(sort);
    }
    ```
    
<img width="2049" height="698" alt="image" src="https://github.com/user-attachments/assets/da5861d1-47f8-470e-be08-fcf6fc1da30a" />

-----

### **8. Statistics Dashboard Functionality**

**Objective:** Display high-level metrics (Total Value, Average Price) and alerts (Low Stock).

1.  **Request Initiation:** A `GET` request is made to `/dashboard`.

2.  **Controller (`DashboardController.java`):**
    Calls specialized service methods to aggregate data.
    ```java
    @GetMapping
    public String showDashboard(Model model) {
        model.addAttribute("totalValue", productService.getTotalValue());
        model.addAttribute("averagePrice", productService.getAveragePrice());
        model.addAttribute("lowStockProducts", productService.getLowStockProducts(10));
        return "dashboard";
    }
    ```

3.  **Repository Layer:**
    Uses JPQL Aggregate functions (`SUM`, `AVG`, `COUNT`).
    ```java
    @Query("SELECT COALESCE(SUM(p.price * p.quantity), 0) FROM Product p")
    BigDecimal calculateTotalValue();

    @Query("SELECT p FROM Product p WHERE p.quantity < :threshold")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);
    
<img width="2246" height="1648" alt="image" src="https://github.com/user-attachments/assets/25d0f3f4-1349-468a-930c-4ecc1637d82b" />

