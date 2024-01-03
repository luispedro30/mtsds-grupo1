package ecommerce.Services;

import ecommerce.Dto.UserDto;
import ecommerce.Enums.Category;
import ecommerce.Models.Product;
import ecommerce.Repository.ProductsRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Transactional
public class ProductsService   {

    @Value("${endpoints.users-microservice.baseUrl}")
    private String usersUrl;

    @Autowired
    private ProductsRepository productRepository;

    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    public List<Product> getAllProductByCategory(String category) {
        Category categoryFinal = Category.valueOf(category);
        return productRepository.findAllByCategory(categoryFinal);
    }


    public Product getProductById(Integer id) {
        return productRepository
                .findById(id)
                .orElseThrow(null);
    }

    public List<Product> getAllByName(String name) {
        return productRepository.findAllByName(name);
    }

    public Product addProduct(Product product,
                              Integer userId,
                              HttpServletRequest request) throws Exception {

        UserDto userDto;
        userDto = getAdminSupplier(userId, request, extractToken(request));
        return productRepository.save(product);
    }

    public Product decreaseStockProduct(Integer productId) throws Exception {
        Product product = productRepository.findById(productId).orElseThrow(null);
        if (product.getStockQuantity() >= 0){
            product.setStockQuantity(product.getStockQuantity()-1);
        }
        return product;
    }

    public void deleteProduct(Integer productId,
                              Integer userId,
                              HttpServletRequest request) throws Exception {
        UserDto userDto;
        userDto = getAdminSupplier(userId, request, extractToken(request));

        productRepository.deleteById(productId);
    }

    public UserDto getAdminSupplier(Integer userId, HttpServletRequest request, String token) throws Exception {

        UserDto userDto;

        // Set token as a header in the outgoing request
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);


        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<UserDto> response = restTemplate.exchange(usersUrl + "/"+userId,
                    HttpMethod.GET,
                    httpEntity,
                    UserDto.class);


            System.out.println(response.getBody().getRole());
            if (response.getBody().getRole().equals("ADMIN")|| response.getBody().getRole().equals("SUPPLIER")){
                return response.getBody();
            }
            else {
                throw new Exception("The user must be Supplier or Admin");
            }

        }
        catch (HttpClientErrorException | HttpServerErrorException e){
            if(e.getStatusCode() == HttpStatus.BAD_REQUEST){
                throw new Exception(e.getResponseBodyAsString());
            }
            if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                throw new Exception(e.getResponseBodyAsString());
            }
            else{
                throw new Exception(e.getResponseBodyAsString());
            }
        }
    }

    private String extractToken(HttpServletRequest request) {
        // Your logic to extract the token from the incoming request headers
        // For example:
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Extract the token without "Bearer " prefix
        }
        return null; // Handle token not found scenario
    }
}
