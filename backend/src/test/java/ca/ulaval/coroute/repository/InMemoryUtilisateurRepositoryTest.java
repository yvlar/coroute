package ca.ulaval.coroute.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.ulaval.coroute.domain.model.Utilisateur;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryUtilisateurRepositoryTest {

  private static final String NOM = "Marc Tremblay";
  private static final String EMAIL = "marc@coroute.ca";
  private static final String MOT_DE_PASSE_HASH = "hash-abc-123";

  private InMemoryUtilisateurRepository repository;

  @BeforeEach
  void setUp() {
    this.repository = new InMemoryUtilisateurRepository();
  }

  // ── save / findById ──────────────────────────────────────────────────

  @Test
  void givenUtilisateur_whenSave_thenTrouvableParId() {
    final Utilisateur utilisateur = new Utilisateur(NOM, EMAIL, MOT_DE_PASSE_HASH);

    repository.save(utilisateur);

    final Optional<Utilisateur> result = repository.findById(utilisateur.getId());
    assertTrue(result.isPresent());
    assertEquals(EMAIL, result.get().getEmail());
  }

  @Test
  void givenIdInexistant_whenFindById_thenRetourneEmpty() {
    final Optional<Utilisateur> result = repository.findById(UUID.randomUUID());
    assertFalse(result.isPresent());
  }

  // ── findByEmail ──────────────────────────────────────────────────────

  @Test
  void givenUtilisateur_whenFindByEmail_thenRetourneUtilisateur() {
    final Utilisateur utilisateur = new Utilisateur(NOM, EMAIL, MOT_DE_PASSE_HASH);

    repository.save(utilisateur);

    final Optional<Utilisateur> result = repository.findByEmail(EMAIL);
    assertTrue(result.isPresent());
    assertEquals(EMAIL, result.get().getEmail());
  }

  @Test
  void givenEmailInexistant_whenFindByEmail_thenRetourneEmpty() {
    final Optional<Utilisateur> result = repository.findByEmail("inconnu@coroute.ca");
    assertFalse(result.isPresent());
  }

  @Test
  void givenEmailMajuscules_whenFindByEmail_thenRetourneUtilisateur() {
    final Utilisateur utilisateur = new Utilisateur(NOM, EMAIL, MOT_DE_PASSE_HASH);

    repository.save(utilisateur);

    final Optional<Utilisateur> result = repository.findByEmail("MARC@COROUTE.CA");
    assertTrue(result.isPresent());
  }

  @Test
  void givenDeuxUtilisateurs_whenFindByEmail_thenRetourneLebon() {
    final Utilisateur utilisateur1 = new Utilisateur(NOM, EMAIL, MOT_DE_PASSE_HASH);
    final Utilisateur utilisateur2 = new Utilisateur("Lea B.", "lea@coroute.ca", MOT_DE_PASSE_HASH);

    repository.save(utilisateur1);
    repository.save(utilisateur2);

    final Optional<Utilisateur> result = repository.findByEmail("lea@coroute.ca");
    assertTrue(result.isPresent());
    assertEquals("lea@coroute.ca", result.get().getEmail());
  }
}
