package ecommerce.Controllers;

import ecommerce.Enums.Category;
import ecommerce.Models.Product;
import ecommerce.Services.ProductsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class ProductsController {

    private static Marker marker = MarkerFactory.getMarker("products-service");

    private static Logger logger = LoggerFactory.getLogger(ProductsController.class);
    @Autowired
    private ProductsService productService;

    @Operation(summary = "Check if the application is working")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application status retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/Landing")
    public ResponseEntity<String> landing(){
        logger.info(marker,"Checking that landing is working fine... 200 Ok");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Application is working fine.");
    }

    // Add Product
    @Operation(summary = "Add a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product added successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/products")
    private ResponseEntity<Product> addProduct(@RequestBody Product product,
                                               @RequestParam ("id") Integer idUser,
                                               HttpServletRequest request){
        logger.info(marker,"addProduct() request received ... pending");
        if(product != null) {
            try {
                productService.addProduct(product, idUser, request);
                logger.info(marker,"addProduct() request received ... 201 Created{}",product);
                return new ResponseEntity<Product>(
                        product,
                        HttpStatus.CREATED);
            }catch (Exception e) {
                logger.error(marker,e.getMessage());
                e.printStackTrace();
                return new ResponseEntity<Product>(
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        logger.info(marker,"addProduct() request received ... Bad Request{}");
        return new ResponseEntity<Product>(
                HttpStatus.BAD_REQUEST);
    }


    @Operation(summary = "Update an existing product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProductById(@PathVariable Integer id,
                                                     @RequestBody Product updatedProduct,
                                                     @RequestParam ("id") Integer idUser,
                                                     HttpServletRequest request) {
        try {
            Product updated = productService.updateProductById(id, updatedProduct, idUser, request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Decrease stock for a product by ID.
     *
     * @param idProduct ID of the product to decrease stock
     * @param request   HTTP request
     * @return ResponseEntity with the updated product or appropriate error status
     * @throws Exception if there's an issue with decreasing the stock
     */
    @Operation(summary = "Decrease stock for a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock decreased successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/products/decreaseStock/{id}")
    private ResponseEntity<Product> decreaseStockProduct(@PathVariable ("id") Integer idProduct, HttpServletRequest request) throws Exception {
        logger.info(marker,"decreaseStockProduct() request received ... pending");
        Product product = productService.decreaseStockProduct(idProduct);

        logger.info(marker,"decreaseStockProduct() request received ... 200 {}",product);
        return new ResponseEntity<Product>(
                product,
                HttpStatus.OK);

    }

    @Operation(summary = "Delete a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/products/{id}")
    private ResponseEntity<Void> deleteProduct(@PathVariable("id") Integer id,
                                               @RequestParam ("id") Integer idUser,
                                               HttpServletRequest request){
        Product product = productService.getProductById(id);
        logger.info(marker,"deleteProduct() request received ... pending");
        if(product != null) {
            try {
                productService.deleteProduct(id, idUser, request);
                logger.info(marker,"deleteProduct() request received ... 200 {}",product);
                return new ResponseEntity<Void>(
                        HttpStatus.OK);
            }catch (Exception e) {
                e.printStackTrace();
                logger.error(marker,e.getMessage());
                return new ResponseEntity<Void>(
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        logger.info(marker,"deleteProduct() request received ... Bad Request{}");
        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Retrieve all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products retrieved"),
            @ApiResponse(responseCode = "404", description = "No products found")
    })
    @GetMapping(value = "/products")
    public ResponseEntity<List<Product>> getAllProducts(){
        List<Product> products =  productService.getAllProduct();
        logger.info(marker,"getAllProduct() request received ... pending");
        if(!products.isEmpty()) {
            logger.info(marker,"getAllProduct() request received ... 200 Ok{}",products);
            return new ResponseEntity<List<Product>>(
                    products,
                    HttpStatus.OK);
        }
        logger.info(marker,"getAllProduct() request received ... 404 Not Found{}",products);
        return new ResponseEntity<List<Product>>(
                HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Retrieve products by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No products found for the given category")
    })
    @GetMapping(value = "/products/products-by-category/{category}")
    public ResponseEntity<List<Product>> getAllProductByCategory(@PathVariable("category") String category){
        List<Product> products = productService.getAllProductByCategory(category);
        logger.info(marker,"getAllProductByCategory() request received ... pending");
        if(!products.isEmpty()) {
            logger.info(marker,"getAllProductByCategory() request received ... 200 {}",products);
            return new ResponseEntity<List<Product>>(
                    products,
                    HttpStatus.OK);
        }
        logger.info(marker,"getAllProductByCategory() request received ... 404 Not Found{}",products);
        return new ResponseEntity<List<Product>>(
                HttpStatus.NOT_FOUND);
    }


    @Operation(summary = "Retrieve a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping(value = "/products/{id}")
    public ResponseEntity<Product> getOneProductById(@PathVariable("id") Integer id){
        Product product =  productService.getProductById(id);
        logger.info(marker,"getOneProductById() request received ... pending");
        if(product != null) {
            logger.info(marker,"getOneProductById() request received ... 200 {}",product);
            return new ResponseEntity<Product>(
                    product,
                    HttpStatus.OK);
        }
        logger.info(marker,"getOneProductById() request received ... 404 Not Found{}");
        return new ResponseEntity<Product>(
                HttpStatus.NOT_FOUND);
    }


    @Operation(summary = "Retrieve products by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No products found with the given name")
    })
    @GetMapping(value = "/products/products-by-name/{name}")
    public ResponseEntity<List<Product>> getAllProductsByName(@PathVariable ("name") String name){
        List<Product> products =  productService.getAllByName(name);
        logger.info(marker,"getAllProductsByName() request received ... pending");
        if(!products.isEmpty()) {
            logger.info(marker,"getAllProductsByName() request received ... 200 {}",products);
            return new ResponseEntity<List<Product>>(
                    products,
                    HttpStatus.OK);
        }
        logger.info(marker,"getAllProductsByName() request received ... 404 Not Found{}",products);
        return new ResponseEntity<List<Product>>(
                HttpStatus.NOT_FOUND);
    }
}
