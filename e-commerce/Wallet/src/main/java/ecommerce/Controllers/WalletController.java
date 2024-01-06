package ecommerce.Controllers;

import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Exceptions.WalletNotFoundException;
import ecommerce.Exceptions.WalletValueException;
import ecommerce.Models.Wallet;
import ecommerce.Services.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
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

    private static Marker marker = MarkerFactory.getMarker("wallet-service");

    private static Logger logger = LoggerFactory.getLogger(WalletController.class);

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
            HttpServletRequest request) throws AlreadyExistingException {

        if (wallet != null) {
            try {
                Wallet existingWallet = walletService.getWalletByUserId(wallet.getUserId());

                if (existingWallet != null) {
                    throw new AlreadyExistingException(wallet.getUserId().toString(), "addWallet()");
                } else {
                    walletService.addWallet(wallet, request);
                    return new ResponseEntity<Wallet>(
                            wallet,
                            HttpStatus.CREATED);
                }
            } catch (AlreadyExistingException e) {
                throw e;
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
    public ResponseEntity<?> takeMoney(
            @PathVariable("id") Integer idWallet,
            @RequestParam float money,
            HttpServletRequest request) {
        try {
            logger.info(marker, "takeMoney() request received ... pending");

            if (money < 0) {
                throw new WalletValueException("Money input cannot be lower than 0", "takeMoney");
            }

            Wallet wallet = walletService.getWalletById(idWallet);
            if (wallet == null) {
                return ResponseEntity.notFound().build();
            }

            float remainingAmount = wallet.getValue() - money;
            if (remainingAmount < 0) {
                throw new WalletValueException("Transaction cannot be completed. Insufficient funds in the wallet.", "takeMoney");
            }

            wallet = walletService.takeMoneyWallet(idWallet, money, request);

            logger.info(marker, "takeMoney() request received ... 200 {}", wallet);
            return ResponseEntity.ok(wallet);
        } catch (WalletValueException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Exception occurred: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing");
        }
    }




    @Operation(summary = "Get wallet by user ID")
    @ApiResponse(responseCode = "200", description = "Wallet found for the user",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Wallet.class)))
    @ApiResponse(responseCode = "404", description = "No wallet found for the user")
    @GetMapping (value = "/userId/{id}")
    public ResponseEntity<Wallet> getWalletByUserId(@PathVariable("id") Integer id) throws WalletNotFoundException {
        try {
            Wallet wallet = walletService.getWalletByUserId(id);
            logger.info(marker, "getWalletByUserId() request received ... pending");
            if (wallet != null) {
                logger.info(marker, "getWalletByUserId() request received ... 200 {}", wallet);
                return new ResponseEntity<>(wallet, HttpStatus.OK);
            } else {
                logger.info(marker, "getWalletByUserId() request received ... 404 Not Found{}");
                throw new WalletNotFoundException(String.valueOf(id), "getWalletByUserId()");
            }
        } catch (WalletNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Exception occurred: {}", ex.getMessage());
            throw new WalletNotFoundException(String.valueOf(id), "getWalletByUserId()");
        }
    }
}
