package ecommerce.Services;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${api.security.token.secret}")
    private String secret;

    private byte[] secretKey;

    @PostConstruct
    public void init() {
        secretKey = Base64.getEncoder().encode(secret.getBytes());
    }

    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            // Token has expired
            // Log or handle the expiration according to your requirements
            ex.printStackTrace(); // Log the exception
            return null;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException ex) {
            // Invalid token or signature
            // Log or handle the specific exception according to your requirements
            ex.printStackTrace(); // Log the exception
            return null;
        } catch (JwtException ex) {
            // Other JWT related exceptions
            // Log or handle the exception according to your requirements
            ex.printStackTrace(); // Log the exception
            return null;
        } catch (Exception ex) {
            // Catch any other unexpected exceptions
            // Log or handle the exception according to your requirements
            ex.printStackTrace(); // Log the exception
            return null;
        }
    }

    public boolean isInvalid(String token) {
        Claims claims = this.getAllClaimsFromToken(token);
        if (claims == null) {
            // Handle null claims, maybe log a message or return as expired
            return true; // or handle differently based on your logic
        }
        try {
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException exception) {
            return true;
        }
    }
}