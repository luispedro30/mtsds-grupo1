package ecommerce.Repository;

import ecommerce.Models.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping,Integer> {
    boolean existsByUserIdAndOrderIdAndPaymentId(Integer userId, Integer orderId, Integer paymentId);
}
