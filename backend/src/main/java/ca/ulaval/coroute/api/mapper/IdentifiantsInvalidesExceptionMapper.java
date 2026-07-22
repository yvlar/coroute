package ca.ulaval.coroute.api.mapper;

import ca.ulaval.coroute.domain.exception.IdentifiantsInvalidesException;
import ca.ulaval.coroute.dto.response.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IdentifiantsInvalidesExceptionMapper
    implements ExceptionMapper<IdentifiantsInvalidesException> {

  @Override
  public Response toResponse(final IdentifiantsInvalidesException exception) {
    return Response.status(Response.Status.UNAUTHORIZED)
        .entity(new ErrorResponse(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
