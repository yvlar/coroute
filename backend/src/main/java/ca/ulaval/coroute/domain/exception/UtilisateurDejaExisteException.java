package ca.ulaval.coroute.domain.exception;

public class UtilisateurDejaExisteException extends RuntimeException {
  public UtilisateurDejaExisteException(final String email) {
    super("Un utilisateur existe déjà avec l'email : " + email);
  }
}
