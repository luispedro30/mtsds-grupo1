package ecommerce.Controllers;

import ecommerce.Models.User;
import ecommerce.Repository.UserRepository;
import ecommerce.DTO.AuthenticationDTO;
import ecommerce.DTO.LoginResponseDTO;
import ecommerce.DTO.RegisterDTO;
import ecommerce.Infra.Security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TokenService tokenService;

    @Value("${endpoints.users-microservice.baseUrl}")
    private String usersUrl;

    /**
     * Check if the application is working.
     *
     * @return ResponseEntity with the application status message
     */
    @Operation(summary = "Check if the application is working")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application status retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/Landing")
    public ResponseEntity<String> landing(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Application is working fine.");
    }

    /**
     * Login with provided credentials.
     *
     * @param data Authentication data containing login credentials
     * @return ResponseEntity with the login response
     */
    @Operation(summary = "Login endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity login (@RequestBody AuthenticationDTO data){
        //verificar na base de dados a senha guardada de forma segura
        try{
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            System.out.println(data.login());
            System.out.println(data.password());
            var authentication = authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((User) authentication.getPrincipal());

            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Register a new user.
     *
     * @param data Registration data for the new user
     * @return ResponseEntity indicating the registration status
     */
    @Operation(summary = "Register endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDTO data) {

        RestTemplate restTemplate = new RestTemplate();


        if (this.userRepository.findByLogin(data.login()) != null) return ResponseEntity.badRequest().build();
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        long currentTimeMillis = System.currentTimeMillis();
        int uniqueInteger = (int) (currentTimeMillis % Integer.MAX_VALUE);

        User newUser = new User(uniqueInteger,
                data.name(),
                data.login(),
                data.email(),
                encryptedPassword,
                data.role());

        ResponseEntity<User> response = restTemplate.postForEntity(usersUrl,
                newUser,
                User.class);
        User savedUser = this.userRepository.save(newUser);
        return ResponseEntity.ok().build();

    }
}
