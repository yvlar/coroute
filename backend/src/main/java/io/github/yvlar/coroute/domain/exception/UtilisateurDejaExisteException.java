package io.github.yvlar.coroute.domain.exception;

public class UtilisateurDejaExisteException extends RuntimeException {
  public UtilisateurDejaExisteException(final String email) {
    super("Un utilisateur existe déjà avec l'email : " + email);
  }

  public UtilisateurDejaExisteException(final String email, final Throwable cause) {
    super("Un utilisateur existe déjà avec l'email : " + email, cause);
  }
}
