package ca.ulaval.coroute.domain.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReservationTest {

  private static final String PASSAGER_ID = "passager-123";
  private static final String AUTRE_ID = "autre-456";
  private static final int NOMBRE_PLACES = 2;

  private Reservation reservation;

  @BeforeEach
  void setUp() {
    this.reservation = new Reservation(PASSAGER_ID, NOMBRE_PLACES);
  }

  // ─── constructeur ───────────────────────────────────────────────────

  @Test
  void givenParametresValides_whenCreerReservation_thenAttributsCorrects() {
    assertAll(
        () -> assertNotNull(reservation.getId()),
        () -> assertEquals(PASSAGER_ID, reservation.getPassagerId()),
        () -> assertEquals(NOMBRE_PLACES, reservation.getNombrePlaces()));
  }

  @Test
  void givenDeuxReservations_whenCreer_thenIdsUniques() {
    final Reservation autre = new Reservation(PASSAGER_ID, NOMBRE_PLACES);
    assertFalse(reservation.getId().equals(autre.getId()));
  }

  // ─── appartientA ────────────────────────────────────────────────────

  @Test
  void givenBonPassager_whenAppartientA_thenRetourneTrue() {
    assertTrue(reservation.appartientA(PASSAGER_ID));
  }

  @Test
  void givenMauvaisPassager_whenAppartientA_thenRetourneFalse() {
    assertFalse(reservation.appartientA(AUTRE_ID));
  }
}
