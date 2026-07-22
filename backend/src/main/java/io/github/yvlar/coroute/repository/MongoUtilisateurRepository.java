package io.github.yvlar.coroute.repository;

import com.mongodb.MongoWriteException;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.yvlar.coroute.domain.exception.UtilisateurDejaExisteException;
import io.github.yvlar.coroute.domain.model.Utilisateur;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;

public class MongoUtilisateurRepository implements UtilisateurRepository {

  private static final int DUPLICATE_KEY_CODE = 11000;

  private final Datastore datastore;

  @Inject
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Injected by DI, thread-safe")
  public MongoUtilisateurRepository(final Datastore datastore) {
    this.datastore = datastore;
  }

  @Override
  public void save(final Utilisateur utilisateur) {
    try {
      this.datastore.save(utilisateur);
    } catch (MongoWriteException exception) {
      if (exception.getError().getCode() == DUPLICATE_KEY_CODE) {
        throw new UtilisateurDejaExisteException(utilisateur.getEmail(), exception);
      }
      throw exception;
    }
  }

  @Override
  public Optional<Utilisateur> findById(final UUID id) {
    return Optional.ofNullable(
        this.datastore.find(Utilisateur.class).filter(Filters.eq("_id", id)).first());
  }

  @Override
  public Optional<Utilisateur> findByEmail(final String email) {
    return Optional.ofNullable(
        this.datastore
            .find(Utilisateur.class)
            .filter(Filters.eq("email", Utilisateur.normaliserEmail(email)))
            .first());
  }
}
