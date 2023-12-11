package ecommerce.Service;

import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Models.User;
import ecommerce.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) throws Exception {
        return userRepository.findById(id).orElseThrow(() -> new Exception("User não existe"));
    }

    public List<User> getAllUserByName(String userName) {
        return userRepository.findAllByName(userName);
    }

    public User saveUser(User user) throws AlreadyExistingException {
        return userRepository.save(user);
    }

}
