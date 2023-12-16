package ecommerce.Services;

import ecommerce.Config.MQConfig;
import ecommerce.Dto.OrderDto;
import ecommerce.Dto.ShippingDto;
import ecommerce.Dto.UserDto;
import ecommerce.Dto.WalletDto;
import ecommerce.Models.Payment;
import ecommerce.Repository.PaymentRepository;
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

    public Payment addPayment(Payment payment) throws Exception {

        UserDto userDto;
        userDto = getUser(payment.getUserId());

        OrderDto orderDto;
        orderDto = getOrder(payment.getOrderId());

        WalletDto walletDto;
        walletDto = getWallet(payment.getUserId());

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

        return payment;
    }

    public void deletePayment(Integer paymentId, Integer userId) throws Exception {
        UserDto userDto;
        userDto = getAdminFornecedor(userId);

        paymentRepository.deleteById(paymentId);
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

    public WalletDto getWallet(Integer userId) throws Exception {

        WalletDto walletDto;
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<WalletDto> response = restTemplate.getForEntity(
                    walletUrl + "/userId/{walletID}",
                    WalletDto.class,
                    userId
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

    public boolean doesNotExistAlready(Integer userId, Integer orderId ) {
        return !paymentRepository.existsByUserIdAndOrderId(userId,orderId);
    }
}
