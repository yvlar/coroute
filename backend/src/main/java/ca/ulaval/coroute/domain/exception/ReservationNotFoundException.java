package ca.ulaval.coroute.domain.exception;

import java.util.UUID;

public class ReservationNotFoundException extends RuntimeException {
  public ReservationNotFoundException(final UUID reservationId) {
    super("Réservation introuvable : " + reservationId);
  }
}
