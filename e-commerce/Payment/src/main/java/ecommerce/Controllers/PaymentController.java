package ecommerce.Controllers;

import ecommerce.Exceptions.ItemDoesNotExistException;
import ecommerce.Models.Payment;
import ecommerce.Services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Check if the application is working.
     *
     * @return ResponseEntity with the application status message
     */
    @Operation(summary = "Check if the application is working")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application status retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/Landing")
    public ResponseEntity<String> landing(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Application is working fine.");

    }

    /**
     * Retrieve all payments.
     *
     * @return ResponseEntity with the list of payments or a NOT_FOUND status if no payments exist
     */
    @Operation(summary = "Retrieve all payments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of payments retrieved"),
            @ApiResponse(responseCode = "404", description = "No payments found")
    })
    @GetMapping
    ResponseEntity<List<Payment>> getAll(){
        List<Payment> payments =  paymentService.getAllPayments();
        if(!payments.isEmpty()) {
            return new ResponseEntity<List<Payment>>(
                    payments,
                    HttpStatus.OK);
        }
        return new ResponseEntity<List<Payment>>(
                HttpStatus.NOT_FOUND);
    }

    /**
     * Retrieve a payment by ID.
     *
     * @param id ID of the payment to retrieve
     * @return ResponseEntity with the requested payment or a NOT_FOUND status if the payment does not exist
     */
    @Operation(summary = "Retrieve a payment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/{id}")
    ResponseEntity<Payment> getById(@PathVariable Integer id){
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.status(HttpStatus.OK).body(payment);
    }

    /**
     * Add a new payment.
     *
     * @param payment Payment details to be added
     * @param request HTTP request
     * @return ResponseEntity with the added payment details or appropriate error status
     */
    @Operation(summary = "Add a new payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment added successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    @PostMapping
    ResponseEntity<?> add(@RequestBody Payment payment, HttpServletRequest request){

        Payment newPayment;
        try {
            newPayment = paymentService.addPayment(payment, request);
        } catch (ItemDoesNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        } catch (Exception e){
            System.out.println("Erro");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        newPayment.setPaymentId(payment.getPaymentId());
        return new ResponseEntity<>(newPayment, HttpStatus.CREATED);

    }
}
