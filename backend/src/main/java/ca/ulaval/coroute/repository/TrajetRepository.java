package ca.ulaval.coroute.repository;

import ca.ulaval.coroute.domain.model.Trajet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrajetRepository {

  void save(Trajet trajet);

  Optional<Trajet> findById(UUID trajetId);

  List<Trajet> findAll();

  List<Trajet> findByFiltres(String depart, String destination, String date);

  void delete(UUID trajetId);
}
