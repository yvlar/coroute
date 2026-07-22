package io.github.yvlar.coroute.api.mapper;

import io.github.yvlar.coroute.domain.exception.AccesInterditException;
import io.github.yvlar.coroute.dto.response.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AccesInterditExceptionMapper implements ExceptionMapper<AccesInterditException> {

  @Override
  public Response toResponse(final AccesInterditException exception) {
    return Response.status(Response.Status.FORBIDDEN)
        .entity(new ErrorResponse(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
