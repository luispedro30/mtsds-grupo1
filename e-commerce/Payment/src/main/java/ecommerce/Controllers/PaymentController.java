package ecommerce.Controllers;

import ecommerce.Exceptions.ItemDoesNotExistException;
import ecommerce.Models.Payment;
import ecommerce.Services.PaymentService;
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

    @GetMapping("/Landing")
    public ResponseEntity<String> landing(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Application is working fine.");

    }

    @GetMapping
    ResponseEntity<List<Payment>> getAll(){
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.status(HttpStatus.OK).body(payments);
    }

    @GetMapping("/{id}")
    ResponseEntity<Payment> getById(@PathVariable Integer id){
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.status(HttpStatus.OK).body(payment);
    }

    @PostMapping
    ResponseEntity<?> add(@RequestBody Payment payment){

        Payment newPayment;
        try {
            newPayment = paymentService.addPayment(payment);
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
