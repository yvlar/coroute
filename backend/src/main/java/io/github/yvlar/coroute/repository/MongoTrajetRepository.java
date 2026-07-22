package io.github.yvlar.coroute.repository;

import com.mongodb.client.model.ReturnDocument;
import dev.morphia.Datastore;
import dev.morphia.ModifyOptions;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filter;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.yvlar.coroute.domain.model.JourSemaine;
import io.github.yvlar.coroute.domain.model.Reservation;
import io.github.yvlar.coroute.domain.model.Trajet;
import io.github.yvlar.coroute.domain.model.TrajetFactory;
import io.github.yvlar.coroute.domain.model.TrajetType;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

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
    initialiserFactories(trajet);
    return Optional.ofNullable(trajet);
  }

  @Override
  public List<Trajet> findAll() {
    return executerRequete(this.datastore.find(Trajet.class));
  }

  @Override
  public List<Trajet> findByFiltres(
      final String depart, final String destination, final String date) {
    final Query<Trajet> query = this.datastore.find(Trajet.class);
    final List<Filter> filters = new ArrayList<>();

    ajouterFiltreTexte(filters, "depart", depart);
    ajouterFiltreTexte(filters, "destination", destination);

    if (date != null && !date.isBlank()) {
      final LocalDate dateRecherchee = LocalDate.parse(date.strip());
      filters.add(
          Filters.or(
              Filters.and(
                  Filters.eq("type", TrajetType.PONCTUEL),
                  Filters.eq("date", dateRecherchee)),
              Filters.and(
                  Filters.eq("type", TrajetType.REGULIER),
                  Filters.lte("dateDebut", dateRecherchee),
                  Filters.gte("dateFin", dateRecherchee),
                  Filters.eq(
                      "joursRecurrence", JourSemaine.from(dateRecherchee.getDayOfWeek())))));
    }

    if (!filters.isEmpty()) {
      query.filter(filters.toArray(Filter[]::new));
    }
    return executerRequete(query);
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

  @Override
  public boolean annulerReservationAtomiquement(
      final UUID trajetId,
      final UUID reservationId,
      final String passagerId,
      final int nombrePlaces) {
    final Trajet updated =
        this.datastore
            .find(Trajet.class)
            .filter(
                Filters.eq("_id", trajetId),
                Filters.elemMatch(
                    "reservations",
                    Filters.eq("_id", reservationId),
                    Filters.eq("passagerId", passagerId)))
            .modify(
                UpdateOperators.inc("placesDisponibles", nombrePlaces),
                UpdateOperators.pull("reservations", Filters.eq("_id", reservationId)))
            .execute(new ModifyOptions().returnDocument(ReturnDocument.AFTER));

    return updated != null;
  }

  private void ajouterFiltreTexte(
      final List<Filter> filters, final String champ, final String valeur) {
    if (valeur == null || valeur.isBlank()) {
      return;
    }
    final Pattern exactIgnoreCase =
        Pattern.compile(
            "^" + Pattern.quote(valeur.strip()) + "$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    filters.add(Filters.regex(champ, exactIgnoreCase));
  }

  private List<Trajet> executerRequete(final Query<Trajet> query) {
    final List<Trajet> trajets = new ArrayList<>();
    query.forEach(
        trajet -> {
          initialiserFactories(trajet);
          trajets.add(trajet);
        });
    return trajets;
  }

  private void initialiserFactories(final Trajet trajet) {
    if (trajet != null) {
      trajet.setReservationFactory(this.trajetFactory.getReservationFactory());
    }
  }
}
