package ca.ulaval.coroute.api.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.ulaval.coroute.domain.exception.IdentifiantsInvalidesException;
import ca.ulaval.coroute.dto.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IdentifiantsInvalidesExceptionMapperTest {

  private IdentifiantsInvalidesExceptionMapper mapper;
  private Response actualResponse;

  @BeforeEach
  void setUp() {
    this.mapper = new IdentifiantsInvalidesExceptionMapper();
  }

  @Test
  void givenIdentifiantsInvalidesException_whenToResponse_thenReturn401() {
    this.actualResponse = mapper.toResponse(new IdentifiantsInvalidesException());

    assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), this.actualResponse.getStatus());
  }

  @Test
  void givenIdentifiantsInvalidesException_whenToResponse_thenBodyContainsMessage() {
    this.actualResponse = mapper.toResponse(new IdentifiantsInvalidesException());

    assertAll(
        () -> {
          final ErrorResponse error = (ErrorResponse) this.actualResponse.getEntity();
          assertNotNull(error.message());
        });
  }
}
