package ca.ulaval.coroute.domain.exception;

public class AccesInterditException extends RuntimeException {
  public AccesInterditException(final String action) {
    super(String.format(
        "Accès interdit : vous n'êtes pas autorisé à effectuer l'action '%s'.", action));
  }
}
