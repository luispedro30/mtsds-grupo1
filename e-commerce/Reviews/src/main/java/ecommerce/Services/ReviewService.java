package ecommerce.Services;

import ecommerce.Dto.OrderDto;
import ecommerce.Dto.UserDto;
import ecommerce.Models.Review;
import ecommerce.Repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public Review addReview(Review review) throws Exception {

        UserDto userDto;
        userDto = getUser(review.getUserId());

        OrderDto orderDto;
        orderDto = getOrder(review.getOrderId());

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




    public void deleteReview(Integer reviewId, Integer userId) throws Exception {
        UserDto userDto;
        userDto = getAdminFornecedor(userId);

        reviewRepository.deleteById(reviewId);
    }

    public UserDto getAdminFornecedor(Integer userId) throws Exception {

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
                throw new Exception("Tem de ser Comprador");
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

    public OrderDto getOrder(Integer orderId) throws Exception {

        OrderDto orderDto;
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<OrderDto> response = restTemplate.getForEntity(
                    ordersUrl + "/{orderId}",
                    OrderDto.class,
                    orderId
            );
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

}
