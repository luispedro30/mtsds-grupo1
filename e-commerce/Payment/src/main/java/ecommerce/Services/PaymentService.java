package ecommerce.Services;

import ecommerce.Dto.OrderDto;
import ecommerce.Dto.UserDto;
import ecommerce.Dto.WalletDto;
import ecommerce.Models.Payment;
import ecommerce.Repository.PaymentRepository;
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
public class PaymentService {

    @Value("${endpoints.orders-microservice.baseUrl}")
    private String ordersUrl;

    @Value("${endpoints.users-microservice.baseUrl}")
    private String usersUrl;

    @Value("${endpoints.wallet-microservice.baseUrl}")
    private String walletUrl;

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

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(walletUrl
                + "/takeMoney/"
                + walletDto.getId() + "?money=" + payment.getPriceTotal(),"",WalletDto.class);

        return paymentRepository.save(payment);
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
