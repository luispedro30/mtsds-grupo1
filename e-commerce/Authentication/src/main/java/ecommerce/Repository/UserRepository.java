package ecommerce.Repository;

import ecommerce.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,String> {
    //User Details vai ser utilizado pelo security
    UserDetails findByLogin(String login);
    List<User> findAllByName(String name);
}
