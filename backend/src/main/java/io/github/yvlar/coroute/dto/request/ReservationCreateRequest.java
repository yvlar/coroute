package io.github.yvlar.coroute.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReservationCreateRequest(@Min(1) @Max(8) int nombrePlaces) {}
