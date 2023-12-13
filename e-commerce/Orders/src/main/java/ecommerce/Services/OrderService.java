package ecommerce.Services;

import ecommerce.Dto.ProductDto;
import ecommerce.Dto.UserDto;
import ecommerce.Exceptions.ItemDoesNotExistException;
import ecommerce.Models.Order;
import ecommerce.Models.Products;
import ecommerce.Models.User;
import ecommerce.Repository.OrderRepository;
import ecommerce.Repository.UserRepository;
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
public class OrderService {


    @Value("${endpoints.users-microservice.baseUrl}")
    private String usersUrl;

    @Value("${endpoints.products-microservice.baseUrl}")
    private String productsUrl;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    public List<Order> getAll(){
        return orderRepository.findAll();
    }

    public Order getOrderById(Integer id){
        return orderRepository.findById(id).orElseThrow(null);
    }

    @Transactional
    public Order addOrder(Order order) throws Exception {
        //user must exist
        UserDto userDto;
        userDto = getUser(order.getUserId());


        double totalPrice = 0;

        //fill the car foreign key
        if (order.getProducts() != null){
            RestTemplate restTemplate = new RestTemplate();
            for (Products product: order.getProducts()){
                //RestTemplate restTemplate = new RestTemplate();
                ProductDto productDto = getProduct(product.getProductId());
                product.setOrder(order);
                product.setPrice(productDto.getPrice());
                totalPrice += productDto.getPrice();
                restTemplate.postForEntity(productsUrl + "/decreaseStock/" + product.getProductId(),"",ProductDto.class);
            }
        }
        order.setPriceTotal(totalPrice);


        userRepository.save(new User(userDto.getUserId(),
                userDto.getName(),
                userDto.getActive(),
                userDto.getRole()));
        return orderRepository.save(order);
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
            if (response.getBody().getRole().equals("USER")){
                return response.getBody();
            }
            else {
                throw new Exception("O user tem de ser comprador");
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

    public ProductDto getProduct(Integer productId) throws Exception {

        UserDto userDto;
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<ProductDto> response = restTemplate.getForEntity(
                    productsUrl + "/{productId}",
                    ProductDto.class,
                    productId
            );

            if(response.getBody().getStockQuantity() <= 0){
                throw new Exception("The product does not have stock enough!");
            }

            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new Exception(e.getResponseBodyAsString());
            }
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new Exception(e.getResponseBodyAsString());
            } else {
                throw new Exception(e.getResponseBodyAsString());
            }
        }
    }
}
