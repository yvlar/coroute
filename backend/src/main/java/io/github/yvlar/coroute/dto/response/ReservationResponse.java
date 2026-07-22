package io.github.yvlar.coroute.dto.response;

import java.util.UUID;

public record ReservationResponse(UUID id, String passagerId, int nombrePlaces) {}
