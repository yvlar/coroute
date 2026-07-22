package ca.ulaval.coroute.domain.service;

import ca.ulaval.coroute.dto.request.ConnexionRequest;
import ca.ulaval.coroute.dto.request.InscriptionRequest;
import ca.ulaval.coroute.dto.response.TokenResponse;

public interface UtilisateurService {

  void inscrire(InscriptionRequest request);

  TokenResponse connecter(ConnexionRequest request);
}
