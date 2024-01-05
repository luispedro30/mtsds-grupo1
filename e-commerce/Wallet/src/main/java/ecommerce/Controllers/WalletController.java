package ecommerce.Controllers;

import ecommerce.Models.Wallet;
import ecommerce.Services.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Operation(summary = "Check if the application is working")
    @ApiResponse(responseCode = "200", description = "Application status retrieved")
    @GetMapping("/Landing")
    public ResponseEntity<String> landing() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Application is working fine.");
    }

    @Operation(summary = "Get all wallets")
    @ApiResponse(responseCode = "200", description = "List of wallets",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Wallet.class)))
    @ApiResponse(responseCode = "404", description = "No wallets found")
    @GetMapping
    public ResponseEntity<List<Wallet>> getAllWallets() {
        List<Wallet> wallets = walletService.getAllWallets();
        if (!wallets.isEmpty()) {
            return new ResponseEntity<List<Wallet>>(
                    wallets,
                    HttpStatus.OK);
        }
        return new ResponseEntity<List<Wallet>>(
                HttpStatus.NOT_FOUND);
    }

    // Similarly, add annotations to other methods...

    @Operation(summary = "Add a new wallet")
    @ApiResponse(responseCode = "201", description = "Wallet created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Wallet.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @PostMapping
    private ResponseEntity<Wallet> addWallet(
            @RequestBody Wallet wallet,
            HttpServletRequest request) {

        if (wallet != null) {
            try {
                walletService.addWallet(wallet, request);
                return new ResponseEntity<Wallet>(
                        wallet,
                        HttpStatus.CREATED);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<Wallet>(
                        HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<Wallet>(
                HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Add money to a wallet")
    @ApiResponse(responseCode = "200", description = "Money added to wallet",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Wallet.class)))
    @PostMapping(value = "/addMoney/{id}")
    private ResponseEntity<Wallet> addMoney(
            @PathVariable("id") Integer idWallet,
            @RequestParam float money,
            HttpServletRequest request) throws Exception {

        Wallet wallet = walletService.addMoneyWallet(idWallet, money, request);

        return new ResponseEntity<Wallet>(
                wallet,
                HttpStatus.OK);
    }

    @Operation(summary = "Take money from a wallet")
    @ApiResponse(responseCode = "200", description = "Money taken from wallet",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Wallet.class)))
    @PostMapping(value = "/takeMoney/{id}")
    private ResponseEntity<Wallet> takeMoney(
            @PathVariable("id") Integer idWallet,
            @RequestParam float money,
            HttpServletRequest request) throws Exception {

        Wallet wallet = walletService.takeMoneyWallet(idWallet, money, request);

        return new ResponseEntity<Wallet>(
                wallet,
                HttpStatus.OK);
    }

    @Operation(summary = "Get wallet by user ID")
    @ApiResponse(responseCode = "200", description = "Wallet found for the user",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Wallet.class)))
    @ApiResponse(responseCode = "404", description = "No wallet found for the user")
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
}
