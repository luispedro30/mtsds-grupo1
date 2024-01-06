package ecommerce.Services;

import ecommerce.Dto.ProductDto;
import ecommerce.Dto.UserDto;
import ecommerce.Messages.Order2Email;
import ecommerce.Models.Order;
import ecommerce.Models.Products;
import ecommerce.Models.User;
import ecommerce.Repository.OrderRepository;
import ecommerce.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    RabbitTemplate template;
    public List<Order> getAll(){
        return orderRepository.findAll();
    }

    @Transactional
    public Order getOrderById(Integer id){
        return orderRepository.findById(id).orElseThrow(null);
    }

    @Transactional
    public List<Order> findByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public Order getOrderWithProducts(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            Hibernate.initialize(order.getProducts());
            return constructOrderDTO(order);
        }
        return null; // Or throw an exception indicating order not found
    }

    private Order constructOrderDTO(Order order) {
        Order orderDTO = new Order();
        orderDTO.setOrderId(order.getOrderId());

        List<Products> productDTOs = order.getProducts().stream()
                .map(this::constructProductDTO)
                .collect(Collectors.toList());
        orderDTO.setProducts(productDTOs);

        return orderDTO;
    }

    private Products constructProductDTO(Products product) {
        Products productDTO = new Products();
        productDTO.setProductId(product.getProductId());
        // Map other fields...
        return productDTO;
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


    @Transactional
    public Order addOrder(Order order, HttpServletRequest request) throws Exception {
        //user must exists
        UserDto userDto;
        String token = extractToken(request);

        userDto = getUser(order.getUserId(),request, token);



        double totalPrice = 0;

        //fill the car foreign key
        if (order.getProducts() != null){
            //RestTemplate restTemplate = new RestTemplate();
            for (Products product: order.getProducts()){
                //RestTemplate restTemplate = new RestTemplate();
                ProductDto productDto = getProduct(product.getProductId());
                product.setOrder(order);
                product.setPrice(productDto.getPrice());
                totalPrice += productDto.getPrice();
                //restTemplate.postForEntity(productsUrl + "/decreaseStock/" + product.getProductId(),"",ProductDto.class);
            }
        }
        order.setPriceTotal(totalPrice);


        userRepository.save(new User(userDto.getUserId(),
                userDto.getName(),
                userDto.getActive(),
                userDto.getRole()));


        orderRepository.save(order);

        Order2Email order2Email = new Order2Email();
        order2Email.setOwnerRef("Ref: Orders");
        order2Email.setSubject("It was created a order in your name");
        order2Email.setEmailFrom("luispedrotrinta.1998@gmail.com");
        order2Email.setEmailTo(userDto.getEmail());
        order2Email.setText("Hello," +
                "" +
                "It was taken created a order in your name with the following id: "+ order.getOrderId()+"."+
                "" +
                "In order to proceed follow with the payment."+
                ""+
                "Best regards");
        template.convertAndSend("order-2-email-queue",order2Email);

        return order;
    }

    public UserDto getUser(Integer userId, HttpServletRequest request, String token) throws Exception {

        UserDto userDto;

        // Set token as a header in the outgoing request
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);


        RestTemplate restTemplate = new RestTemplate();

        try {
            /*ResponseEntity<UserDto> response = restTemplate.getForEntity(
                    usersUrl + "/{userId}",
                    UserDto.class,
                    userId
            );*/
            /*ResponseEntity<UserDto> response =
                    restTemplate.getForEntity(usersUrl + "/{userId}",
                            UserDto.class,
                            httpEntity,
                            userId);*/

            System.out.println(userId);
            System.out.println(usersUrl);
            System.out.println(usersUrl + "/"+userId);
            ResponseEntity<UserDto> response = restTemplate.exchange(usersUrl + "/"+userId,
                    HttpMethod.GET,
                    httpEntity,
                    UserDto.class);


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
