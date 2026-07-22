package ca.ulaval.coroute.repository;

import ca.ulaval.coroute.domain.model.Utilisateur;
import java.util.Optional;
import java.util.UUID;

public interface UtilisateurRepository {

  void save(Utilisateur utilisateur);

  Optional<Utilisateur> findById(UUID id);

  Optional<Utilisateur> findByEmail(String email);
}
