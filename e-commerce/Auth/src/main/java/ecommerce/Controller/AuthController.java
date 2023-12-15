package ecommerce.Controller;

import ecommerce.Config.TokenGenerator;
import ecommerce.Models.UserDao;
import ecommerce.Services.UserService;
import io.jsonwebtoken.*;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RequestMapping({ "/auth" })
@RestController
public class AuthController {


    @Autowired
    UserService userService;

    @Autowired
    private TokenGenerator jwtGenerator;

    private String secret = "9a4f2c8d3b7a1e6f45c8a0b3f267d8b1d4e6f3c8a9d2b5f8e3a9c8b5f6v8a3d9";

    @PostMapping
    public ResponseEntity<?> loginUser(
            @ApiParam(value = "User information", required = true)
            @RequestBody UserDao user) {
        try {

            if(user.getUsername() == null || user.getPassword() == null) {
                HashMap<String,String> map = new HashMap<>();
                map.put("message","Username or Password is Empty");
                map.put("token",null);
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }
            //Check if users exists in database
            userService.getUserByName(user.getUsername(), user.getPassword());

            //If everything is okay, send the token
            return new ResponseEntity<>(jwtGenerator.generateToken(user), HttpStatus.OK);

        } catch (NullPointerException e) {
            HashMap<String,String> map = new HashMap<>();
            map.put("message","User not found");
            map.put("token",null);
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/tokenValidation")
    public ResponseEntity<?> tokenValidation(
            @ApiParam(value = "Http Headers", required = true)
            @RequestHeader HttpHeaders headers) {
        System.out.println(headers.toString());
        String authHeader = headers.get(HttpHeaders.AUTHORIZATION).get(0);
        //System.out.println(authHeader);

        String[] parts = authHeader.split(" ");
        Claims c = null;
        try{

            c = Jwts.parser().setSigningKey(secret).parseClaimsJws(parts[1]).getBody();
            System.out.println(c.toString());
            //Get token userName
            System.out.println(c.getSubject());
        } catch (MalformedJwtException | IllegalArgumentException | ExpiredJwtException |
                 UnsupportedJwtException e) {
            HashMap<String,String> map = new HashMap<>();
            map.put("message","Invalid token");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        HashMap<String,String> map = new HashMap<>();
        map.put("userName",c.getSubject());
        map.put("token",parts[1]);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}


