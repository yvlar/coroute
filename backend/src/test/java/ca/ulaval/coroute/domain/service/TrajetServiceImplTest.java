package ca.ulaval.coroute.domain.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.coroute.domain.exception.TrajetNotFoundException;
import ca.ulaval.coroute.domain.model.JourSemaine;
import ca.ulaval.coroute.domain.model.Reservation;
import ca.ulaval.coroute.domain.model.Trajet;
import ca.ulaval.coroute.domain.model.TrajetFactory;
import ca.ulaval.coroute.domain.model.TrajetType;
import ca.ulaval.coroute.dto.request.ReservationCreateRequest;
import ca.ulaval.coroute.dto.request.TrajetCreateRequest;
import ca.ulaval.coroute.dto.response.ReservationResponse;
import ca.ulaval.coroute.dto.response.TrajetResponse;
import ca.ulaval.coroute.repository.TrajetRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TrajetServiceImplTest {

  private static final UUID TRAJET_ID = UUID.randomUUID();
  private static final UUID RESERVATION_ID = UUID.randomUUID();
  private static final String CONDUCTEUR_ID = "conducteur-123";
  private static final String PASSAGER_ID = "passager-456";

  private static final TrajetCreateRequest TRAJET_PONCTUEL_REQUEST = new TrajetCreateRequest(
      "Québec",
      "Montréal",
      LocalDate.of(2026, 4, 1),
      LocalTime.of(8, 30),
      3,
      20.0,
      TrajetType.PONCTUEL,
      null,
      null,
      null);

  private static final TrajetCreateRequest TRAJET_REGULIER_REQUEST = new TrajetCreateRequest(
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

  private static final ReservationCreateRequest RESERVATION_CREATE_REQUEST = new ReservationCreateRequest(1);

  @Mock
  private TrajetRepository trajetRepository;
  @Mock
  private TrajetFactory trajetFactory;
  @Mock
  private Trajet trajetMock;
  @Mock
  private Reservation reservationMock;

  @InjectMocks
  private TrajetServiceImpl trajetService;

  @BeforeEach
  void setUp() {
    when(trajetMock.getId()).thenReturn(TRAJET_ID);
    when(trajetMock.getDepart()).thenReturn("Québec");
    when(trajetMock.getDestination()).thenReturn("Montréal");
    when(trajetMock.getDate()).thenReturn(LocalDate.of(2026, 4, 1));
    when(trajetMock.getHeure()).thenReturn(LocalTime.of(8, 30));
    when(trajetMock.getPlacesDisponibles()).thenReturn(3);
    when(trajetMock.getPrixParPassager()).thenReturn(20.0);
    when(trajetMock.getConducteurId()).thenReturn(CONDUCTEUR_ID);
    when(trajetMock.getType()).thenReturn(TrajetType.PONCTUEL);
    when(trajetMock.getJoursRecurrence()).thenReturn(List.of());
    when(trajetMock.getDateDebut()).thenReturn(null);
    when(trajetMock.getDateFin()).thenReturn(null);
  }

  // ─── findAll ────────────────────────────────────────────────────────

  @Test
  void givenTrajets_whenFindAll_thenRetourneListeDeResponses() {
    when(trajetRepository.findByFiltres(null, null, null)).thenReturn(List.of(trajetMock));

    final List<TrajetResponse> result = trajetService.findAll(null, null, null);

    assertAll(
        () -> assertEquals(1, result.size()), () -> assertEquals(TRAJET_ID, result.get(0).id()));
  }

  @Test
  void givenAucunTrajet_whenFindAll_thenRetourneListeVide() {
    when(trajetRepository.findByFiltres(null, null, null)).thenReturn(List.of());

    final List<TrajetResponse> result = trajetService.findAll(null, null, null);

    assertEquals(0, result.size());
  }

  // ─── findById ───────────────────────────────────────────────────────

  @Test
  void givenTrajetExistant_whenFindById_thenRetourneResponse() {
    when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.of(trajetMock));

    final TrajetResponse result = trajetService.findById(TRAJET_ID);

    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(TRAJET_ID, result.id()),
        () -> assertEquals("Québec", result.depart()));
  }

  @Test
  void givenTrajetInexistant_whenFindById_thenLanceTrajetNotFoundException() {
    when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.empty());

    assertThrows(TrajetNotFoundException.class, () -> trajetService.findById(TRAJET_ID));
  }

  // ─── createTrajet ───────────────────────────────────────────────────

  @Test
  void givenTrajetPonctuelValide_whenCreateTrajet_thenRetourneUUID() {
    when(trajetFactory.creer(CONDUCTEUR_ID, TRAJET_PONCTUEL_REQUEST)).thenReturn(trajetMock);

    final UUID result = trajetService.createTrajet(CONDUCTEUR_ID, TRAJET_PONCTUEL_REQUEST);

    assertEquals(TRAJET_ID, result);
    verify(trajetRepository).save(trajetMock);
  }

  @Test
  void givenTrajetRegulierValide_whenCreateTrajet_thenRetourneUUID() {
    when(trajetMock.getType()).thenReturn(TrajetType.REGULIER);
    when(trajetMock.getJoursRecurrence())
        .thenReturn(List.of(JourSemaine.LUNDI, JourSemaine.VENDREDI));
    when(trajetFactory.creer(CONDUCTEUR_ID, TRAJET_REGULIER_REQUEST)).thenReturn(trajetMock);

    final UUID result = trajetService.createTrajet(CONDUCTEUR_ID, TRAJET_REGULIER_REQUEST);

    assertEquals(TRAJET_ID, result);
    verify(trajetRepository).save(trajetMock);
  }

  // ─── delete ─────────────────────────────────────────────────────────

  @Test
  void givenTrajetExistant_whenDelete_thenRepositoryDeleteAppele() {
    when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.of(trajetMock));

    trajetService.delete(TRAJET_ID, CONDUCTEUR_ID);

    verify(trajetMock).verifierProprietaire(CONDUCTEUR_ID);
    verify(trajetRepository).delete(TRAJET_ID);
  }

  @Test
  void givenTrajetInexistant_whenDelete_thenLanceTrajetNotFoundException() {
    when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.empty());

    assertThrows(
        TrajetNotFoundException.class, () -> trajetService.delete(TRAJET_ID, CONDUCTEUR_ID));
  }

  // ─── addReservation ─────────────────────────────────────────────────

  @Test
  void givenTrajetExistant_whenAddReservation_thenRetourneReservationId() {
    when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.of(trajetMock));
    when(trajetMock.ajouterReservation(PASSAGER_ID, 1)).thenReturn(RESERVATION_ID);

    final UUID result = trajetService.addReservation(TRAJET_ID, PASSAGER_ID, RESERVATION_CREATE_REQUEST);

    assertEquals(RESERVATION_ID, result);
    verify(trajetRepository).save(trajetMock);
  }

  @Test
  void givenTrajetInexistant_whenAddReservation_thenLanceTrajetNotFoundException() {
    when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.empty());

    assertThrows(
        TrajetNotFoundException.class,
        () -> trajetService.addReservation(TRAJET_ID, PASSAGER_ID, RESERVATION_CREATE_REQUEST));
  }

  // ─── cancelReservation ──────────────────────────────────────────────

  @Test
  void givenTrajetExistant_whenCancelReservation_thenAnnulerAppele() {
    when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.of(trajetMock));

    trajetService.cancelReservation(TRAJET_ID, RESERVATION_ID, PASSAGER_ID);

    verify(trajetMock).annulerReservation(RESERVATION_ID, PASSAGER_ID);
    verify(trajetRepository).save(trajetMock);
  }

  @Test
  void givenTrajetInexistant_whenCancelReservation_thenLanceTrajetNotFoundException() {
    when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.empty());

    assertThrows(
        TrajetNotFoundException.class,
        () -> trajetService.cancelReservation(TRAJET_ID, RESERVATION_ID, PASSAGER_ID));
  }

  // ─── getReservations ────────────────────────────────────────────────

  @Test
  void givenTrajetExistant_whenGetReservations_thenRetourneListeResponses() {
    when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.of(trajetMock));
    when(trajetMock.getReservations(CONDUCTEUR_ID)).thenReturn(List.of(reservationMock));
    when(reservationMock.getId()).thenReturn(RESERVATION_ID);
    when(reservationMock.getPassagerId()).thenReturn(PASSAGER_ID);
    when(reservationMock.getNombrePlaces()).thenReturn(1);

    final List<ReservationResponse> result = trajetService.getReservations(TRAJET_ID, CONDUCTEUR_ID);

    assertAll(
        () -> assertEquals(1, result.size()),
        () -> assertEquals(RESERVATION_ID, result.get(0).id()),
        () -> assertEquals(PASSAGER_ID, result.get(0).passagerId()));
  }

  @Test
  void givenTrajetInexistant_whenGetReservations_thenLanceTrajetNotFoundException() {
    when(trajetRepository.findById(TRAJET_ID)).thenReturn(Optional.empty());

    assertThrows(
        TrajetNotFoundException.class,
        () -> trajetService.getReservations(TRAJET_ID, CONDUCTEUR_ID));
  }
}
