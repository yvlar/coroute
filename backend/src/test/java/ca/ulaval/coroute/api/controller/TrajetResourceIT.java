package ca.ulaval.coroute.api.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.ulaval.coroute.config.TestApplicationConfig;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrajetResourceIT extends JerseyTest {

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

  private static final String TRAJET_PONCTUEL_JSON =
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

  private static final String TRAJET_REGULIER_JSON =
      """
            {
                "depart": "Roxton",
                "destination": "Drummondville",
                "heure": "07:15:00",
                "placesDisponibles": 2,
                "prixParPassager": 8.0,
                "type": "REGULIER",
                "joursRecurrence": ["LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI"],
                "dateDebut": "2026-04-01",
                "dateFin": "2026-06-30"
            }
            """;

  private static final String RESERVATION_JSON =
      """
            {
                "nombrePlaces": 1
            }
            """;

  private String token;

  @Override
  protected jakarta.ws.rs.core.Application configure() {
    return new TestApplicationConfig();
  }

  @BeforeEach
  void inscrireEtConnecter() {
    target("/utilisateurs/inscription")
        .request()
        .post(Entity.entity(INSCRIPTION_JSON, MediaType.APPLICATION_JSON));

    final String tokenJson =
        target("/utilisateurs/connexion")
            .request()
            .post(Entity.entity(CONNEXION_JSON, MediaType.APPLICATION_JSON))
            .readEntity(String.class);

    this.token = tokenJson.replace("{\"token\":\"", "").replace("\"}", "");
  }

  @Test
  void givenAucunTrajet_whenGetTrajets_thenReturn200AvecListeVide() {
    final Response response = target("/trajets").request().get();
    assertAll(
        () -> assertEquals(200, response.getStatus()),
        () -> assertNotNull(response.readEntity(String.class)));
  }

  @Test
  void givenTrajetValide_whenPostTrajet_thenReturn201AvecLocation() {
    final Response response =
        target("/trajets")
            .request()
            .header("Authorization", "Bearer " + token)
            .post(Entity.entity(TRAJET_PONCTUEL_JSON, MediaType.APPLICATION_JSON));
    assertAll(
        () -> assertEquals(201, response.getStatus()),
        () -> assertNotNull(response.getHeaderString("Location")));
  }

  @Test
  void givenTrajetRegulierValide_whenPostTrajet_thenReturn201AvecLocation() {
    final Response response =
        target("/trajets")
            .request()
            .header("Authorization", "Bearer " + token)
            .post(Entity.entity(TRAJET_REGULIER_JSON, MediaType.APPLICATION_JSON));
    assertAll(
        () -> assertEquals(201, response.getStatus()),
        () -> assertNotNull(response.getHeaderString("Location")));
  }

  @Test
  void givenSansToken_whenPostTrajet_thenReturn401() {
    final Response response =
        target("/trajets")
            .request()
            .post(Entity.entity(TRAJET_PONCTUEL_JSON, MediaType.APPLICATION_JSON));
    assertEquals(401, response.getStatus());
  }

  @Test
  void givenCorpsManquant_whenPostTrajet_thenReturn400() {
    final Response response =
        target("/trajets")
            .request()
            .header("Authorization", "Bearer " + token)
            .post(Entity.entity("{}", MediaType.APPLICATION_JSON));
    assertEquals(400, response.getStatus());
  }

  @Test
  void givenTrajetExistant_whenGetTrajetById_thenReturn200() {
    final String location =
        target("/trajets")
            .request()
            .header("Authorization", "Bearer " + token)
            .post(Entity.entity(TRAJET_PONCTUEL_JSON, MediaType.APPLICATION_JSON))
            .getHeaderString("Location");
    final Response response = client().target(location).request().get();
    assertEquals(200, response.getStatus());
  }

  @Test
  void givenIdInexistant_whenGetTrajetById_thenReturn404() {
    final Response response =
        target("/trajets/00000000-0000-0000-0000-000000000000").request().get();
    assertEquals(404, response.getStatus());
  }

  @Test
  void givenTrajetExistant_whenDeleteTrajet_thenReturn204() {
    final String location =
        target("/trajets")
            .request()
            .header("Authorization", "Bearer " + token)
            .post(Entity.entity(TRAJET_PONCTUEL_JSON, MediaType.APPLICATION_JSON))
            .getHeaderString("Location");
    final Response response =
        client().target(location).request().header("Authorization", "Bearer " + token).delete();
    assertEquals(204, response.getStatus());
  }

  @Test
  void givenSansToken_whenDeleteTrajet_thenReturn401() {
    final String location =
        target("/trajets")
            .request()
            .header("Authorization", "Bearer " + token)
            .post(Entity.entity(TRAJET_PONCTUEL_JSON, MediaType.APPLICATION_JSON))
            .getHeaderString("Location");
    final Response response = client().target(location).request().delete();
    assertEquals(401, response.getStatus());
  }

  @Test
  void givenTrajetExistant_whenPostReservation_thenReturn201() {
    final String location =
        target("/trajets")
            .request()
            .header("Authorization", "Bearer " + token)
            .post(Entity.entity(TRAJET_PONCTUEL_JSON, MediaType.APPLICATION_JSON))
            .getHeaderString("Location");
    final String trajetId = location.substring(location.lastIndexOf("/") + 1);
    final Response response =
        target("/trajets/" + trajetId + "/reservations")
            .request()
            .header("Authorization", "Bearer " + token)
            .post(Entity.entity(RESERVATION_JSON, MediaType.APPLICATION_JSON));
    assertAll(
        () -> assertEquals(201, response.getStatus()),
        () -> assertNotNull(response.getHeaderString("Location")));
  }

  @Test
  void givenTrajetInexistant_whenPostReservation_thenReturn404() {
    final Response response =
        target("/trajets/00000000-0000-0000-0000-000000000000/reservations")
            .request()
            .header("Authorization", "Bearer " + token)
            .post(Entity.entity(RESERVATION_JSON, MediaType.APPLICATION_JSON));
    assertEquals(404, response.getStatus());
  }

  @Test
  void givenTrajetAvecReservation_whenGetReservations_thenReturn200() {
    final String location =
        target("/trajets")
            .request()
            .header("Authorization", "Bearer " + token)
            .post(Entity.entity(TRAJET_PONCTUEL_JSON, MediaType.APPLICATION_JSON))
            .getHeaderString("Location");
    final String trajetId = location.substring(location.lastIndexOf("/") + 1);
    target("/trajets/" + trajetId + "/reservations")
        .request()
        .header("Authorization", "Bearer " + token)
        .post(Entity.entity(RESERVATION_JSON, MediaType.APPLICATION_JSON));
    final Response response =
        target("/trajets/" + trajetId + "/reservations")
            .request()
            .header("Authorization", "Bearer " + token)
            .get();
    assertEquals(200, response.getStatus());
  }
}
