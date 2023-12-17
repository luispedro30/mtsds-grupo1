package ecommerce.Services;

import ecommerce.Dto.UserDto;
import ecommerce.Models.Wallet;
import ecommerce.Repository.WalletRepository;
import ecommerce.wallet;
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

@Service
@Transactional
public class WalletService {

    @Value("${endpoints.users-microservice.baseUrl}")
    private String usersUrl;

    @Autowired
    private WalletRepository walletRepository;

    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    public Wallet getWalletById(Integer id) {
        return walletRepository
                .findById(id)
                .orElseThrow(null);
    }

    public Wallet getWalletByUserId(Integer id) {
        return walletRepository.findByUserId(id);
    }

    public Wallet addWallet(Wallet wallet, HttpServletRequest request) throws Exception {

        UserDto userDto;
        String token = extractToken(request);

        userDto = getUser(wallet.getUserId(),request, token);

        if(getWalletByUserId(wallet.getUserId()) != null){
            throw new Exception("There is already a wallet for this User");
        }
        return walletRepository.save(wallet);
    }

    public Wallet addMoneyWallet(Integer walletId,
                                 float money,
                                 HttpServletRequest request) throws Exception {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(null);

        UserDto userDto;
        String token = extractToken(request);

        userDto = getUser(wallet.getUserId(),request, token);

        wallet.setValue(wallet.getValue()+money);

        return wallet;
    }

    public Wallet takeMoneyWallet(Integer walletId,
                                  float money,
                                  HttpServletRequest request) throws Exception {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(null);

        UserDto userDto;
        String token = extractToken(request);

        userDto = getUser(wallet.getUserId(),request, token);

        wallet.setValue(wallet.getValue()-money);

        return wallet;
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
}
