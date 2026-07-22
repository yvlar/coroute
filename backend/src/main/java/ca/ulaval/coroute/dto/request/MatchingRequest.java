package ca.ulaval.coroute.dto.request;

import ca.ulaval.coroute.domain.model.JourSemaine;

import java.util.List;

public record MatchingRequest(
        String depart,
        String destination,
        List<JourSemaine> jours) {

    public MatchingRequest {
        jours = jours != null ? List.copyOf(jours) : null;
    }

    @Override
    public List<JourSemaine> jours() {
        return jours != null ? List.copyOf(jours) : null;
    }
}
