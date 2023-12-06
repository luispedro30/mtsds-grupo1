package ecommerce.Repository;

import ecommerce.Models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    boolean existsByUserIdAndProductIdAndOrderId(Integer userId, Integer ProductId, Integer OrderId);
}
