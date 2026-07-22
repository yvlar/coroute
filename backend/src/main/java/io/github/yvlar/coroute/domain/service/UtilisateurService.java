package io.github.yvlar.coroute.domain.service;

import io.github.yvlar.coroute.dto.request.ConnexionRequest;
import io.github.yvlar.coroute.dto.request.InscriptionRequest;
import io.github.yvlar.coroute.dto.response.TokenResponse;

public interface UtilisateurService {

  void inscrire(InscriptionRequest request);

  TokenResponse connecter(ConnexionRequest request);
}
