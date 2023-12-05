package ecommerce.Controllers;

import ecommerce.Models.Product;
import ecommerce.Services.ProductsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductsController {

    @Autowired
    private ProductsService productService;

    @PostMapping(value = "/products")
    private ResponseEntity<Product> addProduct(@RequestBody Product product, @RequestParam ("id") Integer idUser, HttpServletRequest request){

        if(product != null) {
            try {
                productService.addProduct(product, idUser);
                return new ResponseEntity<Product>(
                        product,
                        HttpStatus.CREATED);
            }catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<Product>(
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<Product>(
                HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/products/{id}")
    private ResponseEntity<Void> deleteProduct(@PathVariable("id") Integer id,@RequestParam ("id") Integer idUser){
        Product product = productService.getProductById(id);
        if(product != null) {
            try {
                productService.deleteProduct(id, idUser);
                return new ResponseEntity<Void>(
                        HttpStatus.OK);
            }catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<Void>(
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }


    @GetMapping(value = "/products")
    public ResponseEntity<List<Product>> getAllProducts(){
        List<Product> products =  productService.getAllProduct();
        if(!products.isEmpty()) {
            return new ResponseEntity<List<Product>>(
                    products,
                    HttpStatus.OK);
        }
        return new ResponseEntity<List<Product>>(
                HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/products", params = "category")
    public ResponseEntity<List<Product>> getAllProductByCategory(@RequestParam("category") String category){
        List<Product> products = productService.getAllProductByCategory(category);
        if(!products.isEmpty()) {
            return new ResponseEntity<List<Product>>(
                    products,
                    HttpStatus.OK);
        }
        return new ResponseEntity<List<Product>>(
                HttpStatus.NOT_FOUND);
    }

    @GetMapping (value = "/products/{id}")
    public ResponseEntity<Product> getOneProductById(@PathVariable("id") Integer id){
        Product product =  productService.getProductById(id);
        if(product != null) {
            return new ResponseEntity<Product>(
                    product,
                    HttpStatus.OK);
        }
        return new ResponseEntity<Product>(
                HttpStatus.NOT_FOUND);
    }

    @GetMapping (value = "/products", params = "name")
    public ResponseEntity<List<Product>> getAllProductsByName(@RequestParam ("name") String name){
        List<Product> products =  productService.getAllByName(name);
        if(!products.isEmpty()) {
            return new ResponseEntity<List<Product>>(
                    products,
                    HttpStatus.OK);
        }
        return new ResponseEntity<List<Product>>(
                HttpStatus.NOT_FOUND);
    }
}
