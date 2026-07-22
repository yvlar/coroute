package ca.ulaval.coroute.dto.request;

import ca.ulaval.coroute.domain.model.JourSemaine;
import ca.ulaval.coroute.domain.model.TrajetType;
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
        @Min(1) int placesDisponibles,
        @Positive double prixParPassager,
        @NotNull TrajetType type,
        List<JourSemaine> joursRecurrence,
        LocalDate dateDebut,
        LocalDate dateFin) {

    /**
     * Compact constructor with defensive copy of mutable list.
     */
    public TrajetCreateRequest {
        joursRecurrence = joursRecurrence == null ? null : List.copyOf(joursRecurrence);
    }
}
