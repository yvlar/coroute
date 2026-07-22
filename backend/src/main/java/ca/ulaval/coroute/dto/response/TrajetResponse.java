package ca.ulaval.coroute.dto.response;

import ca.ulaval.coroute.domain.model.JourSemaine;
import ca.ulaval.coroute.domain.model.TrajetType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record TrajetResponse(
        UUID id,
        String depart,
        String destination,
        LocalDate date,
        LocalTime heure,
        int placesRestantes,
        double prixParPassager,
        String conducteurId,
        TrajetType type,
        List<JourSemaine> joursRecurrence,
        LocalDate dateDebut,
        LocalDate dateFin) {

    /**
     * Compact constructor with defensive copy of mutable list.
     */
    public TrajetResponse {
        joursRecurrence = joursRecurrence == null ? null : List.copyOf(joursRecurrence);
    }
}
