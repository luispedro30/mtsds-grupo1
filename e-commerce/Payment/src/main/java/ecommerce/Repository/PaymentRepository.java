package ecommerce.Repository;

import ecommerce.Models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    boolean existsByUserIdAndOrderId(Integer userId, Integer OrderId);
}
