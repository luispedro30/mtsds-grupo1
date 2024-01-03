package ecommerce.Controller;

import ecommerce.Dto.WalletDto;
import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Models.User;
import ecommerce.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Check if the user module is working")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User module status retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/Landing")
    public ResponseEntity<String> landing(){
        logger.info(marker,"Checking that landing is working fine... 200 Ok");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Application is working fine.");
    }


    @Operation(summary = "Retrieve all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users retrieved"),
            @ApiResponse(responseCode = "404", description = "No users found")
    })
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

    @Operation(summary = "Retrieve user by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users by name retrieved"),
            @ApiResponse(responseCode = "404", description = "No users found by name")
    })
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

    @Operation(summary = "Retrieve user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User by ID retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found by ID")
    })
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


    @Operation(summary = "Add a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User added successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User already exists")
    })

    @PostMapping (value = "/users")
    public ResponseEntity<User> addUser(@RequestBody User user, HttpServletRequest request) throws Exception {
        logger.info(marker,"addUser() request received ... pending");
        if(user != null)
            try {
                String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());

                /*User newUser = new User(
                        user.getName(),
                        encryptedPassword,
                        user.getActive(),
                        user.getRole());*/

                userService.saveUser(user);

                WalletDto walletDto = new WalletDto();
                walletDto.setUserId(user.getId());
                walletDto.setValue(0);

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

    @Operation(summary = "Update user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUserById(@RequestBody User user, HttpServletRequest request) {
        try {
            User updatedUser = userService.updateUser(user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Update user by ID as admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully as admin"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/admin/users/{id}")
    public ResponseEntity<User> updateUserByIdByAdmin(@RequestBody User user, HttpServletRequest request) {
        try {
            User updatedUser = userService.updateUserByAdmin(user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @Operation(summary = "Delete user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Integer id,
                                           HttpServletRequest request) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
