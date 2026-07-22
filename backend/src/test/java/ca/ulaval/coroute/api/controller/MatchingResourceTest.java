package ca.ulaval.coroute.api.controller;

import ca.ulaval.coroute.domain.model.JourSemaine;
import ca.ulaval.coroute.domain.model.TrajetType;
import ca.ulaval.coroute.domain.service.MatchingService;
import ca.ulaval.coroute.dto.response.MatchingResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchingResourceTest {

    private static final String DEPART = "Roxton";
    private static final String DESTINATION = "Drummondville";

    @Mock
    private MatchingService matchingService;

    @InjectMocks
    private MatchingResource matchingResource;

    private Response actualResponse;

    @AfterEach
    void tearDown() {
        this.actualResponse.close();
    }

    @Test
    void givenCriteresValides_whenMatch_thenReturn200AvecMatches() {
        final List<MatchingResponse> expected = List.of(createMatchingResponse());
        when(matchingService.trouverMatches(any())).thenReturn(expected);

        this.actualResponse = matchingResource.match(DEPART, DESTINATION, List.of(JourSemaine.LUNDI));

        assertAll(
                () -> assertEquals(Response.Status.OK.getStatusCode(), this.actualResponse.getStatus()),
                () -> assertEquals(expected, this.actualResponse.getEntity())
        );
    }

    @Test
    void givenAucunMatch_whenMatch_thenReturn200AvecListeVide() {
        when(matchingService.trouverMatches(any())).thenReturn(List.of());

        this.actualResponse = matchingResource.match(DEPART, DESTINATION, List.of());

        assertAll(
                () -> assertEquals(Response.Status.OK.getStatusCode(), this.actualResponse.getStatus()),
                () -> assertTrue(((List<?>) this.actualResponse.getEntity()).isEmpty())
        );
    }

    @Test
    void givenFiltresNuls_whenMatch_thenReturn200() {
        when(matchingService.trouverMatches(any())).thenReturn(List.of());

        this.actualResponse = matchingResource.match(null, null, null);

        assertEquals(Response.Status.OK.getStatusCode(), this.actualResponse.getStatus());
    }

    private MatchingResponse createMatchingResponse() {
        return new MatchingResponse(
                UUID.randomUUID(),
                DEPART,
                DESTINATION,
                LocalTime.of(7, 15),
                8.0,
                2,
                TrajetType.REGULIER,
                List.of(JourSemaine.LUNDI),
                80,
                "conducteur-123",
                null,
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 6, 30)
        );
    }
}
