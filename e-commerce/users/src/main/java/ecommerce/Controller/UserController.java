package ecommerce.Controller;

import ecommerce.Dto.WalletDto;
import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Exceptions.CustomInternalServerException;
import ecommerce.Exceptions.ItemDoesNotExistException;
import ecommerce.Exceptions.NoUsersFoundException;
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
    public ResponseEntity<List<User>> getAll() {
        try {
            List<User> users = userService.getAllUsers();
            if (!users.isEmpty()) {
                logger.info("getAll() request received ... 200 OK");
                return new ResponseEntity<>(users, HttpStatus.OK);
            } else {
                logger.info("getAll() request received ... 404 Not Found");
                throw new NoUsersFoundException("No Users found");
            }
        } catch (NoUsersFoundException ex) {
            logger.warn("No Users found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Exception occurred: {}", ex.getMessage());
            throw new CustomInternalServerException("Internal server error occurred");
        }
    }

    @Operation(summary = "Retrieve user by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users by name retrieved"),
            @ApiResponse(responseCode = "404", description = "No users found by name")
    })
    @GetMapping (value = "/users", params = "name")
    public ResponseEntity<List<User>> getUserByName(@RequestParam("name") String userName) {
        try {
            List<User> users = userService.getAllUserByName(userName);
            logger.info(marker, "getUserByName() request received ... pending");
            if (!users.isEmpty()) {
                logger.info(marker, "getUserByName() request received ... 200 OK {}", users);
                return new ResponseEntity<>(users, HttpStatus.OK);
            }
            logger.info(marker, "getUserByName() request received ... 404 Not Found");
            throw new NoUsersFoundException("No users found with name: " + userName);
        } catch (NoUsersFoundException ex) {
            logger.warn("No users found with name: {}", userName);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error fetching users by name {}: {}", userName, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Retrieve user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User by ID retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found by ID")
    })
    @GetMapping (value = "/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Integer id) throws Exception {

        try {
            User user = userService.getUserById(id);
            logger.info("getUserById() request received ... pending");
            if (user != null) {
                logger.info("getUserById() request received ... 200 {}", user);
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                logger.info("getUserById() request received ... 404 Not Found");
                throw new ItemDoesNotExistException(String.valueOf(id), "getUserById()");
            }
        } catch (ItemDoesNotExistException ex) {
            logger.error("Item does not exist: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Exception occurred: {}", ex.getMessage());
            throw new ItemDoesNotExistException(String.valueOf(id), "getUserById()");
        }
    }


    @Operation(summary = "Add a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User added successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User already exists")
    })

    @PostMapping (value = "/users")
    public ResponseEntity<User> addUser(@RequestBody User user, HttpServletRequest request) {
        try {
            logger.info(marker, "addUser() request received ... pending");

            if (user == null || user.getLogin() == null) {
                logger.info(marker, "addUser() request received ... Bad Request");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            String login = user.getLogin();
            Integer userId = user.getId();

            if (userService.getUserByLogin(login) != null) {
                throw new AlreadyExistingException(login, "getUserByLogin()");
            }

            if (userId != null && userService.getUserById(userId) != null) {
                throw new AlreadyExistingException(userId.toString(), "getUserById");
            }

            String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
            user.setPassword(encryptedPassword);
            userService.saveUser(user);

            WalletDto walletDto = new WalletDto();
            walletDto.setUserId(user.getId());
            walletDto.setValue(0);

            logger.info(marker, "addUser() request received ... 201 Created {}", user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);

        } catch (AlreadyExistingException e) {
            logger.error(marker, e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error(marker, "Error occurred while adding user: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        } catch (NoUsersFoundException e) {
            logger.error("User not found: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
        } catch (NoUsersFoundException e) {
            logger.error("User not found: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Operation(summary = "Delete user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Integer id,
                                           HttpServletRequest request) throws ItemDoesNotExistException {
        try {
            userService.deleteUser(id);
            logger.info("User with ID {} deleted successfully.", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ItemDoesNotExistException ex) {
            logger.warn("User with ID {} not found.", id);
            throw new ItemDoesNotExistException(id.toString(), "deleteUser()");
        } catch (Exception e) {
            logger.error("Error deleting user with ID {}: {}", id, e.getMessage());
            throw new CustomInternalServerException("Error deleting user");
        }
    }


}
