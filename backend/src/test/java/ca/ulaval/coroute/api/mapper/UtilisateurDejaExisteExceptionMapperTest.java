package ca.ulaval.coroute.api.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.ulaval.coroute.domain.exception.UtilisateurDejaExisteException;
import ca.ulaval.coroute.dto.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UtilisateurDejaExisteExceptionMapperTest {

  private static final String EMAIL = "marc@coroute.ca";

  private UtilisateurDejaExisteExceptionMapper mapper;
  private Response actualResponse;

  @BeforeEach
  void setUp() {
    this.mapper = new UtilisateurDejaExisteExceptionMapper();
  }

  @Test
  void givenUtilisateurDejaExisteException_whenToResponse_thenReturn409() {
    this.actualResponse = mapper.toResponse(new UtilisateurDejaExisteException(EMAIL));

    assertEquals(Response.Status.CONFLICT.getStatusCode(), this.actualResponse.getStatus());
  }

  @Test
  void givenUtilisateurDejaExisteException_whenToResponse_thenBodyContainsMessage() {
    this.actualResponse = mapper.toResponse(new UtilisateurDejaExisteException(EMAIL));

    assertAll(
        () -> {
          final ErrorResponse error = (ErrorResponse) this.actualResponse.getEntity();
          assertNotNull(error.message());
        });
  }
}
