package io.github.yvlar.coroute.dto.request;

import io.github.yvlar.coroute.domain.model.JourSemaine;
import java.util.List;

public record MatchingRequest(String depart, String destination, List<JourSemaine> jours) {

  public MatchingRequest {
    jours = jours != null ? List.copyOf(jours) : null;
  }

  @Override
  public List<JourSemaine> jours() {
    return jours != null ? List.copyOf(jours) : null;
  }
}
