package ca.ulaval.coroute.domain.service;

import ca.ulaval.coroute.domain.exception.IdentifiantsInvalidesException;
import ca.ulaval.coroute.domain.exception.UtilisateurDejaExisteException;
import ca.ulaval.coroute.domain.model.Utilisateur;
import ca.ulaval.coroute.dto.request.ConnexionRequest;
import ca.ulaval.coroute.dto.request.InscriptionRequest;
import ca.ulaval.coroute.dto.response.TokenResponse;
import ca.ulaval.coroute.repository.UtilisateurRepository;
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
