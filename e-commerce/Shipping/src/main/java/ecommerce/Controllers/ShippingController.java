package ecommerce.Controllers;

import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Exceptions.CustomInternalServerException;
import ecommerce.Exceptions.ItemDoesNotExistException;

import ecommerce.Exceptions.NoShippingFoundException;
import ecommerce.Infra.RestExceptionHandler;
import ecommerce.Models.Shipping;
import ecommerce.Services.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.criteria.CriteriaBuilder;
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
import java.util.Optional;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @Autowired
    private RestExceptionHandler restExceptionHandler;

    private static Marker marker = MarkerFactory.getMarker("shipping-service");

    private static Logger logger = LoggerFactory.getLogger(ShippingController.class);


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
    public ResponseEntity<List<Shipping>> getAll() {
        try {
            List<Shipping> shippings = shippingService.getAllShipping();
            if (!shippings.isEmpty()) {
                logger.info("getAll() request received ... 200 OK");
                return new ResponseEntity<>(shippings, HttpStatus.OK);
            } else {
                logger.info("getAll() request received ... 404 Not Found");
                throw new NoShippingFoundException("No shippings found");
            }
        } catch (NoShippingFoundException ex) {
            logger.warn("No shippings found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Exception occurred: {}", ex.getMessage());
            throw new CustomInternalServerException("Internal server error occurred");
        }
    }


    @Operation(summary = "Retrieve shipping details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipping details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Shipping details not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Shipping> getById(@PathVariable Integer id) throws ItemDoesNotExistException {
        try {
            Shipping shipping = shippingService.getShippingById(id);
            logger.info("getById() request received ... pending");
            if (shipping != null) {
                logger.info("getById() request received ... 200 {}", shipping);
                return new ResponseEntity<>(shipping, HttpStatus.OK);
            } else {
                logger.info("getById() request received ... 404 Not Found");
                throw new ItemDoesNotExistException(String.valueOf(id), "getById()");
            }
        } catch (ItemDoesNotExistException ex) {
            logger.error("Item does not exist: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Exception occurred: {}", ex.getMessage());
            throw new ItemDoesNotExistException(String.valueOf(id), "getById()");
        }
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
        try {
            Optional<Shipping> shipping = shippingService.getShippingByOrderIdUserId(orderId, userId);

            if (shipping.isPresent()) {
                logger.info("Shipping found for Order ID {} and User ID {}.", orderId, userId);
                return new ResponseEntity<>(shipping.get(), HttpStatus.OK);
            } else {
                logger.warn("Shipping not found for Order ID {} and User ID {}.", orderId, userId);
                throw new NoShippingFoundException("Shipping not found for Order ID " + orderId + " and User ID " + userId);
            }
        } catch (NoShippingFoundException ex) {
            logger.error("Shipping not found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Exception occurred while retrieving shipping: {}", ex.getMessage());
            throw new CustomInternalServerException("Error retrieving shipping information");
        }
    }


    @Operation(summary = "Add new shipping details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Shipping details added successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    @PostMapping
    public ResponseEntity<?> add(@RequestBody Shipping shipping, HttpServletRequest request) {
        try {
            Integer shippingId = shipping.getShippingId();
            if (shippingId != null && shippingService.getShippingById(shippingId) != null) {
                logger.info("Shipping with ID {} already exists", shippingId);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Shipping with ID " + shippingId + " already exists");
            }

            Shipping newShipping = shippingService.addShipping(shipping, request);
            newShipping.setShippingId(shipping.getShippingId());
            logger.info("New Shipping added with ID {}", shipping.getShippingId());
            return new ResponseEntity<>(newShipping, HttpStatus.CREATED);
        } catch (ItemDoesNotExistException e) {
            logger.error("User does not exist: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        } catch (AlreadyExistingException e) {
            logger.error("Shipping already exists: {}", e.getMessage());
            return restExceptionHandler.alreadyExistingExceptionHandler(e);
        } catch (Exception e) {
            logger.error("Error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Update shipping details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipping details updated successfully"),
            @ApiResponse(responseCode = "404", description = "Shipping details not found")
    })
    @PutMapping("/{id}/{supplierId}")
    public ResponseEntity<Shipping> updateShipping(@PathVariable Integer id,
                                                   @PathVariable Integer supplierId,
                                                   @RequestBody Shipping updatedShipping,
                                                   HttpServletRequest request) {
        try {
            Shipping savedShipping = shippingService.updateShipping(id, updatedShipping, request);
            if (savedShipping != null) {
                logger.info("Shipping with ID {} updated successfully.", id);
                return ResponseEntity.ok(savedShipping);
            } else {
                logger.warn("Shipping with ID {} not found.", id);
                throw new NoShippingFoundException("Shipping with ID " + id + " not found");
            }
        } catch (NoShippingFoundException ex) {
            logger.error("Shipping not found: {}", ex.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            logger.error("Error updating shipping with ID {}: {}", id, ex.getMessage());
            throw new CustomInternalServerException("Error updating shipping");
        }
    }


}
