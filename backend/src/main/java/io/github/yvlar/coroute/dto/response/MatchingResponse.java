package io.github.yvlar.coroute.dto.response;

import io.github.yvlar.coroute.domain.model.JourSemaine;
import io.github.yvlar.coroute.domain.model.TrajetType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record MatchingResponse(
    UUID id,
    String depart,
    String destination,
    LocalTime heure,
    double prixParPassager,
    int placesRestantes,
    TrajetType type,
    List<JourSemaine> joursCompatibles,
    int scoreCompatibilite,
    String conducteurId,
    LocalDate date,
    LocalDate dateDebut,
    LocalDate dateFin) {

  public MatchingResponse {
    joursCompatibles = joursCompatibles != null ? List.copyOf(joursCompatibles) : null;
  }

  @Override
  public List<JourSemaine> joursCompatibles() {
    return joursCompatibles != null ? List.copyOf(joursCompatibles) : null;
  }
}
