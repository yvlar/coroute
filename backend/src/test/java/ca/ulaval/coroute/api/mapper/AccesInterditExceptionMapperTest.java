package ca.ulaval.coroute.api.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.ulaval.coroute.domain.exception.AccesInterditException;
import ca.ulaval.coroute.dto.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccesInterditExceptionMapperTest {

  private static final String ACTION = "annuler la réservation d'un autre passager";

  private AccesInterditExceptionMapper mapper;
  private Response actualResponse;

  @BeforeEach
  void setUp() {
    this.mapper = new AccesInterditExceptionMapper();
  }

  @Test
  void givenAccesInterditException_whenToResponse_thenReturn403() {
    this.actualResponse = mapper.toResponse(new AccesInterditException(ACTION));
    assertEquals(Response.Status.FORBIDDEN.getStatusCode(), this.actualResponse.getStatus());
  }

  @Test
  void givenAccesInterditException_whenToResponse_thenBodyContainsAction() {
    this.actualResponse = mapper.toResponse(new AccesInterditException(ACTION));
    assertAll(
        () -> {
          final ErrorResponse error = (ErrorResponse) this.actualResponse.getEntity();
          assertTrue(error.message().contains(ACTION));
        });
  }
}
