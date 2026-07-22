package ca.ulaval.coroute.domain.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UtilisateurTest {

  private static final String NOM = "Marc Tremblay";
  private static final String EMAIL = "marc@coroute.ca";
  private static final String MOT_DE_PASSE_HASH = "hash-abc-123";
  private static final String MAUVAIS_HASH = "hash-mauvais";

  private Utilisateur utilisateur;

  @BeforeEach
  void setUp() {
    this.utilisateur = new Utilisateur(NOM, EMAIL, MOT_DE_PASSE_HASH);
  }

  // ─── constructeur ───────────────────────────────────────────────────

  @Test
  void givenParametresValides_whenCreerUtilisateur_thenAttributsCorrects() {
    assertAll(
        () -> assertNotNull(utilisateur.getId()),
        () -> assertEquals(NOM, utilisateur.getNom()),
        () -> assertEquals(EMAIL, utilisateur.getEmail()));
  }

  @Test
  void givenDeuxUtilisateurs_whenCreer_thenIdsUniques() {
    final Utilisateur autre = new Utilisateur(NOM, EMAIL, MOT_DE_PASSE_HASH);
    assertFalse(utilisateur.getId().equals(autre.getId()));
  }

  // ─── verifierMotDePasse ─────────────────────────────────────────────

  @Test
  void givenBonHash_whenVerifierMotDePasse_thenRetourneTrue() {
    assertTrue(utilisateur.verifierMotDePasse(MOT_DE_PASSE_HASH));
  }

  @Test
  void givenMauvaisHash_whenVerifierMotDePasse_thenRetourneFalse() {
    assertFalse(utilisateur.verifierMotDePasse(MAUVAIS_HASH));
  }
}
