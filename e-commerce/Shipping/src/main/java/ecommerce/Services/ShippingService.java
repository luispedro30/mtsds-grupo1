package ecommerce.Services;

import ecommerce.Dto.OrderDto;
import ecommerce.Dto.PaymentDto;
import ecommerce.Dto.UserDto;
import ecommerce.Dto.WalletDto;
import ecommerce.Models.Shipping;
import ecommerce.Repository.ShippingRepository;
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
import java.util.Optional;

@Service
@Transactional
public class ShippingService {

    @Value("${endpoints.orders-microservice.baseUrl}")
    private String ordersUrl;

    @Value("${endpoints.users-microservice.baseUrl}")
    private String usersUrl;

    @Value("${endpoints.wallet-microservice.baseUrl}")
    private String walletUrl;

    @Value("${endpoints.payment-microservice.baseUrl}")
    private String paymentUrl;

    @Autowired
    private ShippingRepository shippingRepository;

    public List<Shipping> getAllShipping() {
        return shippingRepository.findAll();
    }

    public Shipping getShippingById(Integer id) {
        return shippingRepository
                .findById(id)
                .orElseThrow(null);
    }

    public Optional<Shipping> getShippingByOrderIdUserId(Integer orderId, Integer userId) {
        return shippingRepository.findByOrderIdAndUserId(orderId, userId);
    }

    public Shipping addShipping(Shipping shipping, HttpServletRequest request) throws Exception {

        UserDto userDto;
        String token = extractToken(request);
        userDto = getUser(shipping.getUserId(), request, token);

        OrderDto orderDto;
        orderDto = getOrder(shipping.getOrderId(), request, token);

        WalletDto walletDto;
        walletDto = getWallet(shipping.getUserId(), request, token);

        //PaymentDto paymentDto;
        //paymentDto = getPayment(shipping.getPaymentId());

        if(!doesNotExistAlready(shipping.getUserId(), shipping.getOrderId(), shipping.getPaymentId())){
            throw new Exception("This shipping already exists");
        }


        return shippingRepository.save(shipping);
    }

    public void deletePayment(Integer paymentId,
                              Integer userId,
                              HttpServletRequest request) throws Exception {
        UserDto userDto;
        String token = extractToken(request);
        userDto = getAdminFornecedor(userId, request, token);

        shippingRepository.deleteById(paymentId);
    }

    public void updatePayment(Integer paymentId,
                              Integer userId,
                              Shipping newShipping,
                              HttpServletRequest request) throws Exception {
        UserDto userDto;
        String token = extractToken(request);
        userDto = getAdminFornecedor(userId, request, token);

        shippingRepository.save(newShipping);
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

    public WalletDto getWallet(Integer userId, HttpServletRequest request, String token) throws Exception {

        WalletDto walletDto;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();


        try {
            /*ResponseEntity<WalletDto> response = restTemplate.getForEntity(
                    walletUrl + "/userId/{walletID}",
                    WalletDto.class,
                    userId
            );*/


            ResponseEntity<WalletDto> response = restTemplate.exchange(
                    walletUrl + "/userId/"+userId,
                    HttpMethod.GET,
                    httpEntity,
                    WalletDto.class);
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

    /*public PaymentDto getPayment(Integer paymentId) throws Exception {

        PaymentDto paymentDto;
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<PaymentDto> response = restTemplate.getForEntity(
                    paymentUrl + "/{paymentId}",
                    PaymentDto.class,
                    paymentId
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


    }*/

    public boolean doesNotExistAlready(Integer userId, Integer orderId, Integer paymentId) {
        return !shippingRepository.existsByUserIdAndOrderIdAndPaymentId(userId,orderId, paymentId);
    }


    public Shipping updateShipping(Integer id, Shipping updatedShipping) {
        Optional<Shipping> optionalShipping = shippingRepository.findById(id);
        if (optionalShipping.isPresent()) {
            Shipping existingShipping = optionalShipping.get();
            existingShipping.setStatus(updatedShipping.getStatus()); // Update other fields similarly

            return shippingRepository.save(existingShipping);
        } else {
            return null;
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
