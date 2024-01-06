package ecommerce.Repository;

import ecommerce.Models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o JOIN FETCH o.products WHERE o.orderId = :orderId")
    Order findOrderWithProductsByOrderId(@Param("orderId") Integer orderId);

    List<Order> findAll();

    Optional<Order> findById(Integer id);

    List<Order> findByUserId(Integer userId);
}
