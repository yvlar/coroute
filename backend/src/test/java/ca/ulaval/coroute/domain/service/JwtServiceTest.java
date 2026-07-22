package ca.ulaval.coroute.domain.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JwtServiceTest {

  private static final String UTILISATEUR_ID = "utilisateur-123";
  private static final String EMAIL = "marc@coroute.ca";

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    this.jwtService = new JwtService();
  }

  // ─── genererToken ────────────────────────────────────────────────────

  @Test
  void givenUtilisateurValide_whenGenererToken_thenRetourneTokenNonNull() {
    final String token = jwtService.genererToken(UTILISATEUR_ID, EMAIL);
    assertNotNull(token);
  }

  @Test
  void givenUtilisateurValide_whenGenererToken_thenTokenNonVide() {
    final String token = jwtService.genererToken(UTILISATEUR_ID, EMAIL);
    assertFalse(token.isBlank());
  }

  // ─── extraireUtilisateurId ───────────────────────────────────────────

  @Test
  void givenTokenValide_whenExtraireUtilisateurId_thenRetourneId() {
    final String token = jwtService.genererToken(UTILISATEUR_ID, EMAIL);

    final String id = jwtService.extraireUtilisateurId(token);

    assertEquals(UTILISATEUR_ID, id);
  }

  // ─── extraireEmail ───────────────────────────────────────────────────

  @Test
  void givenTokenValide_whenExtraireEmail_thenRetourneEmail() {
    final String token = jwtService.genererToken(UTILISATEUR_ID, EMAIL);

    final String email = jwtService.extraireEmail(token);

    assertEquals(EMAIL, email);
  }

  // ─── estValide ───────────────────────────────────────────────────────

  @Test
  void givenTokenValide_whenEstValide_thenRetourneTrue() {
    final String token = jwtService.genererToken(UTILISATEUR_ID, EMAIL);

    assertTrue(jwtService.estValide(token));
  }

  @Test
  void givenTokenInvalide_whenEstValide_thenRetourneFalse() {
    assertFalse(jwtService.estValide("token.invalide.ici"));
  }

  @Test
  void givenTokenVide_whenEstValide_thenRetourneFalse() {
    assertFalse(jwtService.estValide(""));
  }

  @Test
  void givenDeuxTokensDifferentsUtilisateurs_whenGenerer_thenTokensDifferents() {
    final String token1 = jwtService.genererToken(UTILISATEUR_ID, EMAIL);
    final String token2 = jwtService.genererToken("autre-id", "autre@coroute.ca");

    assertAll(
        () -> assertFalse(token1.equals(token2)),
        () -> assertEquals(UTILISATEUR_ID, jwtService.extraireUtilisateurId(token1)),
        () -> assertEquals("autre-id", jwtService.extraireUtilisateurId(token2)));
  }
}
