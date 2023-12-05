package ecommerce.Repository;

import ecommerce.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductsRepository extends JpaRepository<Product, Integer> {
    public List<Product> findAllByCategory(String category);
    public List<Product> findAllByName(String name);
}
