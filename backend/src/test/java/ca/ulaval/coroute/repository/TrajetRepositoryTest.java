package ca.ulaval.coroute.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.ulaval.coroute.domain.model.JourSemaine;
import ca.ulaval.coroute.domain.model.ReservationFactory;
import ca.ulaval.coroute.domain.model.Trajet;
import ca.ulaval.coroute.domain.model.TrajetFactory;
import ca.ulaval.coroute.domain.model.TrajetType;
import ca.ulaval.coroute.dto.request.TrajetCreateRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class TrajetRepositoryTest {

  private static final String CONDUCTEUR_ID = "conducteur-123";

  private static final TrajetCreateRequest PONCTUEL_REQUEST =
      new TrajetCreateRequest(
          "Quebec",
          "Montreal",
          LocalDate.of(2026, 4, 1),
          LocalTime.of(8, 30),
          3,
          20.0,
          TrajetType.PONCTUEL,
          null,
          null,
          null);

  private static final TrajetCreateRequest REGULIER_REQUEST =
      new TrajetCreateRequest(
          "Roxton",
          "Drummondville",
          null,
          LocalTime.of(7, 15),
          2,
          8.0,
          TrajetType.REGULIER,
          List.of(JourSemaine.LUNDI, JourSemaine.VENDREDI),
          LocalDate.of(2026, 4, 1),
          LocalDate.of(2026, 6, 30));

  protected TrajetRepository repository;
  protected TrajetFactory trajetFactory;

  protected abstract TrajetRepository createTrajetRepository();

  @BeforeEach
  void setUp() {
    this.trajetFactory = new TrajetFactory(new ReservationFactory());
    this.repository = createTrajetRepository();
  }

  // ─── save / findById ────────────────────────────────────────────────

  @Test
  void givenTrajetPonctuel_whenSave_thenTrouvableParId() {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST);
    repository.save(trajet);

    final Optional<Trajet> result = repository.findById(trajet.getId());
    assertTrue(result.isPresent());
  }

  @Test
  void givenTrajetRegulier_whenSave_thenTrouvableParId() {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, REGULIER_REQUEST);
    repository.save(trajet);

    final Optional<Trajet> result = repository.findById(trajet.getId());
    assertTrue(result.isPresent());
  }

  @Test
  void givenIdInexistant_whenFindById_thenRetourneEmpty() {
    final Optional<Trajet> result = repository.findById(UUID.randomUUID());
    assertFalse(result.isPresent());
  }

  // ─── findAll ────────────────────────────────────────────────────────

  @Test
  void givenTrajetsEnregistres_whenFindAll_thenRetourneTous() {
    repository.save(trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST));
    repository.save(trajetFactory.creer(CONDUCTEUR_ID, REGULIER_REQUEST));

    assertEquals(2, repository.findAll().size());
  }

  @Test
  void givenAucunTrajet_whenFindAll_thenRetourneListeVide() {
    assertTrue(repository.findAll().isEmpty());
  }

  // ─── findByFiltres ───────────────────────────────────────────────────

  @Test
  void givenFiltreDepart_whenFindByFiltres_thenRetourneTrajetsCorrespondants() {
    repository.save(trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST));

    final List<Trajet> result = repository.findByFiltres("Quebec", null, null);

    assertEquals(1, result.size());
  }

  @Test
  void givenFiltreNonCorrespondant_whenFindByFiltres_thenRetourneListeVide() {
    repository.save(trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST));

    final List<Trajet> result = repository.findByFiltres("Sherbrooke", null, null);

    assertTrue(result.isEmpty());
  }

  // ─── delete ─────────────────────────────────────────────────────────

  @Test
  void givenTrajetExistant_whenDelete_thenPlusTrouvable() {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST);
    repository.save(trajet);
    repository.delete(trajet.getId());

    assertFalse(repository.findById(trajet.getId()).isPresent());
  }

  // ─── type ────────────────────────────────────────────────────────────

  @Test
  void givenTrajetRegulier_whenSaveEtFindById_thenTypeEstRegulier() {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, REGULIER_REQUEST);
    repository.save(trajet);

    final Trajet result = repository.findById(trajet.getId()).get();
    assertEquals(TrajetType.REGULIER, result.getType());
  }

  @Test
  void givenTrajetPonctuel_whenSaveEtFindById_thenTypeEstPonctuel() {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST);
    repository.save(trajet);

    final Trajet result = repository.findById(trajet.getId()).get();
    assertEquals(TrajetType.PONCTUEL, result.getType());
  }
}
