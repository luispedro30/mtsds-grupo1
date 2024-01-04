package ecommerce.Repository;

import ecommerce.Enums.Category;
import ecommerce.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Integer> {
    public List<Product> findAllByCategory(Category category);
    public List<Product> findAllByName(String name);

    List<Product> findAll();

    Optional<Product> findById(Integer id);

    void delete(Product product);

}
