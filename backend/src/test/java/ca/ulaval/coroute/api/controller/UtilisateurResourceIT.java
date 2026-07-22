package ca.ulaval.coroute.api.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.ulaval.coroute.config.TestApplicationConfig;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

public class UtilisateurResourceIT extends JerseyTest {

  private static final String INSCRIPTION_JSON =
      """
            {
                "nom": "Marc Tremblay",
                "email": "marc@coroute.ca",
                "motDePasse": "password123"
            }
            """;

  private static final String CONNEXION_JSON =
      """
            {
                "email": "marc@coroute.ca",
                "motDePasse": "password123"
            }
            """;

  private static final String MAUVAIS_MOT_DE_PASSE_JSON =
      """
            {
                "email": "marc@coroute.ca",
                "motDePasse": "mauvais"
            }
            """;

  private static final String TRAJET_JSON =
      """
            {
                "depart": "Quebec",
                "destination": "Montreal",
                "date": "2026-04-15",
                "heure": "08:30:00",
                "placesDisponibles": 3,
                "prixParPassager": 20.0,
                "type": "PONCTUEL"
            }
            """;

  @Override
  protected jakarta.ws.rs.core.Application configure() {
    return new TestApplicationConfig();
  }

  // ─── POST /utilisateurs/inscription ─────────────────────────────────

  @Test
  void givenUtilisateurValide_whenInscription_thenReturn201() {
    final Response response =
        target("/utilisateurs/inscription")
            .request()
            .post(Entity.entity(INSCRIPTION_JSON, MediaType.APPLICATION_JSON));

    assertEquals(201, response.getStatus());
  }

  @Test
  void givenEmailDejaExistant_whenInscription_thenReturn409() {
    target("/utilisateurs/inscription")
        .request()
        .post(Entity.entity(INSCRIPTION_JSON, MediaType.APPLICATION_JSON));

    final Response response =
        target("/utilisateurs/inscription")
            .request()
            .post(Entity.entity(INSCRIPTION_JSON, MediaType.APPLICATION_JSON));

    assertEquals(409, response.getStatus());
  }

  @Test
  void givenCorpsInvalide_whenInscription_thenReturn400() {
    final Response response =
        target("/utilisateurs/inscription")
            .request()
            .post(Entity.entity("{}", MediaType.APPLICATION_JSON));

    assertEquals(400, response.getStatus());
  }

  // ─── POST /utilisateurs/connexion ────────────────────────────────────

  @Test
  void givenIdentifiantsValides_whenConnexion_thenReturn200AvecToken() {
    target("/utilisateurs/inscription")
        .request()
        .post(Entity.entity(INSCRIPTION_JSON, MediaType.APPLICATION_JSON));

    final Response response =
        target("/utilisateurs/connexion")
            .request()
            .post(Entity.entity(CONNEXION_JSON, MediaType.APPLICATION_JSON));

    assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertNotNull(response.readEntity(String.class)));
  }

  @Test
  void givenEmailInconnu_whenConnexion_thenReturn401() {
    final Response response =
        target("/utilisateurs/connexion")
            .request()
            .post(Entity.entity(CONNEXION_JSON, MediaType.APPLICATION_JSON));

    assertEquals(401, response.getStatus());
  }

  @Test
  void givenMauvaisMotDePasse_whenConnexion_thenReturn401() {
    target("/utilisateurs/inscription")
        .request()
        .post(Entity.entity(INSCRIPTION_JSON, MediaType.APPLICATION_JSON));

    final Response response =
        target("/utilisateurs/connexion")
            .request()
            .post(Entity.entity(MAUVAIS_MOT_DE_PASSE_JSON, MediaType.APPLICATION_JSON));

    assertEquals(401, response.getStatus());
  }

  // ─── POST /trajets avec JWT ──────────────────────────────────────────

  @Test
  void givenTokenValide_whenPostTrajet_thenReturn201() {
    target("/utilisateurs/inscription")
        .request()
        .post(Entity.entity(INSCRIPTION_JSON, MediaType.APPLICATION_JSON));

    final String tokenJson =
        target("/utilisateurs/connexion")
            .request()
            .post(Entity.entity(CONNEXION_JSON, MediaType.APPLICATION_JSON))
            .readEntity(String.class);

    final String token = tokenJson.replace("{\"token\":\"", "").replace("\"}", "");

    final Response response =
        target("/trajets")
            .request()
            .header("Authorization", "Bearer " + token)
            .post(Entity.entity(TRAJET_JSON, MediaType.APPLICATION_JSON));

    assertEquals(201, response.getStatus());
  }

  @Test
  void givenSansToken_whenPostTrajet_thenReturn401() {
    final Response response =
        target("/trajets").request().post(Entity.entity(TRAJET_JSON, MediaType.APPLICATION_JSON));

    assertEquals(401, response.getStatus());
  }
}
