package ca.ulaval.coroute.domain.model;

public class ReservationFactory {

  public Reservation creer(final String passagerId, final int nombrePlaces) {
    return new Reservation(passagerId, nombrePlaces);
  }
}
