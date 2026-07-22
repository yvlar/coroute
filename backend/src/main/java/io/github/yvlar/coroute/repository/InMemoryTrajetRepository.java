package io.github.yvlar.coroute.repository;

import io.github.yvlar.coroute.domain.exception.AccesInterditException;
import io.github.yvlar.coroute.domain.exception.ReservationNotFoundException;
import io.github.yvlar.coroute.domain.model.JourSemaine;
import io.github.yvlar.coroute.domain.model.Trajet;
import io.github.yvlar.coroute.domain.model.TrajetType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTrajetRepository implements TrajetRepository {

  private final Map<UUID, Trajet> store = new ConcurrentHashMap<>();

  @Override
  public void save(final Trajet trajet) {
    this.store.put(trajet.getId(), trajet);
  }

  @Override
  public Optional<Trajet> findById(final UUID trajetId) {
    return Optional.ofNullable(this.store.get(trajetId));
  }

  @Override
  public List<Trajet> findAll() {
    return new ArrayList<>(this.store.values());
  }

  @Override
  public List<Trajet> findByFiltres(
      final String depart, final String destination, final String date) {
    return this.store.values().stream()
        .filter(t -> correspondTexte(t.getDepart(), depart))
        .filter(t -> correspondTexte(t.getDestination(), destination))
        .filter(t -> correspondDate(t, date))
        .toList();
  }

  @Override
  public void delete(final UUID trajetId) {
    this.store.remove(trajetId);
  }

  @Override
  public synchronized Optional<UUID> reserverAtomiquement(
      final UUID trajetId, final String passagerId, final int nombrePlaces) {
    final Trajet trajet = this.store.get(trajetId);
    if (trajet == null || nombrePlaces > trajet.getPlacesDisponibles()) {
      return Optional.empty();
    }
    return Optional.of(trajet.ajouterReservation(passagerId, nombrePlaces));
  }

  @Override
  public synchronized boolean annulerReservationAtomiquement(
      final UUID trajetId,
      final UUID reservationId,
      final String passagerId,
      final int nombrePlaces) {
    final Trajet trajet = this.store.get(trajetId);
    if (trajet == null) {
      return false;
    }

    try {
      if (trajet.getNombrePlacesReservation(reservationId, passagerId) != nombrePlaces) {
        return false;
      }
      trajet.annulerReservation(reservationId, passagerId);
      return true;
    } catch (ReservationNotFoundException | AccesInterditException exception) {
      return false;
    }
  }

  private static boolean correspondTexte(final String valeur, final String filtre) {
    return filtre == null
        || filtre.isBlank()
        || valeur != null && valeur.equalsIgnoreCase(filtre.strip());
  }

  private static boolean correspondDate(final Trajet trajet, final String date) {
    if (date == null || date.isBlank()) {
      return true;
    }

    final LocalDate dateRecherchee = LocalDate.parse(date);
    if (!TrajetType.REGULIER.equals(trajet.getType())) {
      return dateRecherchee.equals(trajet.getDate());
    }

    return trajet.getDateDebut() != null
        && trajet.getDateFin() != null
        && !dateRecherchee.isBefore(trajet.getDateDebut())
        && !dateRecherchee.isAfter(trajet.getDateFin())
        && trajet.getJoursRecurrence().contains(JourSemaine.from(dateRecherchee.getDayOfWeek()));
  }
}
