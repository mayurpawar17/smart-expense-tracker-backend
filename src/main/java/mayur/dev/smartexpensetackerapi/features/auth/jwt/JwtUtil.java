package mayur.dev.smartexpensetackerapi.features.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

//    private final String SECRET = "my-super-secure-secret-key-1234567890";
//
//    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // IMPORTANT: Secret must be Base64 encoded for this Decoders.BASE64 logic
    // Or use Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8))
    private final String SECRET = "bXktc3VwZXItc2VjdXJlLXNlY3JldC1rZXktMTIzNDU2Nzg5MA==";

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate token
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hr
                .signWith(getSigningKey())
                .compact();
    }

    // Extract userEmail
    public String extractSubject(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}