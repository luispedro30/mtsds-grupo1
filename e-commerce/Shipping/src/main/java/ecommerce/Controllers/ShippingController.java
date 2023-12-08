package ecommerce.Controllers;

import ecommerce.Exceptions.ItemDoesNotExistException;

import ecommerce.Models.Shipping;
import ecommerce.Services.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @GetMapping
    ResponseEntity<List<Shipping>> getAll(){
        List<Shipping> payments = shippingService.getAllShipping();
        return ResponseEntity.status(HttpStatus.OK).body(payments);
    }

    @GetMapping("/{id}")
    ResponseEntity<Shipping> getById(@PathVariable Integer id){
        Shipping shipping = shippingService.getShippingById(id);
        return ResponseEntity.status(HttpStatus.OK).body(shipping);
    }

    @PostMapping
    ResponseEntity<?> add(@RequestBody Shipping shipping){

        Shipping newShipping;
        try {
            newShipping = shippingService.addPayment(shipping);
        } catch (ItemDoesNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        } catch (Exception e){
            System.out.println("Erro");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        newShipping.setShippingId(shipping.getShippingId());
        return new ResponseEntity<>(newShipping, HttpStatus.CREATED);

    }
}
