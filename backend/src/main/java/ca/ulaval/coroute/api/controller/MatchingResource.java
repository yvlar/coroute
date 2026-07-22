package ca.ulaval.coroute.api.controller;

import ca.ulaval.coroute.domain.model.JourSemaine;
import ca.ulaval.coroute.domain.service.MatchingService;
import ca.ulaval.coroute.dto.request.MatchingRequest;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/trajets/match")
public class MatchingResource {

    @Inject
    private MatchingService matchingService;

    @PermitAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response match(
            @QueryParam("depart") final String depart,
            @QueryParam("destination") final String destination,
            @QueryParam("jours") final List<JourSemaine> jours) {

        final MatchingRequest request = new MatchingRequest(depart, destination, jours);
        return Response.ok(matchingService.trouverMatches(request)).build();
    }
}
