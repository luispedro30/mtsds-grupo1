package ecommerce.Controllers;

import ecommerce.Models.User;
import ecommerce.Repository.UserRepository;
import ecommerce.DTO.AuthenticationDTO;
import ecommerce.DTO.LoginResponseDTO;
import ecommerce.DTO.RegisterDTO;
import ecommerce.Infra.Security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
                encryptedPassword,
                data.role());

        ResponseEntity<User> response = restTemplate.postForEntity(usersUrl,
                newUser,
                User.class);
        User savedUser = this.userRepository.save(newUser);
        return ResponseEntity.ok().build();

    }
}
