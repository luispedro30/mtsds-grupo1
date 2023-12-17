package ecommerce.Controllers;

import ecommerce.Exceptions.ItemDoesNotExistException;

import ecommerce.Models.Shipping;
import ecommerce.Services.ShippingService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/{orderId}/{userId}")
    public ResponseEntity<Shipping> getShippingByOrderUserId(
            @PathVariable Integer orderId,
            @PathVariable Integer userId) {

        Optional<Shipping> shipping = shippingService.getShippingByOrderIdUserId(orderId, userId);

        if (shipping.isPresent()) {
            return new ResponseEntity<>(shipping.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    ResponseEntity<?> add(@RequestBody Shipping shipping, HttpServletRequest request){

        Shipping newShipping;
        try {
            newShipping = shippingService.addShipping(shipping, request);
        } catch (ItemDoesNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        } catch (Exception e){
            System.out.println("Erro");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        newShipping.setShippingId(shipping.getShippingId());
        return new ResponseEntity<>(newShipping, HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    public ResponseEntity<Shipping> updateOrder(@PathVariable Integer id,
                                                @RequestBody Shipping updatedShipping) {
        Shipping savedShipping = shippingService.updateShipping(id, updatedShipping);
        if (savedShipping != null) {
            return ResponseEntity.ok(savedShipping);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
