package ca.ulaval.coroute.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.coroute.domain.exception.IdentifiantsInvalidesException;
import ca.ulaval.coroute.domain.exception.UtilisateurDejaExisteException;
import ca.ulaval.coroute.domain.model.Utilisateur;
import ca.ulaval.coroute.dto.request.ConnexionRequest;
import ca.ulaval.coroute.dto.request.InscriptionRequest;
import ca.ulaval.coroute.dto.response.TokenResponse;
import ca.ulaval.coroute.repository.UtilisateurRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UtilisateurServiceImplTest {

  private static final String NOM = "Marc Tremblay";
  private static final String EMAIL = "marc@coroute.ca";
  private static final String MOT_DE_PASSE = "password123";
  private static final String TOKEN = "jwt.token.ici";

  private static final InscriptionRequest INSCRIPTION_REQUEST = new InscriptionRequest(NOM, EMAIL, MOT_DE_PASSE);
  private static final ConnexionRequest CONNEXION_REQUEST = new ConnexionRequest(EMAIL, MOT_DE_PASSE);

  @Mock
  private UtilisateurRepository utilisateurRepository;
  @Mock
  private JwtService jwtService;
  @Mock
  private Utilisateur utilisateurMock;

  @InjectMocks
  private UtilisateurServiceImpl utilisateurService;

  // ─── inscrire ────────────────────────────────────────────────────────

  @Test
  void givenNouvelUtilisateur_whenInscrire_thenSauvegardeAppele() {
    when(utilisateurRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    utilisateurService.inscrire(INSCRIPTION_REQUEST);

    verify(utilisateurRepository).save(any(Utilisateur.class));
  }

  @Test
  void givenEmailDejaExistant_whenInscrire_thenLanceUtilisateurDejaExisteException() {
    when(utilisateurRepository.findByEmail(EMAIL)).thenReturn(Optional.of(utilisateurMock));

    assertThrows(
        UtilisateurDejaExisteException.class,
        () -> utilisateurService.inscrire(INSCRIPTION_REQUEST));

    verify(utilisateurRepository, never()).save(any());
  }

  // ─── connecter ───────────────────────────────────────────────────────

  @Test
  void givenIdentifiantsValides_whenConnecter_thenRetourneToken() {
    final String hash = BCrypt.hashpw(MOT_DE_PASSE, BCrypt.gensalt());
    when(utilisateurRepository.findByEmail(EMAIL)).thenReturn(Optional.of(utilisateurMock));
    when(utilisateurMock.getMotDePasseHash()).thenReturn(hash);
    when(utilisateurMock.getId()).thenReturn(UUID.randomUUID());
    when(utilisateurMock.getEmail()).thenReturn(EMAIL);
    when(jwtService.genererToken(any(), any())).thenReturn(TOKEN);

    final TokenResponse result = utilisateurService.connecter(CONNEXION_REQUEST);

    assertNotNull(result);
    assertEquals(TOKEN, result.token());
  }

  @Test
  void givenEmailInconnu_whenConnecter_thenLanceIdentifiantsInvalidesException() {
    when(utilisateurRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    assertThrows(
        IdentifiantsInvalidesException.class,
        () -> utilisateurService.connecter(CONNEXION_REQUEST));
  }

  @Test
  void givenMauvaisMotDePasse_whenConnecter_thenLanceIdentifiantsInvalidesException() {
    final String hash = BCrypt.hashpw("autrePassword", BCrypt.gensalt());
    when(utilisateurRepository.findByEmail(EMAIL)).thenReturn(Optional.of(utilisateurMock));
    when(utilisateurMock.getMotDePasseHash()).thenReturn(hash);

    assertThrows(
        IdentifiantsInvalidesException.class,
        () -> utilisateurService.connecter(CONNEXION_REQUEST));
  }
}
