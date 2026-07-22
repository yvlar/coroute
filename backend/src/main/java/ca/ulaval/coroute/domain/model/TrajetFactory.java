package ca.ulaval.coroute.domain.model;

import ca.ulaval.coroute.dto.request.TrajetCreateRequest;
import jakarta.inject.Inject;

public class TrajetFactory {

  private final ReservationFactory reservationFactory;

  @Inject
  public TrajetFactory(final ReservationFactory reservationFactory) {
    this.reservationFactory = reservationFactory;
  }

  public ReservationFactory getReservationFactory() {
    return reservationFactory;
  }

  public Trajet creer(final String conducteurId, final TrajetCreateRequest request) {
    return new Trajet(
        conducteurId,
        request.depart(),
        request.destination(),
        request.date(),
        request.heure(),
        request.placesDisponibles(),
        request.prixParPassager(),
        request.type(),
        request.joursRecurrence(),
        request.dateDebut(),
        request.dateFin(),
        reservationFactory);
  }
}
