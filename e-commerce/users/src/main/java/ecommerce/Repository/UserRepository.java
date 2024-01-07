package ecommerce.Repository;

import ecommerce.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByName(String name);
    List<User> findAllByName(String name);

    List<User> findAll();

    Optional<User> findById(Integer id);

    void delete(User user);

    Optional<User> findByLogin(String login);
}
