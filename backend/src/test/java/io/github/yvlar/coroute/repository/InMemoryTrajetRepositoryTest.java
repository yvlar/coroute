package io.github.yvlar.coroute.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class InMemoryTrajetRepositoryTest {

  private static final UUID TRAJET_ID = UUID.randomUUID();
  private static final String DEPART = "Québec";
  private static final String DESTINATION = "Montréal";
  private static final String DATE = "2026-04-01";

  @Mock private Trajet trajetMock;
  @Mock private Trajet trajetMock2;

  private InMemoryTrajetRepository repository;

  @BeforeEach
  void setUp() {
    this.repository = new InMemoryTrajetRepository();
  }

  // ─── save / findById ────────────────────────────────────────────────

  @Test
  void givenTrajet_whenSave_thenTrouvableParId() {
    when(trajetMock.getId()).thenReturn(TRAJET_ID);

    repository.save(trajetMock);

    final Optional<Trajet> result = repository.findById(TRAJET_ID);
    assertTrue(result.isPresent());
    assertEquals(trajetMock, result.get());
  }

  @Test
  void givenIdInexistant_whenFindById_thenRetourneEmpty() {
    final Optional<Trajet> result = repository.findById(UUID.randomUUID());
    assertFalse(result.isPresent());
  }

  // ─── findAll ────────────────────────────────────────────────────────

  @Test
  void givenTrajetsEnregistres_whenFindAll_thenRetourneTous() {
    when(trajetMock.getId()).thenReturn(TRAJET_ID);
    when(trajetMock2.getId()).thenReturn(UUID.randomUUID());

    repository.save(trajetMock);
    repository.save(trajetMock2);

    assertEquals(2, repository.findAll().size());
  }

  @Test
  void givenAucunTrajet_whenFindAll_thenRetourneListeVide() {
    assertTrue(repository.findAll().isEmpty());
  }

  // ─── findByFiltres ───────────────────────────────────────────────────

  @Test
  void givenFiltreDepart_whenFindByFiltres_thenRetourneTrajetsCorrespondants() {
    when(trajetMock.getId()).thenReturn(TRAJET_ID);
    when(trajetMock.getDepart()).thenReturn(DEPART);

    repository.save(trajetMock);

    final List<Trajet> result = repository.findByFiltres(DEPART, null, null);
    assertAll(() -> assertEquals(1, result.size()), () -> assertEquals(trajetMock, result.get(0)));
  }

  @Test
  void givenFiltreDestination_whenFindByFiltres_thenRetourneTrajetsCorrespondants() {
    when(trajetMock.getId()).thenReturn(TRAJET_ID);
    when(trajetMock.getDestination()).thenReturn(DESTINATION);

    repository.save(trajetMock);

    final List<Trajet> result = repository.findByFiltres(null, DESTINATION, null);
    assertEquals(1, result.size());
  }

  @Test
  void givenFiltreDate_whenFindByFiltres_thenRetourneTrajetsCorrespondants() {
    when(trajetMock.getId()).thenReturn(TRAJET_ID);
    when(trajetMock.getDate()).thenReturn(LocalDate.parse(DATE));

    repository.save(trajetMock);

    final List<Trajet> result = repository.findByFiltres(null, null, DATE);
    assertEquals(1, result.size());
  }

  @Test
  void givenTousFiltres_whenFindByFiltres_thenRetourneTrajetExact() {
    when(trajetMock.getId()).thenReturn(TRAJET_ID);
    when(trajetMock.getDepart()).thenReturn(DEPART);
    when(trajetMock.getDestination()).thenReturn(DESTINATION);
    when(trajetMock.getDate()).thenReturn(LocalDate.parse(DATE));

    repository.save(trajetMock);

    final List<Trajet> result = repository.findByFiltres(DEPART, DESTINATION, DATE);
    assertEquals(1, result.size());
  }

  @Test
  void givenFiltreNonCorrespondant_whenFindByFiltres_thenRetourneListeVide() {
    when(trajetMock.getId()).thenReturn(TRAJET_ID);
    when(trajetMock.getDepart()).thenReturn(DEPART);

    repository.save(trajetMock);

    final List<Trajet> result = repository.findByFiltres("Sherbrooke", null, null);
    assertTrue(result.isEmpty());
  }

  // ─── delete ─────────────────────────────────────────────────────────

  @Test
  void givenTrajetExistant_whenDelete_thenPlusTrouvable() {
    when(trajetMock.getId()).thenReturn(TRAJET_ID);

    repository.save(trajetMock);
    repository.delete(TRAJET_ID);

    assertFalse(repository.findById(TRAJET_ID).isPresent());
  }

  @Test
  void givenTrajetExistant_whenDelete_thenFindAllRetourneMoins() {
    when(trajetMock.getId()).thenReturn(TRAJET_ID);

    repository.save(trajetMock);
    repository.delete(TRAJET_ID);

    assertTrue(repository.findAll().isEmpty());
  }

  // ─── reserverAtomiquement / concurrence ──────────────────────────────

  @Test
  void givenReservationsConcurrentes_whenReserverAtomiquement_thenPasDeSurReservation()
      throws InterruptedException {
    final TrajetFactory trajetFactory = new TrajetFactory(new ReservationFactory());
    final Trajet trajet = trajetFactory.creer("conducteur-1", requeteAvecPlaces(3));
    repository.save(trajet);

    final int nombreDeThreads = 10;
    final ExecutorService executor = Executors.newFixedThreadPool(nombreDeThreads);
    final CountDownLatch depart = new CountDownLatch(1);
    final CountDownLatch fini = new CountDownLatch(nombreDeThreads);
    final AtomicInteger reussies = new AtomicInteger(0);

    for (int i = 0; i < nombreDeThreads; i++) {
      final String passagerId = "passager-" + i;
      executor.submit(
          () -> {
            try {
              depart.await();
              if (repository.reserverAtomiquement(trajet.getId(), passagerId, 1).isPresent()) {
                reussies.incrementAndGet();
              }
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            } finally {
              fini.countDown();
            }
          });
    }

    depart.countDown();
    assertTrue(fini.await(30, TimeUnit.SECONDS));
    executor.shutdownNow();

    assertAll(
        () -> assertEquals(3, reussies.get()),
        () -> assertEquals(0, repository.findById(trajet.getId()).get().getPlacesDisponibles()));
  }

  private static TrajetCreateRequest requeteAvecPlaces(final int places) {
    return new TrajetCreateRequest(
        DEPART,
        DESTINATION,
        LocalDate.parse(DATE),
        LocalTime.of(8, 30),
        places,
        20.0,
        TrajetType.PONCTUEL,
        null,
        null,
        null);
  }
}
