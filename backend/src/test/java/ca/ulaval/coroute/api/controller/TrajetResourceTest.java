package ca.ulaval.coroute.api.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.coroute.domain.model.JourSemaine;
import ca.ulaval.coroute.domain.model.TrajetType;
import ca.ulaval.coroute.domain.service.TrajetService;
import ca.ulaval.coroute.dto.request.ReservationCreateRequest;
import ca.ulaval.coroute.dto.request.TrajetCreateRequest;
import ca.ulaval.coroute.dto.response.ReservationResponse;
import ca.ulaval.coroute.dto.response.TrajetResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TrajetResourceTest {

  private static final UUID TRAJET_ID = UUID.randomUUID();
  private static final UUID RESERVATION_ID = UUID.randomUUID();
  private static final String CONDUCTEUR_ID = "conducteur-123";
  private static final String PASSAGER_ID = "passager-456";
  private static final String DEPART = "Québec";
  private static final String DESTINATION = "Montréal";
  private static final String DATE = "2026-04-01";

  private static final TrajetCreateRequest TRAJET_PONCTUEL_REQUEST =
      new TrajetCreateRequest(
          DEPART,
          DESTINATION,
          LocalDate.of(2026, 4, 1),
          LocalTime.of(8, 30),
          3,
          20.0,
          TrajetType.PONCTUEL,
          null,
          null,
          null);

  private static final TrajetCreateRequest TRAJET_REGULIER_REQUEST =
      new TrajetCreateRequest(
          "Roxton",
          "Drummondville",
          null,
          LocalTime.of(7, 15),
          2,
          8.0,
          TrajetType.REGULIER,
          List.of(JourSemaine.LUNDI, JourSemaine.VENDREDI),
          LocalDate.of(2026, 4, 1),
          LocalDate.of(2026, 6, 30));

  private static final ReservationCreateRequest RESERVATION_CREATE_REQUEST =
      new ReservationCreateRequest(1);

  @Mock private TrajetService trajetService;
  @Mock private UriInfo uriInfo;
  @Mock private UriBuilder uriBuilder;

  @InjectMocks private TrajetResource trajetResource;

  private Response actualResponse;

  @AfterEach
  public void tearDown() {
    this.actualResponse.close();
  }

  // ─── findAll ────────────────────────────────────────────────────────

  @Test
  void givenTrajetsInService_whenFindAll_thenReturn200WithListOfTrajets() {
    final TrajetResponse trajet1 =
        createTrajetResponse(TRAJET_ID, DEPART, DESTINATION, TrajetType.PONCTUEL);
    final TrajetResponse trajet2 =
        createTrajetResponse(UUID.randomUUID(), "Roxton", "Drummondville", TrajetType.REGULIER);
    final List<TrajetResponse> expectedTrajets = List.of(trajet1, trajet2);

    when(trajetService.findAll(null, null, null)).thenReturn(expectedTrajets);

    this.actualResponse = trajetResource.findAll(null, null, null);

    assertAll(
        () -> assertEquals(Response.Status.OK.getStatusCode(), this.actualResponse.getStatus()),
        () -> {
          final List<TrajetResponse> trajets =
              (List<TrajetResponse>) this.actualResponse.getEntity();
          assertAll(
              () -> assertEquals(expectedTrajets.size(), trajets.size()),
              () -> assertEquals(DEPART, trajets.get(0).depart()),
              () -> assertEquals("Drummondville", trajets.get(1).destination()));
        });
  }

  @Test
  void givenNoTrajetsInService_whenFindAll_thenReturn200WithEmptyList() {
    when(trajetService.findAll(null, null, null)).thenReturn(List.of());

    this.actualResponse = trajetResource.findAll(null, null, null);

    assertAll(
        () -> assertEquals(Response.Status.OK.getStatusCode(), this.actualResponse.getStatus()),
        () -> assertTrue(((List<TrajetResponse>) this.actualResponse.getEntity()).isEmpty()));
  }

  // ─── getTrajet ──────────────────────────────────────────────────────

  @Test
  void givenExistingTrajetId_whenGetTrajet_thenReturn200WithTrajet() {
    final TrajetResponse expected =
        createTrajetResponse(TRAJET_ID, DEPART, DESTINATION, TrajetType.PONCTUEL);

    when(trajetService.findById(TRAJET_ID)).thenReturn(expected);

    this.actualResponse = trajetResource.getTrajet(TRAJET_ID);

    assertAll(
        () -> assertEquals(Response.Status.OK.getStatusCode(), this.actualResponse.getStatus()),
        () -> assertEquals(expected, this.actualResponse.getEntity()));
  }

  // ─── createTrajet ponctuel ──────────────────────────────────────────

  @Test
  void givenTrajetPonctuelValide_whenCreateTrajet_thenReturn201WithLocation() {
    final String uri = "http://localhost:8080/trajets/" + TRAJET_ID;

    when(trajetService.createTrajet(CONDUCTEUR_ID, TRAJET_PONCTUEL_REQUEST)).thenReturn(TRAJET_ID);
    when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
    when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
    when(uriBuilder.build()).thenReturn(URI.create(uri));

    this.actualResponse =
        trajetResource.createTrajet(uriInfo, CONDUCTEUR_ID, TRAJET_PONCTUEL_REQUEST);

    assertAll(
        () ->
            assertEquals(Response.Status.CREATED.getStatusCode(), this.actualResponse.getStatus()),
        () -> assertEquals(uri, this.actualResponse.getHeaderString("Location")));
    verify(trajetService).createTrajet(CONDUCTEUR_ID, TRAJET_PONCTUEL_REQUEST);
  }

  // ─── createTrajet régulier ──────────────────────────────────────────

  @Test
  void givenTrajetRegulierValide_whenCreateTrajet_thenReturn201WithLocation() {
    final String uri = "http://localhost:8080/trajets/" + TRAJET_ID;

    when(trajetService.createTrajet(CONDUCTEUR_ID, TRAJET_REGULIER_REQUEST)).thenReturn(TRAJET_ID);
    when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
    when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
    when(uriBuilder.build()).thenReturn(URI.create(uri));

    this.actualResponse =
        trajetResource.createTrajet(uriInfo, CONDUCTEUR_ID, TRAJET_REGULIER_REQUEST);

    assertAll(
        () ->
            assertEquals(Response.Status.CREATED.getStatusCode(), this.actualResponse.getStatus()),
        () -> assertEquals(uri, this.actualResponse.getHeaderString("Location")));
    verify(trajetService).createTrajet(CONDUCTEUR_ID, TRAJET_REGULIER_REQUEST);
  }

  // ─── deleteTrajet ───────────────────────────────────────────────────

  @Test
  void givenExistingTrajet_whenDeleteTrajet_thenReturn204AndServiceCalled() {
    this.actualResponse = trajetResource.deleteTrajet(TRAJET_ID, CONDUCTEUR_ID);

    assertEquals(Response.Status.NO_CONTENT.getStatusCode(), this.actualResponse.getStatus());
    verify(trajetService).delete(TRAJET_ID, CONDUCTEUR_ID);
  }

  // ─── createReservation ──────────────────────────────────────────────

  @Test
  void givenValidRequest_whenCreateReservation_thenReturn201WithLocation() {
    final String uri =
        "http://localhost:8080/trajets/" + TRAJET_ID + "/reservations/" + RESERVATION_ID;

    when(trajetService.addReservation(TRAJET_ID, PASSAGER_ID, RESERVATION_CREATE_REQUEST))
        .thenReturn(RESERVATION_ID);
    when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
    when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
    when(uriBuilder.build()).thenReturn(URI.create(uri));

    this.actualResponse =
        trajetResource.createReservation(
            uriInfo, TRAJET_ID, PASSAGER_ID, RESERVATION_CREATE_REQUEST);

    assertAll(
        () ->
            assertEquals(Response.Status.CREATED.getStatusCode(), this.actualResponse.getStatus()),
        () -> assertEquals(uri, this.actualResponse.getHeaderString("Location")));
    verify(trajetService).addReservation(TRAJET_ID, PASSAGER_ID, RESERVATION_CREATE_REQUEST);
  }

  // ─── cancelReservation ──────────────────────────────────────────────

  @Test
  void givenExistingReservation_whenCancelReservation_thenReturn204AndServiceCalled() {
    this.actualResponse = trajetResource.cancelReservation(TRAJET_ID, RESERVATION_ID, PASSAGER_ID);

    assertEquals(Response.Status.NO_CONTENT.getStatusCode(), this.actualResponse.getStatus());
    verify(trajetService).cancelReservation(TRAJET_ID, RESERVATION_ID, PASSAGER_ID);
  }

  // ─── getReservations ────────────────────────────────────────────────

  @Test
  void givenExistingTrajet_whenGetReservations_thenReturn200WithList() {
    final List<ReservationResponse> expected =
        List.of(new ReservationResponse(RESERVATION_ID, PASSAGER_ID, 1));

    when(trajetService.getReservations(TRAJET_ID, CONDUCTEUR_ID)).thenReturn(expected);

    this.actualResponse = trajetResource.getReservations(TRAJET_ID, CONDUCTEUR_ID);

    assertAll(
        () -> assertEquals(Response.Status.OK.getStatusCode(), this.actualResponse.getStatus()),
        () -> assertEquals(expected, this.actualResponse.getEntity()));
    verify(trajetService).getReservations(TRAJET_ID, CONDUCTEUR_ID);
  }

  // ─── helpers ────────────────────────────────────────────────────────

  private TrajetResponse createTrajetResponse(
      final UUID id, final String depart, final String destination, final TrajetType type) {
    return new TrajetResponse(
        id,
        depart,
        destination,
        LocalDate.of(2026, 4, 1),
        LocalTime.of(8, 30),
        3,
        20.0,
        CONDUCTEUR_ID,
        type,
        List.of(),
        null,
        null);
  }
}
