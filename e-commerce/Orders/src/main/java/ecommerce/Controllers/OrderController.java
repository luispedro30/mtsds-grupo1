package ecommerce.Controllers;

import ecommerce.Exceptions.ItemDoesNotExistException;
import ecommerce.Models.Order;
import ecommerce.Services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.weaver.ast.Or;
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
    ResponseEntity<Order> getById(@PathVariable Integer id){
        Order order =  orderService.getOrderById(id);
        if(order != null) {
            return new ResponseEntity<Order>(
                    order,
                    HttpStatus.OK);
        }
        return new ResponseEntity<Order>(
                HttpStatus.NOT_FOUND);
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
    ResponseEntity<?> add(@RequestBody Order order, HttpServletRequest request){

        Order newOrder;
        try {
            newOrder = orderService.addOrder(order, request);
        } catch (ItemDoesNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        } catch (Exception e){
            System.out.println("Erro");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        newOrder.setOrderId(order.getOrderId());
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);

    }

}
