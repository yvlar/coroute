package ca.ulaval.coroute.api.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.ulaval.coroute.domain.exception.PlacesInsuffisantesException;
import ca.ulaval.coroute.dto.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlacesInsuffisantesExceptionMapperTest {

  private static final int PLACES_DISPONIBLES = 1;
  private static final int PLACES_DEMANDEES = 3;

  private PlacesInsuffisantesExceptionMapper mapper;
  private Response actualResponse;

  @BeforeEach
  void setUp() {
    this.mapper = new PlacesInsuffisantesExceptionMapper();
  }

  @Test
  void givenPlacesInsuffisantesException_whenToResponse_thenReturn409() {
    this.actualResponse = mapper.toResponse(new PlacesInsuffisantesException(PLACES_DISPONIBLES, PLACES_DEMANDEES));
    assertEquals(Response.Status.CONFLICT.getStatusCode(), this.actualResponse.getStatus());
  }

  @Test
  void givenPlacesInsuffisantesException_whenToResponse_thenBodyContainsPlacesDisponibles() {
    this.actualResponse = mapper.toResponse(new PlacesInsuffisantesException(PLACES_DISPONIBLES, PLACES_DEMANDEES));
    assertAll(
        () -> {
          final ErrorResponse error = (ErrorResponse) this.actualResponse.getEntity();
          assertTrue(error.message().contains(String.valueOf(PLACES_DISPONIBLES)));
          assertTrue(error.message().contains(String.valueOf(PLACES_DEMANDEES)));
        });
  }
}
