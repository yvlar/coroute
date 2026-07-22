package io.github.yvlar.coroute.repository;

import io.github.yvlar.coroute.domain.exception.UtilisateurDejaExisteException;
import io.github.yvlar.coroute.domain.model.Utilisateur;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryUtilisateurRepository implements UtilisateurRepository {

  private final Map<UUID, Utilisateur> store = new HashMap<>();

  @Override
  public synchronized void save(final Utilisateur utilisateur) {
    final Optional<Utilisateur> existant = findByEmail(utilisateur.getEmail());
    if (existant.isPresent() && !existant.get().getId().equals(utilisateur.getId())) {
      throw new UtilisateurDejaExisteException(utilisateur.getEmail());
    }
    this.store.put(utilisateur.getId(), utilisateur);
  }

  @Override
  public Optional<Utilisateur> findById(final UUID id) {
    return Optional.ofNullable(this.store.get(id));
  }

  @Override
  public Optional<Utilisateur> findByEmail(final String email) {
    final String emailNormalise = Utilisateur.normaliserEmail(email);
    return this.store.values().stream()
        .filter(u -> u.getEmail().equals(emailNormalise))
        .findFirst();
  }
}
