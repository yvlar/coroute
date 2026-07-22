package io.github.yvlar.coroute.domain.exception;

public class IdentifiantsInvalidesException extends RuntimeException {
  public IdentifiantsInvalidesException() {
    super("Email ou mot de passe invalide.");
  }
}
