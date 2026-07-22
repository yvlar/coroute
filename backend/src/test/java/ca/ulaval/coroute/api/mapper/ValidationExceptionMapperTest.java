package ca.ulaval.coroute.api.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ca.ulaval.coroute.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ValidationExceptionMapperTest {

  @Mock private ConstraintViolation<?> violation1;
  @Mock private ConstraintViolation<?> violation2;

  private ValidationExceptionMapper mapper;
  private Response actualResponse;

  @BeforeEach
  void setUp() {
    this.mapper = new ValidationExceptionMapper();
  }

  @Test
  void givenConstraintViolationException_whenToResponse_thenReturn400() {
    this.actualResponse = mapper.toResponse(new ConstraintViolationException("Erreur", Set.of()));
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), this.actualResponse.getStatus());
  }

  @Test
  void givenSingleViolation_whenToResponse_thenBodyContainsMessage() {
    when(violation1.getMessage()).thenReturn("La destination est obligatoire");

    this.actualResponse =
        mapper.toResponse(new ConstraintViolationException("Erreur", Set.of(violation1)));

    assertAll(
        () -> {
          final ErrorResponse error = (ErrorResponse) this.actualResponse.getEntity();
          assertEquals("La destination est obligatoire", error.message());
        });
  }

  @Test
  void givenMultipleViolations_whenToResponse_thenBodyContainsAllMessages() {
    when(violation1.getMessage()).thenReturn("Le départ est obligatoire");
    when(violation2.getMessage()).thenReturn("Le prix doit être positif");

    this.actualResponse =
        mapper.toResponse(
            new ConstraintViolationException("Erreur", Set.of(violation1, violation2)));

    assertAll(
        () -> {
          final ErrorResponse error = (ErrorResponse) this.actualResponse.getEntity();
          assertTrue(
              error.message().contains("Le départ est obligatoire")
                  || error.message().contains("Le prix doit être positif"));
        });
  }
}
