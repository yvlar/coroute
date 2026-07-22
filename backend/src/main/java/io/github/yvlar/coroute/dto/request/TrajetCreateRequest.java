package io.github.yvlar.coroute.dto.request;

import io.github.yvlar.coroute.domain.model.JourSemaine;
import io.github.yvlar.coroute.domain.model.TrajetType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record TrajetCreateRequest(
    @NotBlank String depart,
    @NotBlank String destination,
    LocalDate date,
    @NotNull LocalTime heure,
    @Min(1) @Max(8) int placesDisponibles,
    @Positive double prixParPassager,
    @NotNull TrajetType type,
    List<JourSemaine> joursRecurrence,
    LocalDate dateDebut,
    LocalDate dateFin) {

  /** Compact constructor with defensive copy of mutable list. */
  public TrajetCreateRequest {
    joursRecurrence = joursRecurrence == null ? null : List.copyOf(joursRecurrence);
  }

  @AssertTrue(message = "Le départ et la destination doivent être différents")
  public boolean isDepartDestinationValides() {
    return depart == null
        || destination == null
        || !depart.strip().equalsIgnoreCase(destination.strip());
  }

  @AssertTrue(message = "Les champs du trajet ne correspondent pas au type sélectionné")
  public boolean isConfigurationTypeValide() {
    if (type == null) {
      return true;
    }
    if (TrajetType.PONCTUEL.equals(type)) {
      return date != null
          && (joursRecurrence == null || joursRecurrence.isEmpty())
          && dateDebut == null
          && dateFin == null;
    }
    return date == null
        && joursRecurrence != null
        && !joursRecurrence.isEmpty()
        && dateDebut != null
        && dateFin != null;
  }

  @AssertTrue(message = "La date de fin doit être égale ou postérieure à la date de début")
  public boolean isPeriodeValide() {
    return dateDebut == null || dateFin == null || !dateFin.isBefore(dateDebut);
  }
}
