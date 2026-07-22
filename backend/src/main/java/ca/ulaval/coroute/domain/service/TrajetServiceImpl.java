package ca.ulaval.coroute.domain.service;

import ca.ulaval.coroute.domain.exception.TrajetNotFoundException;
import ca.ulaval.coroute.domain.model.Trajet;
import ca.ulaval.coroute.domain.model.TrajetFactory;
import ca.ulaval.coroute.dto.request.ReservationCreateRequest;
import ca.ulaval.coroute.dto.request.TrajetCreateRequest;
import ca.ulaval.coroute.dto.response.ReservationResponse;
import ca.ulaval.coroute.dto.response.TrajetResponse;
import ca.ulaval.coroute.repository.TrajetRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

public class TrajetServiceImpl implements TrajetService {

  private final TrajetRepository trajetRepository;
  private final TrajetFactory trajetFactory;

  @Inject
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Injected by DI framework")
  public TrajetServiceImpl(
      final TrajetRepository trajetRepository, final TrajetFactory trajetFactory) {
    this.trajetRepository = trajetRepository;
    this.trajetFactory = trajetFactory;
  }

  @Override
  public List<TrajetResponse> findAll(
      final String depart, final String destination, final String date) {
    return this.trajetRepository.findByFiltres(depart, destination, date).stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public TrajetResponse findById(final UUID trajetId) {
    final Trajet trajet = this.trouverTrajet(trajetId);
    return this.toResponse(trajet);
  }

  @Override
  public UUID createTrajet(final String conducteurId, final TrajetCreateRequest request) {
    final Trajet trajet = this.trajetFactory.creer(conducteurId, request);
    this.trajetRepository.save(trajet);
    return trajet.getId();
  }

  @Override
  public void delete(final UUID trajetId, final String conducteurId) {
    final Trajet trajet = this.trouverTrajet(trajetId);
    trajet.verifierProprietaire(conducteurId);
    this.trajetRepository.delete(trajetId);
  }

  @Override
  public UUID addReservation(
      final UUID trajetId, final String passagerId, final ReservationCreateRequest request) {
    final Trajet trajet = this.trouverTrajet(trajetId);
    final UUID reservationId = trajet.ajouterReservation(passagerId, request.nombrePlaces());
    this.trajetRepository.save(trajet);
    return reservationId;
  }

  @Override
  public void cancelReservation(
      final UUID trajetId, final UUID reservationId, final String passagerId) {
    final Trajet trajet = this.trouverTrajet(trajetId);
    trajet.annulerReservation(reservationId, passagerId);
    this.trajetRepository.save(trajet);
  }

  @Override
  public List<ReservationResponse> getReservations(final UUID trajetId, final String conducteurId) {
    final Trajet trajet = this.trouverTrajet(trajetId);
    return trajet.getReservations(conducteurId).stream()
        .map(r -> new ReservationResponse(r.getId(), r.getPassagerId(), r.getNombrePlaces()))
        .toList();
  }

  private Trajet trouverTrajet(final UUID trajetId) {
    return this.trajetRepository
        .findById(trajetId)
        .orElseThrow(() -> new TrajetNotFoundException(trajetId));
  }

  private TrajetResponse toResponse(final Trajet trajet) {
    return new TrajetResponse(
        trajet.getId(),
        trajet.getDepart(),
        trajet.getDestination(),
        trajet.getDate(),
        trajet.getHeure(),
        trajet.getPlacesDisponibles(),
        trajet.getPrixParPassager(),
        trajet.getConducteurId(),
        trajet.getType(),
        trajet.getJoursRecurrence(),
        trajet.getDateDebut(),
        trajet.getDateFin());
  }
}
