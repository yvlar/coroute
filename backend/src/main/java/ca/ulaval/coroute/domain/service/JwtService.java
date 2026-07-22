package ca.ulaval.coroute.domain.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

@Singleton
public final class JwtService {

  private static final String SECRET = "coroute-secret-key-must-be-at-least-32-chars!!";
  private static final long EXPIRATION_MS = 86_400_000L; // 24 heures

  private final SecretKey key;

  public JwtService() {
    try {
      this.key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      throw new IllegalStateException("Failed to initialize JWT key", e);
    }
  }

  public String genererToken(final String utilisateurId, final String email) {
    return Jwts.builder()
        .subject(utilisateurId)
        .claim("email", email)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
        .signWith(key)
        .compact();
  }

  public String extraireUtilisateurId(final String token) {
    return parserClaims(token).getSubject();
  }

  public String extraireEmail(final String token) {
    return parserClaims(token).get("email", String.class);
  }

  public boolean estValide(final String token) {
    try {
      parserClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private Claims parserClaims(final String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }
}
