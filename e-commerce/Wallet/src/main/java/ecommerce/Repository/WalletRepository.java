package ecommerce.Repository;

import ecommerce.Models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    public Wallet findByUserId(Integer userId);
    public List<Wallet> findAll();
    Optional<Wallet> findById(Integer id);
}
