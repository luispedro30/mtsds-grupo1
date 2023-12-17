package ecommerce.Service;

import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Models.User;
import ecommerce.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private String encryptPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    public User updateUser(User user) throws Exception {
        // Check if the user exists
        User userPast = userRepository.findById(user.getId())
                .orElseThrow(() -> new Exception("User does not exist "));

        String encryptedPassword = encryptPassword(user.getPassword());

        User newUser = new User();
        newUser.setId(userPast.getId());
        newUser.setName(user.getName());
        newUser.setActive(user.getActive());
        newUser.setLogin(userPast.getLogin());
        newUser.setRole(userPast.getRole());
        newUser.setPassword(encryptedPassword);

        return userRepository.save(newUser);
    }

    public User updateUserByAdmin(User user) throws Exception {
        // Check if the user exists
        User userPast = userRepository.findById(user.getId())
                .orElseThrow(() -> new Exception("User does not exist "));

        String encryptedPassword = encryptPassword(user.getPassword());

        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setName(user.getName());
        newUser.setActive(user.getActive());
        newUser.setLogin(user.getLogin());
        newUser.setRole(user.getRole());
        newUser.setPassword(encryptedPassword);

        return userRepository.save(user);
    }

    public void deleteUser(Integer id) throws Exception {
        // Check if the user exists
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User does not exist"));

        userRepository.delete(user);
    }

}
