package io.github.yvlar.coroute.repository;

import io.github.yvlar.coroute.domain.model.Utilisateur;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryUtilisateurRepository implements UtilisateurRepository {

  private final Map<UUID, Utilisateur> store = new HashMap<>();

  @Override
  public void save(final Utilisateur utilisateur) {
    this.store.put(utilisateur.getId(), utilisateur);
  }

  @Override
  public Optional<Utilisateur> findById(final UUID id) {
    return Optional.ofNullable(this.store.get(id));
  }

  @Override
  public Optional<Utilisateur> findByEmail(final String email) {
    return this.store.values().stream()
        .filter(u -> u.getEmail().equalsIgnoreCase(email))
        .findFirst();
  }
}
