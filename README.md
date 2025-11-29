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
- [ ] Pagination
- [ ] REST API (Bonus)

## Project Structure
```
product-management/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/productmanagement/
│   │   │       ├── ProductManagementApplication.java (Main)
│   │   │       ├── entity/
│   │   │       │   └── Product.java
│   │   │       ├── repository/
│   │   │       │   └── ProductRepository.java
│   │   │       ├── service/
│   │   │       │   ├── ProductService.java
│   │   │       │   └── ProductServiceImpl.java
│   │   │       └── controller/
│   │   │           └── ProductController.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       │   └── css/
│   │       └── templates/
│   │           ├── product-list.html
│   │           └── product-form.html
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



