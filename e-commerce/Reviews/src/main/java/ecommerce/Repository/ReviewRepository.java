package ecommerce.Repository;

import ecommerce.Models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    boolean existsByUserIdAndProductIdAndOrderId(Integer userId, Integer ProductId, Integer OrderId);

    List<Review> findAll();

    Optional<Review> findById(Integer id);
}
