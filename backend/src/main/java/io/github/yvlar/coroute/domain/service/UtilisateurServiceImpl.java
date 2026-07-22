package io.github.yvlar.coroute.domain.service;

import io.github.yvlar.coroute.domain.exception.IdentifiantsInvalidesException;
import io.github.yvlar.coroute.domain.exception.UtilisateurDejaExisteException;
import io.github.yvlar.coroute.domain.model.Utilisateur;
import io.github.yvlar.coroute.dto.request.ConnexionRequest;
import io.github.yvlar.coroute.dto.request.InscriptionRequest;
import io.github.yvlar.coroute.dto.response.TokenResponse;
import io.github.yvlar.coroute.repository.UtilisateurRepository;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

public class UtilisateurServiceImpl implements UtilisateurService {

  private final UtilisateurRepository utilisateurRepository;
  private final JwtService jwtService;

  @Inject
  public UtilisateurServiceImpl(
      final UtilisateurRepository utilisateurRepository, final JwtService jwtService) {
    this.utilisateurRepository = utilisateurRepository;
    this.jwtService = jwtService;
  }

  @Override
  public void inscrire(final InscriptionRequest request) {
    if (this.utilisateurRepository.findByEmail(request.email()).isPresent()) {
      throw new UtilisateurDejaExisteException(request.email());
    }

    final String motDePasseHash = BCrypt.hashpw(request.motDePasse(), BCrypt.gensalt());
    final Utilisateur utilisateur = new Utilisateur(request.nom(), request.email(), motDePasseHash);

    this.utilisateurRepository.save(utilisateur);
  }

  @Override
  public TokenResponse connecter(final ConnexionRequest request) {
    final Utilisateur utilisateur =
        this.utilisateurRepository
            .findByEmail(request.email())
            .orElseThrow(IdentifiantsInvalidesException::new);

    if (!BCrypt.checkpw(request.motDePasse(), utilisateur.getMotDePasseHash())) {
      throw new IdentifiantsInvalidesException();
    }

    final String token =
        this.jwtService.genererToken(utilisateur.getId().toString(), utilisateur.getEmail());

    return new TokenResponse(token);
  }
}
