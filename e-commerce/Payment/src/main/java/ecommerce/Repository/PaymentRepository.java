package ecommerce.Repository;

import ecommerce.Models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    boolean existsByUserIdAndOrderId(Integer userId, Integer OrderId);

    List<Payment> findAll();

    Optional<Payment> findById(Integer id);

    List<Payment> findByUserId(Integer userId);
}
