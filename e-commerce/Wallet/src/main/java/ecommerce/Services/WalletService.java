package ecommerce.Services;

import ecommerce.Dto.UserDto;
import ecommerce.Models.Wallet;
import ecommerce.Repository.WalletRepository;
import ecommerce.wallet;
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

    public Wallet addWallet(Wallet wallet) throws Exception {

        UserDto userDto;
        userDto = getUser(wallet.getUserId());

        if(getWalletByUserId(wallet.getUserId()) != null){
            throw new Exception("There is already a wallet for this User");
        }
        return walletRepository.save(wallet);
    }

    public Wallet addMoneyWallet(Integer walletId, float money) throws Exception {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(null);

        UserDto userDto;
        userDto = getUser(wallet.getUserId());

        wallet.setValue(wallet.getValue()+money);

        return wallet;
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
}
