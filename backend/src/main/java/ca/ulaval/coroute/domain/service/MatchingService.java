package ca.ulaval.coroute.domain.service;

import ca.ulaval.coroute.domain.model.JourSemaine;
import ca.ulaval.coroute.domain.model.Trajet;
import ca.ulaval.coroute.domain.model.TrajetType;
import ca.ulaval.coroute.dto.request.MatchingRequest;
import ca.ulaval.coroute.dto.response.MatchingResponse;
import ca.ulaval.coroute.repository.TrajetRepository;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MatchingService {

    private final TrajetRepository trajetRepository;

    @Inject
    public MatchingService(final TrajetRepository trajetRepository) {
        this.trajetRepository = trajetRepository;
    }

    public List<MatchingResponse> trouverMatches(final MatchingRequest request) {
        return this.trajetRepository.findAll().stream()
                .filter(t -> t.getPlacesDisponibles() > 0)
                .filter(t -> correspondDestination(t, request.destination()))
                .filter(t -> correspondDepart(t, request.depart()))
                .filter(t -> aJoursCompatibles(t, request.jours()))
                .map(t -> toMatchingResponse(t, request.jours()))
                .sorted(Comparator.comparingInt(MatchingResponse::scoreCompatibilite).reversed())
                .toList();
    }

    private boolean correspondDepart(final Trajet trajet, final String depart) {
        if (depart == null || depart.isBlank()) {
            return true;
        }
        return trajet.getDepart().equalsIgnoreCase(depart);
    }

    private boolean correspondDestination(final Trajet trajet, final String destination) {
        if (destination == null || destination.isBlank()) {
            return true;
        }
        return trajet.getDestination().equalsIgnoreCase(destination);
    }

    private boolean aJoursCompatibles(final Trajet trajet,
            final List<JourSemaine> joursRecherches) {
        if (joursRecherches == null || joursRecherches.isEmpty()) {
            return true;
        }
        return TrajetType.PONCTUEL.equals(trajet.getType())
                || trajet.getJoursRecurrence().stream()
                .anyMatch(joursRecherches::contains);
    }

    private List<JourSemaine> calculerJoursCompatibles(final Trajet trajet,
            final List<JourSemaine> joursRecherches) {
        if (joursRecherches == null || joursRecherches.isEmpty()
                || TrajetType.PONCTUEL.equals(trajet.getType())) {
            return trajet.getJoursRecurrence();
        }
        final List<JourSemaine> compatibles = new ArrayList<>(trajet.getJoursRecurrence());
        compatibles.retainAll(joursRecherches);
        return compatibles;
    }

    private int calculerScore(final Trajet trajet,
            final List<JourSemaine> joursRecherches) {
        int score = 0;

        // +50 si trajet régulier
        if (TrajetType.REGULIER.equals(trajet.getType())) {
            score += 50;
        }

        // +10 par jour compatible
        if (joursRecherches != null && !joursRecherches.isEmpty()) {
            final long joursCommuns = trajet.getJoursRecurrence().stream()
                    .filter(joursRecherches::contains)
                    .count();
            score += joursCommuns * 10;
        }

        // +20 si places disponibles > 1
        if (trajet.getPlacesDisponibles() > 1) {
            score += 20;
        }

        return score;
    }

    private MatchingResponse toMatchingResponse(final Trajet trajet,
            final List<JourSemaine> joursRecherches) {
        return new MatchingResponse(
                trajet.getId(),
                trajet.getDepart(),
                trajet.getDestination(),
                trajet.getHeure(),
                trajet.getPrixParPassager(),
                trajet.getPlacesDisponibles(),
                trajet.getType(),
                calculerJoursCompatibles(trajet, joursRecherches),
                calculerScore(trajet, joursRecherches),
                trajet.getConducteurId(),
                trajet.getDate(),
                trajet.getDateDebut(),
                trajet.getDateFin());
    }
}
