package ecommerce.Controllers;

import ecommerce.Exceptions.ItemDoesNotExistException;
import ecommerce.Models.Order;
import ecommerce.Services.OrderService;
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

    @GetMapping
    ResponseEntity<List<Order>> getAll(){
        List<Order> cars = orderService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(cars);
    }

    @GetMapping("/{id}")
    ResponseEntity<Order> getById(@PathVariable Integer id){
        Order order = orderService.getOrderById(id);
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @PostMapping
    ResponseEntity<?> add(@RequestBody Order order){

        Order newOrder;
        try {
            newOrder = orderService.addOrder(order);
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
