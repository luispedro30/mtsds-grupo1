package ecommerce.Controllers;

import ecommerce.Models.User;
import ecommerce.Repository.UserRepository;
import ecommerce.DTO.AuthenticationDTO;
import ecommerce.DTO.LoginResponseDTO;
import ecommerce.DTO.RegisterDTO;
import ecommerce.Infra.Security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TokenService tokenService;

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
        if (this.userRepository.findByLogin(data.login()) != null) return ResponseEntity.badRequest().build();
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        User newUser = new User(data.login(), encryptedPassword, data.role());
        this.userRepository.save(newUser);
        return ResponseEntity.ok().build();

    }
}
