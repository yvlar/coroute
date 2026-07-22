package ca.ulaval.coroute.api.controller;

import ca.ulaval.coroute.domain.service.UtilisateurService;
import ca.ulaval.coroute.dto.request.ConnexionRequest;
import ca.ulaval.coroute.dto.request.InscriptionRequest;
import ca.ulaval.coroute.dto.response.TokenResponse;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@Path("/utilisateurs")
public class UtilisateurResource {

    @Inject
    private UtilisateurService utilisateurService;

    @PermitAll
    @POST
    @Path("/inscription")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response inscrire(
            final @Context UriInfo uriInfo,
            @Valid final InscriptionRequest inscriptionRequest) {
        this.utilisateurService.inscrire(inscriptionRequest);
        final URI location = uriInfo.getAbsolutePathBuilder().build();
        return Response.created(location).build();
    }

    @PermitAll
    @POST
    @Path("/connexion")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response connecter(@Valid final ConnexionRequest connexionRequest) {
        final TokenResponse token = this.utilisateurService.connecter(connexionRequest);
        return Response.ok(token).build();
    }
}
