package io.github.yvlar.coroute.domain.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

/**
 * Service de génération et de validation des JWT.
 *
 * <p>Le secret et la durée d'expiration sont fournis par l'environnement :
 *
 * <ul>
 *   <li>{@code JWT_SECRET} — obligatoire, au moins {@value #MIN_SECRET_LENGTH} caractères ;
 *   <li>{@code JWT_EXPIRATION_SECONDS} — optionnel, défaut {@value #DEFAULT_EXPIRATION_SECONDS} s.
 * </ul>
 *
 * <p>Aucun secret de production n'est codé en dur. Le constructeur valide la configuration au
 * démarrage et échoue de manière explicite si le secret est absent ou trop court.
 */
@Singleton
public final class JwtService {

  static final int MIN_SECRET_LENGTH = 32;
  static final long DEFAULT_EXPIRATION_SECONDS = 86_400L;

  private final SecretKey key;
  private final long expirationMs;

  /**
   * Constructeur utilisé par l'injection de dépendances : lit la configuration de l'environnement.
   */
  public JwtService() {
    this(readSecretFromEnv(), readExpirationFromEnv());
  }

  /**
   * Constructeur explicite (utile pour les tests et une configuration programmatique).
   *
   * @param secret le secret de signature HMAC
   * @param expirationSeconds la durée de validité du token en secondes
   */
  public JwtService(final String secret, final long expirationSeconds) {
    if (secret == null || secret.strip().length() < MIN_SECRET_LENGTH) {
      throw new IllegalStateException(
          "Configuration JWT invalide : la variable JWT_SECRET est absente ou trop courte "
              + "(au moins "
              + MIN_SECRET_LENGTH
              + " caractères requis).");
    }
    if (expirationSeconds <= 0) {
      throw new IllegalStateException(
          "Configuration JWT invalide : JWT_EXPIRATION_SECONDS doit être un entier positif.");
    }
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationSeconds * 1000L;
  }

  private static String readSecretFromEnv() {
    return System.getenv("JWT_SECRET");
  }

  private static long readExpirationFromEnv() {
    final String raw = System.getenv("JWT_EXPIRATION_SECONDS");
    if (raw == null || raw.isBlank()) {
      return DEFAULT_EXPIRATION_SECONDS;
    }
    try {
      return Long.parseLong(raw.strip());
    } catch (NumberFormatException e) {
      throw new IllegalStateException(
          "Configuration JWT invalide : JWT_EXPIRATION_SECONDS n'est pas un entier valide : " + raw,
          e);
    }
  }

  public String genererToken(final String utilisateurId, final String email) {
    return Jwts.builder()
        .subject(utilisateurId)
        .claim("email", email)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expirationMs))
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
