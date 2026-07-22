package ca.ulaval.coroute.api.controller;

import ca.ulaval.coroute.config.AuthenticationFilter;
import ca.ulaval.coroute.domain.service.TrajetService;
import ca.ulaval.coroute.dto.request.ReservationCreateRequest;
import ca.ulaval.coroute.dto.request.TrajetCreateRequest;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.UUID;

@Path("/trajets")
public class TrajetResource {

  @Inject private TrajetService trajetService;

  @PermitAll
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response findAll(
      @QueryParam("depart") final String depart,
      @QueryParam("destination") final String destination,
      @QueryParam("date") final String date) {
    return Response.ok(this.trajetService.findAll(depart, destination, date)).build();
  }

  @PermitAll
  @GET
  @Path("/{trajetId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getTrajet(@PathParam("trajetId") final UUID trajetId) {
    return Response.ok(this.trajetService.findById(trajetId)).build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createTrajet(
      final @Context UriInfo uriInfo,
      @HeaderParam(AuthenticationFilter.USER_AUTH_HEADER) final String conducteurId,
      @Valid final TrajetCreateRequest trajetCreateRequest) {
    final UUID id = this.trajetService.createTrajet(conducteurId, trajetCreateRequest);
    final URI location = this.buildUri(uriInfo, String.valueOf(id));
    return Response.created(location).build();
  }

  @DELETE
  @Path("/{trajetId}")
  public Response deleteTrajet(
      @PathParam("trajetId") final UUID trajetId,
      @HeaderParam(AuthenticationFilter.USER_AUTH_HEADER) final String conducteurId) {
    this.trajetService.delete(trajetId, conducteurId);
    return Response.noContent().build();
  }

  @POST
  @Path("/{trajetId}/reservations")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createReservation(
      final @Context UriInfo uriInfo,
      @PathParam("trajetId") final UUID trajetId,
      @HeaderParam(AuthenticationFilter.USER_AUTH_HEADER) final String passagerId,
      @Valid final ReservationCreateRequest reservationCreateRequest) {
    final UUID id =
        this.trajetService.addReservation(trajetId, passagerId, reservationCreateRequest);
    final URI location = this.buildUri(uriInfo, String.valueOf(id));
    return Response.created(location).build();
  }

  @DELETE
  @Path("/{trajetId}/reservations/{reservationId}")
  public Response cancelReservation(
      @PathParam("trajetId") final UUID trajetId,
      @PathParam("reservationId") final UUID reservationId,
      @HeaderParam(AuthenticationFilter.USER_AUTH_HEADER) final String passagerId) {
    this.trajetService.cancelReservation(trajetId, reservationId, passagerId);
    return Response.noContent().build();
  }

  @GET
  @Path("/{trajetId}/reservations")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getReservations(
      @PathParam("trajetId") final UUID trajetId,
      @HeaderParam(AuthenticationFilter.USER_AUTH_HEADER) final String conducteurId) {
    return Response.ok(this.trajetService.getReservations(trajetId, conducteurId)).build();
  }

  private URI buildUri(final UriInfo uriInfo, final String resourcePath) {
    return uriInfo.getAbsolutePathBuilder().path(resourcePath).build();
  }
}
