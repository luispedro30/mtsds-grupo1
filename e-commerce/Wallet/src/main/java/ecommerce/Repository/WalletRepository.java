package ecommerce.Repository;

import ecommerce.Models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    public Wallet findByUserId(Integer userId);
}
