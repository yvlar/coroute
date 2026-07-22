package ca.ulaval.coroute.api.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.coroute.domain.service.UtilisateurService;
import ca.ulaval.coroute.dto.request.ConnexionRequest;
import ca.ulaval.coroute.dto.request.InscriptionRequest;
import ca.ulaval.coroute.dto.response.TokenResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UtilisateurResourceTest {

  private static final String NOM = "Marc Tremblay";
  private static final String EMAIL = "marc@coroute.ca";
  private static final String MOT_DE_PASSE = "password123";
  private static final String TOKEN = "jwt.token.ici";

  private static final InscriptionRequest INSCRIPTION_REQUEST =
      new InscriptionRequest(NOM, EMAIL, MOT_DE_PASSE);
  private static final ConnexionRequest CONNEXION_REQUEST =
      new ConnexionRequest(EMAIL, MOT_DE_PASSE);

  @Mock private UtilisateurService utilisateurService;
  @Mock private UriInfo uriInfo;
  @Mock private UriBuilder uriBuilder;

  @InjectMocks private UtilisateurResource utilisateurResource;

  private Response actualResponse;

  @AfterEach
  void tearDown() {
    this.actualResponse.close();
  }

  // ─── inscrire ────────────────────────────────────────────────────────

  @Test
  void givenInscriptionValide_whenInscrire_thenReturn201() {
    when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
    when(uriBuilder.build())
        .thenReturn(URI.create("http://localhost:8080/utilisateurs/inscription"));

    this.actualResponse = utilisateurResource.inscrire(uriInfo, INSCRIPTION_REQUEST);

    assertAll(
        () ->
            assertEquals(Response.Status.CREATED.getStatusCode(), this.actualResponse.getStatus()),
        () -> verify(utilisateurService).inscrire(INSCRIPTION_REQUEST));
  }

  // ─── connecter ───────────────────────────────────────────────────────

  @Test
  void givenConnexionValide_whenConnecter_thenReturn200AvecToken() {
    when(utilisateurService.connecter(CONNEXION_REQUEST)).thenReturn(new TokenResponse(TOKEN));

    this.actualResponse = utilisateurResource.connecter(CONNEXION_REQUEST);

    assertAll(
        () -> assertEquals(Response.Status.OK.getStatusCode(), this.actualResponse.getStatus()),
        () -> assertEquals(new TokenResponse(TOKEN), this.actualResponse.getEntity()));
  }
}
