package ecommerce.Services;

import ecommerce.Config.MQConfig;
import ecommerce.Dto.OrderDto;
import ecommerce.Dto.UserDto;
import ecommerce.Dto.WalletDto;
import ecommerce.Messages.Payment2Email;
import ecommerce.Models.Payment;
import ecommerce.Repository.PaymentRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
public class PaymentService {

    @Value("${endpoints.orders-microservice.baseUrl}")
    private String ordersUrl;

    @Value("${endpoints.users-microservice.baseUrl}")
    private String usersUrl;

    @Value("${endpoints.wallet-microservice.baseUrl}")
    private String walletUrl;

    @Value("${endpoints.shipping-microservice.baseUrl}")
    private String shippingUrl;

    @Autowired
    private RabbitTemplate template;
    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Integer id) {
        return paymentRepository
                .findById(id)
                .orElseThrow(null);
    }

    public Payment addPayment(Payment payment, HttpServletRequest request) throws Exception {

        UserDto userDto;
        String token = extractToken(request);
        userDto = getUser(payment.getUserId(), request, token);

        OrderDto orderDto;
        orderDto = getOrder(payment.getOrderId(), request, token);

        WalletDto walletDto;
        walletDto = getWallet(payment.getUserId(), request, token);

        payment.setPriceTotal(orderDto.getPriceTotal());

        if(!doesNotExistAlready(payment.getUserId(), payment.getOrderId())){
            throw new Exception("This payment already exists");
        }

        /*
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(walletUrl
                + "/takeMoney/"
                + walletDto.getId() + "?money=" + payment.getPriceTotal(),"",WalletDto.class);
        */

        paymentRepository.save(payment);

        template.convertAndSend(
                MQConfig.EXCHANGE,
                MQConfig.ROUTING_KEY_1,
                payment
        );

        RestTemplate restTemplate2 = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        /*
        String requestJson = "{\"paymentId\":"+payment.getPaymentId()
                +",\"orderId\":"+payment.getOrderId()
                +",\"userId\":"+payment.getUserId()
                +",\"status\":\"REGISTED\"}";


        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        System.out.println("aqui");

        try {
            ResponseEntity<ShippingDto> response = restTemplate2.postForEntity(
                    shippingUrl,
                    request,
                    ShippingDto.class
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e.getMessage());
        }
        */

        template.convertAndSend(
                MQConfig.EXCHANGE,
                MQConfig.ROUTING_KEY_2,
                payment
        );

        template.convertAndSend(
                MQConfig.EXCHANGE,
                MQConfig.ROUTING_KEY_3,
                payment
        );

        Payment2Email payment2Email = new Payment2Email();
        payment2Email.setOwnerRef("Ref: Payment");
        payment2Email.setSubject("Payment received with success");
        payment2Email.setEmailFrom("luispedrotrinta.1998@gmail.com");
        payment2Email.setEmailTo(userDto.getEmail());
        payment2Email.setText("Hello," +
                "" +
                "It was taken a payment with the id: "+ payment.getPaymentId()+
                " with the value of" + payment.getPriceTotal() + "euros with the userId "+
                payment.getUserId() + "and orderId "+payment.getOrderId()+
                "" +
                "Best regards");
        template.convertAndSend("payment-2-email-queue",payment2Email);


        return payment;
    }

    public void deletePayment(Integer paymentId, Integer userId, HttpServletRequest request) throws Exception {
        UserDto userDto;
        String token = extractToken(request);
        userDto = getAdminSupplier(userId, request, token);

        paymentRepository.deleteById(paymentId);
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
                throw new Exception("The user must be Supplier or Admin.");
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

    private String extractToken(HttpServletRequest request) {
        // Your logic to extract the token from the incoming request headers
        // For example:
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Extract the token without "Bearer " prefix
        }
        return null; // Handle token not found scenario
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

    public boolean doesNotExistAlready(Integer userId, Integer orderId ) {
        return !paymentRepository.existsByUserIdAndOrderId(userId,orderId);
    }
}
