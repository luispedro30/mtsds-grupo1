package ecommerce.Repository;

import ecommerce.Models.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDao, String> {
    UserDao findByUsername(String userName);
}

