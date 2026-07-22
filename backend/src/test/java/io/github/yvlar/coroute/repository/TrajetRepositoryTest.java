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

  @Test
  void givenTrajetRegulierEtJourCompatible_whenFindByDate_thenRetourneTrajet() {
    repository.save(trajetFactory.creer(CONDUCTEUR_ID, REGULIER_REQUEST));

    final List<Trajet> result = repository.findByFiltres(null, null, "2026-04-03");

    assertEquals(1, result.size());
  }

  @Test
  void givenTrajetRegulierEtJourIncompatible_whenFindByDate_thenRetourneListeVide() {
    repository.save(trajetFactory.creer(CONDUCTEUR_ID, REGULIER_REQUEST));

    final List<Trajet> result = repository.findByFiltres(null, null, "2026-04-07");

    assertTrue(result.isEmpty());
  }

  @Test
  void givenTrajetExistant_whenDelete_thenPlusTrouvable() {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST);
    repository.save(trajet);
    repository.delete(trajet.getId());

    assertFalse(repository.findById(trajet.getId()).isPresent());
  }

  @Test
  void givenTrajetRegulier_whenSaveEtFindById_thenTypeEstRegulier() {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, REGULIER_REQUEST);
    repository.save(trajet);

    final Trajet result = repository.findById(trajet.getId()).orElseThrow();
    assertEquals(TrajetType.REGULIER, result.getType());
  }

  @Test
  void givenTrajetPonctuel_whenSaveEtFindById_thenTypeEstPonctuel() {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST);
    repository.save(trajet);

    final Trajet result = repository.findById(trajet.getId()).orElseThrow();
    assertEquals(TrajetType.PONCTUEL, result.getType());
  }

  @Test
  void givenPlacesDisponibles_whenReserverAtomiquement_thenDecrementeEtRetourneId() {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST);
    repository.save(trajet);

    final Optional<UUID> reservationId =
        repository.reserverAtomiquement(trajet.getId(), "passager-1", 2);

    assertTrue(reservationId.isPresent());
    assertEquals(1, repository.findById(trajet.getId()).orElseThrow().getPlacesDisponibles());
  }

  @Test
  void givenPlacesInsuffisantes_whenReserverAtomiquement_thenEmptyEtAucunChangement() {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST);
    repository.save(trajet);

    final Optional<UUID> reservationId =
        repository.reserverAtomiquement(trajet.getId(), "passager-1", 4);

    assertFalse(reservationId.isPresent());
    assertEquals(3, repository.findById(trajet.getId()).orElseThrow().getPlacesDisponibles());
  }

  @Test
  void givenTrajetInexistant_whenReserverAtomiquement_thenEmpty() {
    assertFalse(repository.reserverAtomiquement(UUID.randomUUID(), "passager-1", 1).isPresent());
  }

  @Test
  void givenReservation_whenAnnulerAtomiquement_thenRestaurePlacesUneSeuleFois() {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST);
    repository.save(trajet);
    final UUID reservationId =
        repository.reserverAtomiquement(trajet.getId(), "passager-1", 2).orElseThrow();

    final boolean premiereAnnulation =
        repository.annulerReservationAtomiquement(trajet.getId(), reservationId, "passager-1", 2);
    final boolean secondeAnnulation =
        repository.annulerReservationAtomiquement(trajet.getId(), reservationId, "passager-1", 2);

    final Trajet recharge = repository.findById(trajet.getId()).orElseThrow();
    assertTrue(premiereAnnulation);
    assertFalse(secondeAnnulation);
    assertEquals(3, recharge.getPlacesDisponibles());
    assertTrue(recharge.getReservations(CONDUCTEUR_ID).isEmpty());
  }

  @Test
  void givenReservationsConcurrentes_whenReserverAtomiquement_thenPasDeSurReservation()
      throws InterruptedException {
    final Trajet trajet = trajetFactory.creer(CONDUCTEUR_ID, PONCTUEL_REQUEST);
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
            } catch (InterruptedException exception) {
              Thread.currentThread().interrupt();
            } finally {
              fini.countDown();
            }
          });
    }

    depart.countDown();
    assertTrue(fini.await(30, TimeUnit.SECONDS));
    executor.shutdownNow();

    final int placesRestantes =
        repository.findById(trajet.getId()).orElseThrow().getPlacesDisponibles();
    assertEquals(3, reservationsReussies.get());
    assertEquals(0, placesRestantes);
  }
}
