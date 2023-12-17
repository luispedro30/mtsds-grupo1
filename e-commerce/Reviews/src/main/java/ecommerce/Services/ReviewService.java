package ecommerce.Services;

import ecommerce.Dto.OrderDto;
import ecommerce.Dto.ShippingDto;
import ecommerce.Dto.UserDto;
import ecommerce.Enum.Status;
import ecommerce.Models.Review;
import ecommerce.Repository.ReviewRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;

@Service
@Transactional
public class ReviewService {
    @Value("${endpoints.orders-microservice.baseUrl}")
    private String ordersUrl;

    @Value("${endpoints.users-microservice.baseUrl}")
    private String usersUrl;

    @Value("${endpoints.shipping-microservice.baseUrl}")
    private String shippingUrl;

    @Autowired
    private ReviewRepository reviewRepository;
    private boolean n;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review getReviewById(Integer id) {
        return reviewRepository
                .findById(id)
                .orElseThrow(null);
    }

    public Review addReview(Review review, HttpServletRequest request) throws Exception {

        UserDto userDto;
        String token = extractToken(request);
        userDto = getUser(review.getUserId(), request, token);

        ShippingDto shippingDto;
        shippingDto = getShipping(review.getOrderId(), review.getUserId(), request, token);

        OrderDto orderDto;
        orderDto = getOrder(review.getOrderId(), request, token);

        if(!doesNotExistAlready(review.getUserId(), review.getProductId(), review.getOrderId())){
            throw new Exception("This reviews already exists");
        }

        List<LinkedHashMap<String, Object>> products = orderDto.getProducts();
        int productIdToCheck = review.getProductId();

        boolean allContainProductId = true;
        Integer countCheck = 0;
        for (LinkedHashMap<String, Object> product : products) {
            if (product.get("productId").equals(productIdToCheck)){
                countCheck += 1;
                break;
            }
        }
        System.out.println(countCheck);
        if (countCheck != 0) {
            System.out.println("All products contain the productId: " + productIdToCheck);
            Review save = reviewRepository.save(review);

            return review;
        } else {
            System.out.println("Not all products contain the productId: " + productIdToCheck);
            throw new Exception("The product must be in the order");

        }
    }


    public void deleteReview(Integer reviewId, Integer userId, HttpServletRequest request) throws Exception {
        UserDto userDto;
        userDto = getAdminFornecedor(userId, request, extractToken(request));

        reviewRepository.deleteById(reviewId);
    }

    public UserDto getAdminFornecedor(Integer userId, HttpServletRequest request, String token) throws Exception {

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
                throw new Exception("There must be a buyer user");
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



    public OrderDto getOrder(Integer orderId,
                             HttpServletRequest request,
                             String token) throws Exception {

        OrderDto orderDto;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            /*ResponseEntity<OrderDto> response = restTemplate.getForEntity(
                    ordersUrl + "/{orderId}",
                    OrderDto.class,
                    orderId
            );*/

            ResponseEntity<OrderDto> response = restTemplate.exchange(ordersUrl + "/"+orderId,
                    HttpMethod.GET,
                    httpEntity,
                    OrderDto.class);

            return response.getBody();
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


    public ShippingDto getShipping(Integer orderId,
                                   Integer userId,
                                   HttpServletRequest request,
                                   String token) throws Exception {

        ShippingDto shippingDto;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            /*ResponseEntity<ShippingDto> response = restTemplate.getForEntity(
                    shippingUrl + "/{orderId}/{userId}",
                    ShippingDto.class,
                    orderId, userId
            );*/

            ResponseEntity<ShippingDto> response = restTemplate.exchange(
                    shippingUrl + "/"+orderId + "/"+userId,
                    HttpMethod.GET,
                    httpEntity,
                    ShippingDto.class);

            if (response.getBody().getStatus() != Status.DELIVERED){
                throw new Exception("You cannot give review to this product");
            }
            return response.getBody();
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

    public boolean doesNotExistAlready(Integer userId, Integer productId, Integer orderId ) {
        return !reviewRepository.existsByUserIdAndProductIdAndOrderId(userId,productId,orderId);
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
