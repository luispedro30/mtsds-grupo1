package ecommerce.Services;

import ecommerce.Models.UserDao;
import ecommerce.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    public UserDao getUserByName(String name, String password) {
        UserDao user = userRepository.findByUsername(name);
        if(user == null){
            throw new NullPointerException("Invalid username");
        }
        if(!bcryptEncoder.matches(password, user.getPassword())){
            throw new NullPointerException("Invalid password");
        }
        return user;
    }
}