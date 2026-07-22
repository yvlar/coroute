package io.github.yvlar.coroute.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.yvlar.coroute.domain.exception.UtilisateurDejaExisteException;
import io.github.yvlar.coroute.domain.model.Utilisateur;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class UtilisateurRepositoryTest {

  private static final String NOM = "Marc Tremblay";
  private static final String EMAIL = "marc@coroute.ca";
  private static final String MOT_DE_PASSE_HASH = "hash-abc-123";

  protected UtilisateurRepository repository;

  protected abstract UtilisateurRepository createUtilisateurRepository();

  @BeforeEach
  void setUp() {
    this.repository = createUtilisateurRepository();
  }

  @Test
  void givenUtilisateur_whenSave_thenTrouvableParId() {
    final Utilisateur utilisateur = new Utilisateur(NOM, EMAIL, MOT_DE_PASSE_HASH);

    repository.save(utilisateur);

    final Optional<Utilisateur> result = repository.findById(utilisateur.getId());
    assertTrue(result.isPresent());
  }

  @Test
  void givenIdInexistant_whenFindById_thenRetourneEmpty() {
    final Optional<Utilisateur> result = repository.findById(UUID.randomUUID());
    assertFalse(result.isPresent());
  }

  @Test
  void givenUtilisateur_whenFindByEmail_thenRetourneUtilisateur() {
    final Utilisateur utilisateur = new Utilisateur(NOM, EMAIL, MOT_DE_PASSE_HASH);

    repository.save(utilisateur);

    final Optional<Utilisateur> result = repository.findByEmail(EMAIL);
    assertTrue(result.isPresent());
    assertEquals(EMAIL, result.orElseThrow().getEmail());
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
    assertEquals("lea@coroute.ca", result.orElseThrow().getEmail());
  }

  @Test
  void givenCourrielDejaUtilise_whenSave_thenLanceUtilisateurDejaExisteException() {
    repository.save(new Utilisateur(NOM, EMAIL, MOT_DE_PASSE_HASH));

    final Utilisateur doublon =
        new Utilisateur("Autre nom", " MARC@COROUTE.CA ", MOT_DE_PASSE_HASH);

    assertThrows(UtilisateurDejaExisteException.class, () -> repository.save(doublon));
  }
}
