package ecommerce.Controllers;

import ecommerce.Exceptions.ItemDoesNotExistException;
import ecommerce.Models.Order;
import ecommerce.Services.OrderService;
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

    @GetMapping("/Landing")
    public ResponseEntity<String> landing(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Application is working fine.");

    }


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
