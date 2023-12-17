package ecommerce.Controllers;

import ecommerce.Models.Wallet;
import ecommerce.Services.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping("/Landing")
    public ResponseEntity<String> landing(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Application is working fine.");
    }

    @GetMapping
    public ResponseEntity<List<Wallet>> getAllWallets(){
        List<Wallet> wallets =  walletService.getAllWallets();
        if(!wallets.isEmpty()) {
            return new ResponseEntity<List<Wallet>>(
                    wallets,
                    HttpStatus.OK);
        }
        return new ResponseEntity<List<Wallet>>(
                HttpStatus.NOT_FOUND);
    }

    @GetMapping (value = "/{id}")
    public ResponseEntity<Wallet> getWalletById(@PathVariable("id") Integer id){
        Wallet wallet =  walletService.getWalletById(id);
        if(wallet != null) {
            return new ResponseEntity<Wallet>(
                    wallet,
                    HttpStatus.OK);
        }
        return new ResponseEntity<Wallet>(
                HttpStatus.NOT_FOUND);
    }

    @GetMapping (value = "/userId/{id}")
    public ResponseEntity<Wallet> getWalletByUserId(@PathVariable("id") Integer id){
        Wallet wallet =  walletService.getWalletByUserId(id);
        if(wallet != null) {
            return new ResponseEntity<Wallet>(
                    wallet,
                    HttpStatus.OK);
        }
        return new ResponseEntity<Wallet>(
                HttpStatus.NOT_FOUND);
    }

    @PostMapping
    private ResponseEntity<Wallet> addWallet(@RequestBody Wallet wallet,
                                             HttpServletRequest request){

        if(wallet != null) {
            try {
                walletService.addWallet(wallet, request);
                return new ResponseEntity<Wallet>(
                        wallet,
                        HttpStatus.CREATED);
            }catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<Wallet>(
                        HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<Wallet>(
                HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/addMoney/{id}")
    private ResponseEntity<Wallet> addMoney(@PathVariable ("id") Integer idWallet,
                                            @RequestParam float money,
                                            HttpServletRequest request) throws Exception {
        Wallet wallet = walletService.addMoneyWallet(idWallet, money, request);

        return new ResponseEntity<Wallet>(
                wallet,
                HttpStatus.OK);

    }

    @PostMapping(value = "/takeMoney/{id}")
    private ResponseEntity<Wallet> takeMoney(@PathVariable ("id") Integer idWallet,
                                            @RequestParam float money,
                                            HttpServletRequest request) throws Exception {
        Wallet wallet = walletService.takeMoneyWallet(idWallet, money, request);

        return new ResponseEntity<Wallet>(
                wallet,
                HttpStatus.OK);

    }
}

