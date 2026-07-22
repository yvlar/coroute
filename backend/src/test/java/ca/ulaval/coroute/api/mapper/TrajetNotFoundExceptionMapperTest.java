package ca.ulaval.coroute.api.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.ulaval.coroute.domain.exception.TrajetNotFoundException;
import ca.ulaval.coroute.dto.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrajetNotFoundExceptionMapperTest {

  private static final UUID TRAJET_ID = UUID.randomUUID();

  private TrajetNotFoundExceptionMapper mapper;
  private Response actualResponse;

  @BeforeEach
  void setUp() {
    this.mapper = new TrajetNotFoundExceptionMapper();
  }

  @Test
  void givenTrajetNotFoundException_whenToResponse_thenReturn404() {
    this.actualResponse = mapper.toResponse(new TrajetNotFoundException(TRAJET_ID));
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), this.actualResponse.getStatus());
  }

  @Test
  void givenTrajetNotFoundException_whenToResponse_thenBodyContainsTrajetId() {
    this.actualResponse = mapper.toResponse(new TrajetNotFoundException(TRAJET_ID));
    assertAll(
        () -> {
          final ErrorResponse error = (ErrorResponse) this.actualResponse.getEntity();
          assertTrue(error.message().contains(TRAJET_ID.toString()));
        });
  }
}
