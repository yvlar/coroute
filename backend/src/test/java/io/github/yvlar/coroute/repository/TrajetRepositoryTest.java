package io.github.yvlar.coroute.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.yvlar.coroute.domain.model.JourSemaine;
import io.github.yvlar.coroute.domain.model.ReservationFactory;
import io.github.yvlar.coroute.domain.model.Trajet;
import io.github.yvlar.coroute.domain.model.TrajetFactory;
import io.github.yvlar.coroute.domain.model.TrajetType;
import io.github.yvlar.coroute.dto.request.TrajetCreateRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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

  // ─── reserverAtomiquement ────────────────────────────────────────────

  @Test
  void givenPlacesDisponibles_whenReserverAtomiquement_thenDecrementeEtRetourneId() {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST); // 3 places
    repository.save(trajet);

    final Optional<UUID> reservationId =
        repository.reserverAtomiquement(trajet.getId(), "passager-1", 2);

    assertTrue(reservationId.isPresent());
    assertEquals(1, repository.findById(trajet.getId()).get().getPlacesDisponibles());
  }

  @Test
  void givenPlacesInsuffisantes_whenReserverAtomiquement_thenEmptyEtAucunChangement() {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST); // 3 places
    repository.save(trajet);

    final Optional<UUID> reservationId =
        repository.reserverAtomiquement(trajet.getId(), "passager-1", 4);

    assertFalse(reservationId.isPresent());
    assertEquals(3, repository.findById(trajet.getId()).get().getPlacesDisponibles());
  }

  @Test
  void givenTrajetInexistant_whenReserverAtomiquement_thenEmpty() {
    assertFalse(repository.reserverAtomiquement(UUID.randomUUID(), "passager-1", 1).isPresent());
  }

  // ─── concurrence : pas de sur-réservation ────────────────────────────

  @Test
  void givenReservationsConcurrentes_whenReserverAtomiquement_thenPasDeSurReservation()
      throws InterruptedException {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST); // 3 places
    repository.save(trajet);

    final int nombreDeThreads = 10;
    final ExecutorService executor = Executors.newFixedThreadPool(nombreDeThreads);
    final CountDownLatch depart = new CountDownLatch(1);
    final CountDownLatch fini = new CountDownLatch(nombreDeThreads);
    final AtomicInteger reservationsReussies = new AtomicInteger(0);

    for (int i = 0; i < nombreDeThreads; i++) {
      final String passagerId = "passager-" + i;
      executor.submit(
          () -> {
            try {
              depart.await();
              if (repository.reserverAtomiquement(trajet.getId(), passagerId, 1).isPresent()) {
                reservationsReussies.incrementAndGet();
              }
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            } finally {
              fini.countDown();
            }
          });
    }

    depart.countDown(); // libère tous les threads en même temps
    assertTrue(fini.await(30, TimeUnit.SECONDS), "Les réservations concurrentes n'ont pas terminé");
    executor.shutdownNow();

    final int placesRestantes = repository.findById(trajet.getId()).get().getPlacesDisponibles();
    assertEquals(
        3, reservationsReussies.get(), "Exactement 3 réservations doivent réussir (3 places)");
    assertEquals(0, placesRestantes, "Le nombre de places ne doit jamais devenir négatif");
  }
}
