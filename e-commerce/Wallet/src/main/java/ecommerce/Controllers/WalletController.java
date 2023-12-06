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

    @PostMapping
    private ResponseEntity<Wallet> addWallet(@RequestBody Wallet wallet,
                                             HttpServletRequest request){

        if(wallet != null) {
            try {
                walletService.addWallet(wallet);
                return new ResponseEntity<Wallet>(
                        wallet,
                        HttpStatus.CREATED);
            }catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<Wallet>(
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<Wallet>(
                HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/addMoney/{id}")
    private ResponseEntity<Wallet> addMoney(@PathVariable ("id") Integer idWallet,
                                            @RequestParam float money,
                                            HttpServletRequest request) throws Exception {
        Wallet wallet = walletService.addMoneyWallet(idWallet, money);

        return new ResponseEntity<Wallet>(
                wallet,
                HttpStatus.OK);

    }
}

