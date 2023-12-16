package ecommerce.Repository;

import ecommerce.Models.Order;
import ecommerce.Models.Products;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o JOIN FETCH o.products WHERE o.orderId = :orderId")
    Order findOrderWithProductsByOrderId(@Param("orderId") Integer orderId);
}
