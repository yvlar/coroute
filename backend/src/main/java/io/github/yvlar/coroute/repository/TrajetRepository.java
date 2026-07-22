package io.github.yvlar.coroute.repository;

import io.github.yvlar.coroute.domain.model.Trajet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrajetRepository {

  void save(Trajet trajet);

  Optional<Trajet> findById(UUID trajetId);

  List<Trajet> findAll();

  List<Trajet> findByFiltres(String depart, String destination, String date);

  void delete(UUID trajetId);

  /**
   * Réserve des places de manière atomique.
   *
   * <p>Décrémente {@code placesDisponibles} et ajoute la réservation en une seule opération
   * conditionnelle (uniquement si {@code placesDisponibles >= nombrePlaces}). Empêche la
   * sur-réservation lorsque plusieurs passagers réservent simultanément le même trajet.
   *
   * @return l'identifiant de la réservation créée si des places étaient disponibles, sinon {@link
   *     Optional#empty()} (trajet inexistant ou places insuffisantes).
   */
  Optional<UUID> reserverAtomiquement(UUID trajetId, String passagerId, int nombrePlaces);

  /**
   * Annule une réservation sans réécrire tout le document du trajet.
   *
   * <p>La suppression de la réservation et la restitution des places sont effectuées dans une seule
   * mise à jour conditionnelle.
   *
   * @return {@code true} si la réservation correspondante a été annulée, sinon {@code false}.
   */
  boolean annulerReservationAtomiquement(
      UUID trajetId, UUID reservationId, String passagerId, int nombrePlaces);
}
