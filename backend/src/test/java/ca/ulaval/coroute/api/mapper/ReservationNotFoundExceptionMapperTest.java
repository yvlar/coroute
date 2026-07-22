package ca.ulaval.coroute.api.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.ulaval.coroute.domain.exception.ReservationNotFoundException;
import ca.ulaval.coroute.dto.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReservationNotFoundExceptionMapperTest {

  private static final UUID RESERVATION_ID = UUID.randomUUID();

  private ReservationNotFoundExceptionMapper mapper;
  private Response actualResponse;

  @BeforeEach
  void setUp() {
    this.mapper = new ReservationNotFoundExceptionMapper();
  }

  @Test
  void givenReservationNotFoundException_whenToResponse_thenReturn404() {
    this.actualResponse = mapper.toResponse(new ReservationNotFoundException(RESERVATION_ID));
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), this.actualResponse.getStatus());
  }

  @Test
  void givenReservationNotFoundException_whenToResponse_thenBodyContainsReservationId() {
    this.actualResponse = mapper.toResponse(new ReservationNotFoundException(RESERVATION_ID));
    assertAll(
        () -> {
          final ErrorResponse error = (ErrorResponse) this.actualResponse.getEntity();
          assertTrue(error.message().contains(RESERVATION_ID.toString()));
        });
  }
}
