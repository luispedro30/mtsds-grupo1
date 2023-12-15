package ecommerce.Controller;

import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Models.User;
import ecommerce.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private static Marker marker = MarkerFactory.getMarker("users-service");

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/Landing")
    public ResponseEntity<String> landing(){
        logger.info(marker,"Checking that landing is working fine... 200 Ok");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Application is working fine.");
    }


    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users =  userService.getAllUsers();
        logger.info(marker,"getAllUsers() request received ... pending");
        if(!users.isEmpty()) {
            logger.info(marker,"getAllUsers() request received ... 200 Ok{}",users);
            return ResponseEntity.status(HttpStatus.OK).body(users);
        }
        logger.info(marker,"getAllUsers() request received ... 404 Not Found{}",users);
        return new ResponseEntity<List<User>>(
                HttpStatus.NOT_FOUND);
    }

    @GetMapping (value = "/users", params = "name")
    public ResponseEntity<List<User>> getUserByName(@RequestParam("name") String userName){
        List<User> users = userService.getAllUserByName(userName);
        logger.info(marker,"getUserByName() request received ... pending");
        if(users != null) {
            logger.info(marker,"getUserByName() request received ... 200 Ok{}",users);
            return new ResponseEntity<List<User>>(
                    users,
                    HttpStatus.OK);
        }
        logger.info(marker,"getUserByName() request received ... 404 Not Found{}");
        return new ResponseEntity<List<User>>(
                HttpStatus.NOT_FOUND);
    }

    @GetMapping (value = "/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Integer id) throws Exception {
        User user = userService.getUserById(id);
        logger.info(marker,"getUserById() request received ... pending");
        if(user != null) {
            logger.info(marker,"getUserById() request received ... 200 Ok{}",user);
            return new ResponseEntity<User>(
                    user,
                    HttpStatus.OK);
        }
        logger.info(marker,"getUserById() request received ... 404 Not Found{}");
        return new ResponseEntity<User>(
                HttpStatus.NOT_FOUND);
    }

    /*
    @PostMapping (value = "/users")
    public ResponseEntity<User> addUser(@RequestBody User user, HttpServletRequest request) throws Exception {
        logger.info(marker,"addUser() request received ... pending");
        if(user != null)
            try {
                String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());

                User newUser = new User(
                        user.getName(),
                        encryptedPassword,
                        user.getActive(),
                        user.getRole());

                userService.saveUser(user);
                logger.info(marker,"addUser() request received ... 201 Created{}",user);
                return new ResponseEntity<User>(
                        user,
                        HttpStatus.CREATED);
            } catch (AlreadyExistingException e){
                logger.error(marker,e.getMessage());
                return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(marker,e.getMessage());
                return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        logger.info(marker,"addUser() request received ... Bad Request{}");
        return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
    }
    */
}
