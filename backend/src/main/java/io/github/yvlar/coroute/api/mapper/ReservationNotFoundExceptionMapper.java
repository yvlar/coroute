package io.github.yvlar.coroute.api.mapper;

import io.github.yvlar.coroute.domain.exception.ReservationNotFoundException;
import io.github.yvlar.coroute.dto.response.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ReservationNotFoundExceptionMapper
    implements ExceptionMapper<ReservationNotFoundException> {

  @Override
  public Response toResponse(final ReservationNotFoundException exception) {
    return Response.status(Response.Status.NOT_FOUND)
        .entity(new ErrorResponse(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
