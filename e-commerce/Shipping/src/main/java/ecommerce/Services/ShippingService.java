package ecommerce.Services;

import ecommerce.Dto.OrderDto;
import ecommerce.Dto.PaymentDto;
import ecommerce.Dto.UserDto;
import ecommerce.Dto.WalletDto;
import ecommerce.Models.Shipping;
import ecommerce.Repository.ShippingRepository;
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

    public Shipping addShipping(Shipping shipping) throws Exception {

        UserDto userDto;
        userDto = getUser(shipping.getUserId());

        OrderDto orderDto;
        orderDto = getOrder(shipping.getOrderId());

        WalletDto walletDto;
        walletDto = getWallet(shipping.getUserId());

        //PaymentDto paymentDto;
        //paymentDto = getPayment(shipping.getPaymentId());

        if(!doesNotExistAlready(shipping.getUserId(), shipping.getOrderId(), shipping.getPaymentId())){
            throw new Exception("This shipping already exists");
        }


        return shippingRepository.save(shipping);
    }

    public void deletePayment(Integer paymentId, Integer userId) throws Exception {
        UserDto userDto;
        userDto = getAdminFornecedor(userId);

        shippingRepository.deleteById(paymentId);
    }

    public void updatePayment(Integer paymentId, Integer userId, Shipping newShipping) throws Exception {
        UserDto userDto;
        userDto = getAdminFornecedor(userId);

        shippingRepository.save(newShipping);
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
}
