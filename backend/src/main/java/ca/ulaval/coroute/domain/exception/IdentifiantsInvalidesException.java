package ca.ulaval.coroute.domain.exception;

public class IdentifiantsInvalidesException extends RuntimeException {
  public IdentifiantsInvalidesException() {
    super("Email ou mot de passe invalide.");
  }
}
