package ca.ulaval.coroute.domain.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ca.ulaval.coroute.domain.exception.AccesInterditException;
import ca.ulaval.coroute.domain.exception.PlacesInsuffisantesException;
import ca.ulaval.coroute.domain.exception.ReservationNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TrajetTest {

  private static final String CONDUCTEUR_ID = "conducteur-123";
  private static final String PASSAGER_ID = "passager-456";
  private static final String AUTRE_ID = "autre-789";
  private static final int PLACES = 3;
  private static final UUID RESERVATION_UUID = UUID.randomUUID();

  @Mock private ReservationFactory reservationFactory;
  @Mock private Reservation reservationMock;

  private Trajet trajetPonctuel;
  private Trajet trajetRegulier;

  @BeforeEach
  void setUp() {
    this.trajetPonctuel =
        new Trajet(
            CONDUCTEUR_ID,
            "Québec",
            "Montréal",
            LocalDate.of(2026, 4, 1),
            LocalTime.of(8, 30),
            PLACES,
            20.0,
            TrajetType.PONCTUEL,
            null,
            null,
            null,
            reservationFactory);
    this.trajetRegulier =
        new Trajet(
            CONDUCTEUR_ID,
            "Roxton",
            "Drummondville",
            null,
            LocalTime.of(7, 15),
            2,
            8.0,
            TrajetType.REGULIER,
            List.of(
                JourSemaine.LUNDI,
                JourSemaine.MARDI,
                JourSemaine.MERCREDI,
                JourSemaine.JEUDI,
                JourSemaine.VENDREDI),
            LocalDate.of(2026, 4, 1),
            LocalDate.of(2026, 6, 30),
            reservationFactory);
  }

  @Test
  void givenTrajetPonctuel_whenEstRegulier_thenRetourneFalse() {
    assertFalse(trajetPonctuel.estRegulier());
  }

  @Test
  void givenTrajetRegulier_whenEstRegulier_thenRetourneTrue() {
    assertTrue(trajetRegulier.estRegulier());
  }

  @Test
  void givenTrajetRegulier_whenGetType_thenRetourneREGULIER() {
    assertEquals(TrajetType.REGULIER, trajetRegulier.getType());
  }

  @Test
  void givenTrajetPonctuel_whenGetType_thenRetournePONCTUEL() {
    assertEquals(TrajetType.PONCTUEL, trajetPonctuel.getType());
  }

  @Test
  void givenTrajetRegulier_whenGetJoursRecurrence_thenRetourneJours() {
    assertAll(
        () -> assertEquals(5, trajetRegulier.getJoursRecurrence().size()),
        () -> assertTrue(trajetRegulier.getJoursRecurrence().contains(JourSemaine.LUNDI)),
        () -> assertTrue(trajetRegulier.getJoursRecurrence().contains(JourSemaine.VENDREDI)));
  }

  @Test
  void givenTrajetPonctuel_whenGetJoursRecurrence_thenRetourneListeVide() {
    assertTrue(trajetPonctuel.getJoursRecurrence().isEmpty());
  }

  @Test
  void givenTrajetRegulier_whenGetDateDebut_thenRetourneDate() {
    assertEquals(LocalDate.of(2026, 4, 1), trajetRegulier.getDateDebut());
  }

  @Test
  void givenTrajetRegulier_whenGetDateFin_thenRetourneDate() {
    assertEquals(LocalDate.of(2026, 6, 30), trajetRegulier.getDateFin());
  }

  @Test
  void givenPlacesDisponibles_whenAjouterReservation_thenRetourneUUID() {
    when(reservationFactory.creer(PASSAGER_ID, 1)).thenReturn(reservationMock);
    when(reservationMock.getId()).thenReturn(RESERVATION_UUID);
    final UUID id = trajetPonctuel.ajouterReservation(PASSAGER_ID, 1);
    assertNotNull(id);
    assertEquals(RESERVATION_UUID, id);
  }

  @Test
  void givenPlacesDisponibles_whenAjouterReservation_thenPlacesDiminuent() {
    when(reservationFactory.creer(PASSAGER_ID, 2)).thenReturn(reservationMock);
    when(reservationMock.getId()).thenReturn(RESERVATION_UUID);
    trajetPonctuel.ajouterReservation(PASSAGER_ID, 2);
    assertEquals(1, trajetPonctuel.getPlacesDisponibles());
  }

  @Test
  void givenPlacesInsuffisantes_whenAjouterReservation_thenLancePlacesInsuffisantesException() {
    assertThrows(
        PlacesInsuffisantesException.class,
        () -> trajetPonctuel.ajouterReservation(PASSAGER_ID, 10));
  }

  @Test
  void givenReservationExistante_whenAnnulerReservation_thenPlacesAugmentent() {
    when(reservationFactory.creer(PASSAGER_ID, 2)).thenReturn(reservationMock);
    when(reservationMock.getId()).thenReturn(RESERVATION_UUID);
    when(reservationMock.appartientA(PASSAGER_ID)).thenReturn(true);
    when(reservationMock.getNombrePlaces()).thenReturn(2);
    trajetPonctuel.ajouterReservation(PASSAGER_ID, 2);
    trajetPonctuel.annulerReservation(RESERVATION_UUID, PASSAGER_ID);
    assertEquals(PLACES, trajetPonctuel.getPlacesDisponibles());
  }

  @Test
  void givenMauvaisPassager_whenAnnulerReservation_thenLanceAccesInterditException() {
    when(reservationFactory.creer(PASSAGER_ID, 1)).thenReturn(reservationMock);
    when(reservationMock.getId()).thenReturn(RESERVATION_UUID);
    when(reservationMock.appartientA(AUTRE_ID)).thenReturn(false);
    trajetPonctuel.ajouterReservation(PASSAGER_ID, 1);
    assertThrows(
        AccesInterditException.class,
        () -> trajetPonctuel.annulerReservation(RESERVATION_UUID, AUTRE_ID));
  }

  @Test
  void givenReservationInexistante_whenAnnulerReservation_thenLanceReservationNotFoundException() {
    assertThrows(
        ReservationNotFoundException.class,
        () -> trajetPonctuel.annulerReservation(UUID.randomUUID(), PASSAGER_ID));
  }

  @Test
  void givenBonConducteur_whenVerifierProprietaire_thenAucuneException() {
    trajetPonctuel.verifierProprietaire(CONDUCTEUR_ID);
  }

  @Test
  void givenMauvaisConducteur_whenVerifierProprietaire_thenLanceAccesInterditException() {
    assertThrows(AccesInterditException.class, () -> trajetPonctuel.verifierProprietaire(AUTRE_ID));
  }
}
