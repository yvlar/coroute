package ca.ulaval.coroute.repository;

import ca.ulaval.coroute.domain.model.Utilisateur;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.inject.Inject;

import java.util.Optional;
import java.util.UUID;

public class MongoUtilisateurRepository implements UtilisateurRepository {

    private final Datastore datastore;

    @Inject
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Injected by DI, thread-safe")
    public MongoUtilisateurRepository(final Datastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public void save(final Utilisateur utilisateur) {
        this.datastore.save(utilisateur);
    }

    @Override
    public Optional<Utilisateur> findById(final UUID id) {
        return Optional.ofNullable(
                this.datastore.find(Utilisateur.class)
                        .filter(Filters.eq("_id", id))
                        .first());
    }

    @Override
    public Optional<Utilisateur> findByEmail(final String email) {
        return this.datastore.find(Utilisateur.class)
                .stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
