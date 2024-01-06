package ecommerce.Controllers;

import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Exceptions.ItemDoesNotExistException;
import ecommerce.Infra.RestExceptionHandler;
import ecommerce.Models.Order;
import ecommerce.Services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.weaver.ast.Or;
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
@RequestMapping("orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestExceptionHandler restExceptionHandler;

    private static Marker marker = MarkerFactory.getMarker("order-service");

    private static Logger logger = LoggerFactory.getLogger(OrderController.class);


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
     * Retrieve all orders.
     *
     * @return ResponseEntity with the list of orders or a NOT_FOUND status if no orders exist
     */
    @Operation(summary = "Retrieve all orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders retrieved"),
            @ApiResponse(responseCode = "404", description = "No orders found")
    })
    @GetMapping
    public ResponseEntity<List<Order>> getAll(){
        List<Order> orders =  orderService.getAll();
        if(!orders.isEmpty()) {
            return new ResponseEntity<List<Order>>(
                    orders,
                    HttpStatus.OK);
        }
        return new ResponseEntity<List<Order>>(
                HttpStatus.NOT_FOUND);
    }

    /**
     * Retrieve an order by ID.
     *
     * @param id ID of the order to retrieve
     * @return ResponseEntity with the requested order or a NOT_FOUND status if the order does not exist
     */
    @Operation(summary = "Retrieve an order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) throws ItemDoesNotExistException {
        try {
            Order order = orderService.getOrderById(id);
            logger.info(marker, "getOrderById() request received ... pending");
            if (order != null) {
                logger.info(marker, "getOrderById() request received ... 200 {}", order);
                return new ResponseEntity<>(order, HttpStatus.OK);
            } else {
                logger.info(marker, "getOrderById() request received ... 404 Not Found{}");
                throw new ItemDoesNotExistException(String.valueOf(id), "getOrderById()");
            }
        } catch (ItemDoesNotExistException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Exception occurred: {}", ex.getMessage());
            throw new ItemDoesNotExistException(String.valueOf(id), "getOrderById()");
        }
    }

    /**
     * Add a new order.
     *
     * @param order   Order details to be added
     * @param request HTTP request
     * @return ResponseEntity with the added order details or appropriate error status
     */
    @Operation(summary = "Add a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order added successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    @PostMapping
    public ResponseEntity<?> add(@RequestBody Order order, HttpServletRequest request) {
        try {
            Integer orderId = order.getOrderId(); // Assuming the ID field is 'orderId'
            if (orderId != null && orderService.getOrderById(orderId) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Order with ID " + orderId + " already exists");
            }

            Order newOrder = orderService.addOrder(order, request);
            newOrder.setOrderId(order.getOrderId());
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
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
    @Operation(summary = "Retrieve orders by User ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User does not have any orders")
    })
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Integer userId) throws ItemDoesNotExistException {
        try {
            List<Order> orders = orderService.findByUserId(userId);
            logger.info(marker, "getOrdersByUserId() request received ... pending");
            if (!orders.isEmpty()) {
                logger.info(marker, "getOrdersByUserId() request received ... 200 {}", orders);
                return new ResponseEntity<>(orders, HttpStatus.OK);
            } else {
                logger.info(marker, "getOrdersByUserId() request received ... 404 Not Found{}");
                throw new ItemDoesNotExistException(String.valueOf(userId), "getOrdersByUserId()");
            }
        } catch (ItemDoesNotExistException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Exception occurred: {}", ex.getMessage());
            throw new ItemDoesNotExistException(String.valueOf(userId), "getOrdersByUserId()");
        }
    }


}
