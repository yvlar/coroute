package ca.ulaval.coroute.domain.exception;

public class PlacesInsuffisantesException extends RuntimeException {
  public PlacesInsuffisantesException(final int placesDisponibles, final int placesDemandees) {
    super(String.format(
        "Places insuffisantes : %d demandée(s), mais seulement %d disponible(s) pour ce trajet.",
        placesDemandees, placesDisponibles));
  }
}
