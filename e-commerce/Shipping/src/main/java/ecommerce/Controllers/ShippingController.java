package ecommerce.Controllers;

import ecommerce.Exceptions.ItemDoesNotExistException;

import ecommerce.Models.Shipping;
import ecommerce.Services.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


    @Operation(summary = "Check if the shipping module is working")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipping module status retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/Landing")
    public ResponseEntity<String> landing(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Application is working fine.");

    }


    @Operation(summary = "Retrieve all shipping details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of shipping details retrieved"),
            @ApiResponse(responseCode = "404", description = "No shipping details found")
    })
    @GetMapping
    ResponseEntity<List<Shipping>> getAll(){
        List<Shipping> shippings =  shippingService.getAllShipping();
        if(!shippings.isEmpty()) {
            return new ResponseEntity<List<Shipping>>(
                    shippings,
                    HttpStatus.OK);
        }
        return new ResponseEntity<List<Shipping>>(
                HttpStatus.NOT_FOUND);
    }


    @Operation(summary = "Retrieve shipping details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipping details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Shipping details not found")
    })
    @GetMapping("/{id}")
    ResponseEntity<Shipping> getById(@PathVariable Integer id){
        Shipping shipping = shippingService.getShippingById(id);
        return ResponseEntity.status(HttpStatus.OK).body(shipping);
    }

    @Operation(summary = "Retrieve shipping details by Order ID and User ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipping details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Shipping details not found")
    })
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

    @Operation(summary = "Add new shipping details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Shipping details added successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
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

    @Operation(summary = "Update shipping details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipping details updated successfully"),
            @ApiResponse(responseCode = "404", description = "Shipping details not found")
    })
    @PutMapping("/{id}/{fornecedorId}")
    public ResponseEntity<Shipping> updateShipping(@PathVariable Integer id,
                                                @PathVariable Integer fornecedorId,
                                                @RequestBody Shipping updatedShipping,
                                                HttpServletRequest request) throws Exception {
        Shipping savedShipping = shippingService.updateShipping(id,
                updatedShipping,
                request);
        if (savedShipping != null) {
            return ResponseEntity.ok(savedShipping);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
