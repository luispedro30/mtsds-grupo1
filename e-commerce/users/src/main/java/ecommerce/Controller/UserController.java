package ecommerce.Controller;

import ecommerce.Models.User;
import ecommerce.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/Landing")
    public ResponseEntity<String> landing(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Application is working fine.");
    }


    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users =  userService.getAllUsers();
        if(!users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(users);
        }
        return new ResponseEntity<List<User>>(
                HttpStatus.NOT_FOUND);
    }

    @GetMapping (value = "/users", params = "name")
    public ResponseEntity<User> getUserByName(@RequestParam("name") String userName){
        User user = userService.getUserByName(userName);
        if(user != null) {
            return new ResponseEntity<User>(
                    user,
                    HttpStatus.OK);
        }
        return new ResponseEntity<User>(
                HttpStatus.NOT_FOUND);
    }

    @GetMapping (value = "/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Integer id) throws Exception {
        User user = userService.getUserById(id);
        if(user != null) {
            return new ResponseEntity<User>(
                    user,
                    HttpStatus.OK);
        }
        return new ResponseEntity<User>(
                HttpStatus.NOT_FOUND);
    }

    @PostMapping (value = "/users")
    public ResponseEntity<User> addUser(@RequestBody User user, HttpServletRequest request){
        if(user != null)
            try {
                userService.saveUser(user);
                return new ResponseEntity<User>(
                        user,
                        HttpStatus.CREATED);
            }catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
    }
}
