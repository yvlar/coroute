package ca.ulaval.coroute.domain.service;

import ca.ulaval.coroute.dto.request.ReservationCreateRequest;
import ca.ulaval.coroute.dto.request.TrajetCreateRequest;
import ca.ulaval.coroute.dto.response.ReservationResponse;
import ca.ulaval.coroute.dto.response.TrajetResponse;
import java.util.List;
import java.util.UUID;

public interface TrajetService {

    List<TrajetResponse> findAll(String depart, String destination, String date);

    TrajetResponse findById(UUID trajetId);

    UUID createTrajet(String conducteurId, TrajetCreateRequest request);

    void delete(UUID trajetId, String conducteurId);

    UUID addReservation(UUID trajetId, String passagerId, ReservationCreateRequest request);

    void cancelReservation(UUID trajetId, UUID reservationId, String passagerId);

    List<ReservationResponse> getReservations(UUID trajetId, String conducteurId);
}
