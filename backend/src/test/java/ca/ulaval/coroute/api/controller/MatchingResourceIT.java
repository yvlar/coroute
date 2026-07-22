package ca.ulaval.coroute.api.controller;

import ca.ulaval.coroute.config.TestApplicationConfig;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MatchingResourceIT extends JerseyTest {

    private static final String INSCRIPTION_JSON = """
            {
                "nom": "Marc Tremblay",
                "email": "marc@coroute.ca",
                "motDePasse": "password123"
            }
            """;

    private static final String CONNEXION_JSON = """
            {
                "email": "marc@coroute.ca",
                "motDePasse": "password123"
            }
            """;

    private static final String TRAJET_REGULIER_JSON = """
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

    private static final String TRAJET_PONCTUEL_JSON = """
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

        final String tokenJson = target("/utilisateurs/connexion")
                .request()
                .post(Entity.entity(CONNEXION_JSON, MediaType.APPLICATION_JSON))
                .readEntity(String.class);

        this.token = tokenJson.replace("{\"token\":\"", "").replace("\"}", "");

        target("/trajets")
                .request()
                .header("Authorization", "Bearer " + token)
                .post(Entity.entity(TRAJET_REGULIER_JSON, MediaType.APPLICATION_JSON));

        target("/trajets")
                .request()
                .header("Authorization", "Bearer " + token)
                .post(Entity.entity(TRAJET_PONCTUEL_JSON, MediaType.APPLICATION_JSON));
    }

    // ─── GET /trajets/match ───────────────────────────────────────────────

    @Test
    void givenAucunFiltre_whenMatch_thenReturn200AvecTousTrajets() {
        final Response response = target("/trajets/match").request().get();

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertNotNull(response.readEntity(String.class))
        );
    }

    @Test
    void givenFiltreDepart_whenMatch_thenReturn200AvecTrajetsCorrespondants() {
        final Response response = target("/trajets/match")
                .queryParam("depart", "Roxton")
                .request()
                .get();

        assertEquals(200, response.getStatus());
    }

    @Test
    void givenFiltreDestination_whenMatch_thenReturn200() {
        final Response response = target("/trajets/match")
                .queryParam("destination", "Drummondville")
                .request()
                .get();

        assertEquals(200, response.getStatus());
    }

    @Test
    void givenFiltreJours_whenMatch_thenReturn200() {
        final Response response = target("/trajets/match")
                .queryParam("jours", "LUNDI")
                .queryParam("jours", "MERCREDI")
                .request()
                .get();

        assertEquals(200, response.getStatus());
    }

    @Test
    void givenTousFiltres_whenMatch_thenReturn200() {
        final Response response = target("/trajets/match")
                .queryParam("depart", "Roxton")
                .queryParam("destination", "Drummondville")
                .queryParam("jours", "LUNDI")
                .queryParam("jours", "VENDREDI")
                .request()
                .get();

        assertEquals(200, response.getStatus());
    }

    @Test
    void givenDestinationInexistante_whenMatch_thenReturn200AvecListeVide() {
        final Response response = target("/trajets/match")
                .queryParam("destination", "Sherbrooke")
                .request()
                .get();

        final String body = response.readEntity(String.class);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("[]", body)
        );
    }

    @Test
    void givenJoursIncompatibles_whenMatch_thenReturn200AvecListeVide() {
        final Response response = target("/trajets/match")
                .queryParam("depart", "Roxton")
                .queryParam("destination", "Drummondville")
                .queryParam("jours", "SAMEDI")
                .queryParam("jours", "DIMANCHE")
                .request()
                .get();

        final String body = response.readEntity(String.class);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("[]", body)
        );
    }
}
