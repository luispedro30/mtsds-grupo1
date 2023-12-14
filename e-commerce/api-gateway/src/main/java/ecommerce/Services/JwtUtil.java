package ecommerce.Services;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private byte[] secretKey = "${api.security.token.secret}".getBytes();

    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean isInvalid(String token) {
        System.out.println(this.getAllClaimsFromToken(token));
        try {
            return this.getAllClaimsFromToken(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException exception) {
            return true;
        }
    }
}
