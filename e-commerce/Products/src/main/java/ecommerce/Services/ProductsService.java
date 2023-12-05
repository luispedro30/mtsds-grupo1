package ecommerce.Services;

import ecommerce.Dto.UserDto;
import ecommerce.Models.Product;
import ecommerce.Repository.ProductsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        return productRepository.findAllByCategory(category);
    }


    public Product getProductById(Integer id) {
        return productRepository.getById(id);
    }

    public List<Product> getAllByName(String name) {
        return productRepository.findAllByName(name);
    }

    public Product addProduct(Product product, Integer userId) throws Exception {

        UserDto userDto;
        userDto = getUser(userId);
        return productRepository.save(product);
    }

    public void deleteProduct(Integer productId, Integer userId) throws Exception {
        UserDto userDto;
        userDto = getUser(userId);

        productRepository.deleteById(productId);
    }

    public UserDto getUser(Integer userId) throws Exception {

        UserDto userDto;
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<UserDto> response = restTemplate.getForEntity(
                    usersUrl + "/{userId}",
                    UserDto.class,
                    userId
            );
            System.out.println(response.getBody().getRole());
            if (response.getBody().getRole().equals("ADMIN")|| response.getBody().getRole().equals("FORNECEDOR")){
                return response.getBody();
            }
            else {
                throw new Exception("O user tem de ser fornecedor ou Administrador");
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
}
