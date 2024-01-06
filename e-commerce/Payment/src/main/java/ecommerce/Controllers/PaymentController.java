package ecommerce.Controllers;

import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Exceptions.ItemDoesNotExistException;
import ecommerce.Infra.RestExceptionHandler;
import ecommerce.Models.Payment;
import ecommerce.Services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
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

    @Autowired
    private RestExceptionHandler restExceptionHandler;

    private static Marker marker = MarkerFactory.getMarker("payment-service");

    private static Logger logger = LoggerFactory.getLogger(PaymentController.class);

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
    ResponseEntity<Payment> getById(@PathVariable Integer id) throws ItemDoesNotExistException {
        try {
            Payment payment = paymentService.getPaymentById(id);
            logger.info(marker, "getById() request received ... pending");
            if (payment != null) {
                logger.info(marker, "getById() request received ... 200 {}", payment);
                return new ResponseEntity<>(payment, HttpStatus.OK);
            } else {
                logger.info(marker, "getById() request received ... 404 Not Found{}");
                throw new ItemDoesNotExistException(String.valueOf(id), "getById()");
            }
        } catch (ItemDoesNotExistException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Exception occurred: {}", ex.getMessage());
            throw new ItemDoesNotExistException(String.valueOf(id), "getById()");
        }
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
    public ResponseEntity<?> add(@RequestBody Payment payment, HttpServletRequest request) {
        try {
            Integer paymentId = payment.getPaymentId();
            if (paymentId != null && paymentService.getPaymentById(paymentId) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Payment with ID " + paymentId + " already exists");
            }

            Payment newPayment = paymentService.addPayment(payment, request);
            newPayment.setPaymentId(payment.getPaymentId());
            return new ResponseEntity<>(newPayment, HttpStatus.CREATED);
        } catch (ItemDoesNotExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        } catch (AlreadyExistingException e) {
            return restExceptionHandler.alreadyExistingExceptionHandler(e);
        } catch (Exception e) {
            System.out.println("Error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Retrieve payments by User ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User does not have any payments")
    })
    public ResponseEntity<List<Payment>> getPaymentsByUserId(@PathVariable Integer userId) throws ItemDoesNotExistException {
        try {
            List<Payment> payments = paymentService.findByUserId(userId);
            logger.info(marker, "getPaymentsByUserId() request received ... pending");
            if (!payments.isEmpty()) {
                logger.info(marker, "getPaymentsByUserId() request received ... 200 {}", payments);
                return new ResponseEntity<>(payments, HttpStatus.OK);
            } else {
                logger.info(marker, "getPaymentsByUserId() request received ... 404 Not Found{}");
                throw new ItemDoesNotExistException(String.valueOf(userId), "getPaymentsByUserId()");
            }
        } catch (ItemDoesNotExistException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Exception occurred: {}", ex.getMessage());
            throw new ItemDoesNotExistException(String.valueOf(userId), "getPaymentsByUserId()");
        }
    }
}
