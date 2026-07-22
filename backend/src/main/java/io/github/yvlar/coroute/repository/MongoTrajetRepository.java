package io.github.yvlar.coroute.repository;

import com.mongodb.client.model.ReturnDocument;
import dev.morphia.Datastore;
import dev.morphia.ModifyOptions;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.yvlar.coroute.domain.model.Reservation;
import io.github.yvlar.coroute.domain.model.Trajet;
import io.github.yvlar.coroute.domain.model.TrajetFactory;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MongoTrajetRepository implements TrajetRepository {

  private final Datastore datastore;
  private final TrajetFactory trajetFactory;

  @Inject
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Injected by DI, thread-safe")
  public MongoTrajetRepository(final Datastore datastore, final TrajetFactory trajetFactory) {
    this.datastore = datastore;
    this.trajetFactory = trajetFactory;
  }

  @Override
  public void save(final Trajet trajet) {
    this.datastore.save(trajet);
  }

  @Override
  public Optional<Trajet> findById(final UUID trajetId) {
    final Trajet trajet =
        this.datastore.find(Trajet.class).filter(Filters.eq("_id", trajetId)).first();
    if (trajet != null) {
      trajet.setReservationFactory(trajetFactory.getReservationFactory());
    }
    return Optional.ofNullable(trajet);
  }

  @Override
  public List<Trajet> findAll() {
    final List<Trajet> trajets = new ArrayList<>();
    this.datastore
        .find(Trajet.class)
        .forEach(
            t -> {
              t.setReservationFactory(trajetFactory.getReservationFactory());
              trajets.add(t);
            });
    return trajets;
  }

  @Override
  public List<Trajet> findByFiltres(
      final String depart, final String destination, final String date) {
    return findAll().stream()
        .filter(t -> depart == null || t.getDepart().equalsIgnoreCase(depart))
        .filter(t -> destination == null || t.getDestination().equalsIgnoreCase(destination))
        .filter(t -> date == null || t.getDate().equals(LocalDate.parse(date)))
        .toList();
  }

  @Override
  public void delete(final UUID trajetId) {
    this.datastore.find(Trajet.class).filter(Filters.eq("_id", trajetId)).findAndDelete();
  }

  @Override
  public Optional<UUID> reserverAtomiquement(
      final UUID trajetId, final String passagerId, final int nombrePlaces) {
    final Reservation reservation =
        this.trajetFactory.getReservationFactory().creer(passagerId, nombrePlaces);

    // Mise à jour conditionnelle et atomique : ne s'applique que si le trajet a encore assez de
    // places. MongoDB garantit l'atomicité au niveau du document, ce qui empêche la
    // sur-réservation.
    final Trajet updated =
        this.datastore
            .find(Trajet.class)
            .filter(Filters.eq("_id", trajetId), Filters.gte("placesDisponibles", nombrePlaces))
            .modify(
                UpdateOperators.inc("placesDisponibles", -nombrePlaces),
                UpdateOperators.push("reservations", reservation))
            .execute(new ModifyOptions().returnDocument(ReturnDocument.AFTER));

    return updated == null ? Optional.empty() : Optional.of(reservation.getId());
  }
}
