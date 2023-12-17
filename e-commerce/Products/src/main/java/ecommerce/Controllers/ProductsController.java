package ecommerce.Controllers;

import ecommerce.Enums.Category;
import ecommerce.Models.Product;
import ecommerce.Services.ProductsService;
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

@RestController
public class ProductsController {

    private static Marker marker = MarkerFactory.getMarker("products-service");

    private static Logger logger = LoggerFactory.getLogger(ProductsController.class);
    @Autowired
    private ProductsService productService;

    @GetMapping("/Landing")
    public ResponseEntity<String> landing(){
        logger.info(marker,"Checking that landing is working fine... 200 Ok");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Application is working fine.");
    }

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

    @PostMapping(value = "/products/decreaseStock/{id}")
    private ResponseEntity<Product> decreaseStockProduct(@PathVariable ("id") Integer idProduct, HttpServletRequest request) throws Exception {
        logger.info(marker,"decreaseStockProduct() request received ... pending");
        Product product = productService.decreaseStockProduct(idProduct);

        logger.info(marker,"decreaseStockProduct() request received ... 200 {}",product);
        return new ResponseEntity<Product>(
                product,
                HttpStatus.OK);

    }

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
