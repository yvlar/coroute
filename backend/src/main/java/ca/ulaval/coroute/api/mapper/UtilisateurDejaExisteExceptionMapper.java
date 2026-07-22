package ca.ulaval.coroute.api.mapper;

import ca.ulaval.coroute.domain.exception.UtilisateurDejaExisteException;
import ca.ulaval.coroute.dto.response.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UtilisateurDejaExisteExceptionMapper
        implements ExceptionMapper<UtilisateurDejaExisteException> {

    @Override
    public Response toResponse(final UtilisateurDejaExisteException exception) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
