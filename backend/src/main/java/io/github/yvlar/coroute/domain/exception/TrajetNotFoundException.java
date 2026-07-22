package io.github.yvlar.coroute.domain.exception;

import java.util.UUID;

public class TrajetNotFoundException extends RuntimeException {
  public TrajetNotFoundException(final UUID trajetId) {
    super("Trajet introuvable : " + trajetId);
  }
}
