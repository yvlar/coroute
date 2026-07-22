package ca.ulaval.coroute.repository;

import ca.ulaval.coroute.domain.model.Trajet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryTrajetRepository implements TrajetRepository {

  private final Map<UUID, Trajet> store = new HashMap<>();

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
        .filter(t -> depart == null || t.getDepart().equalsIgnoreCase(depart))
        .filter(t -> destination == null || t.getDestination().equalsIgnoreCase(destination))
        .filter(t -> date == null || t.getDate().equals(LocalDate.parse(date)))
        .toList();
  }

  @Override
  public void delete(final UUID trajetId) {
    this.store.remove(trajetId);
  }
}
