package ca.ulaval.coroute.dto.request;

import jakarta.validation.constraints.Min;

public record ReservationCreateRequest(@Min(1) int nombrePlaces) {}
