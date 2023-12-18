package ecommerce.Service;

import ecommerce.Config.MQConfig;
import ecommerce.Dto.WalletDto;
import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Messages.User2Email;
import ecommerce.Messages.WalletRegistrationMessage;
import ecommerce.Models.User;
import ecommerce.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private RabbitTemplate template;

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

        User2Email user2Email = new User2Email();
        user2Email.setOwnerRef("Ref: Wallet");
        user2Email.setSubject("User creation");
        user2Email.setEmailFrom("luispedrotrinta.1998@gmail.com");
        user2Email.setEmailTo(user.getEmail());
        user2Email.setText("Hello "+user.getName()+"," +
                "" +
                "It was created a user and a wallet in your name, it contains 0.00 euros."+
                "" +
                "Best regards");

        template.convertAndSend("user-2-email-queue", user2Email);

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

    public void walletRegistration(WalletDto walletDto){

        WalletRegistrationMessage message;

        message = new WalletRegistrationMessage();

        message.setUserId(walletDto.getUserId());
        message.setValue(walletDto.getValue());

        template.convertAndSend(
                MQConfig.EXCHANGE,
                MQConfig.ROUTING_KEY_1,
                message
        );
    }

}
