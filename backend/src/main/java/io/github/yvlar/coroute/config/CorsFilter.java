package io.github.yvlar.coroute.config;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.List;

/**
 * Filtre CORS basé sur une liste blanche d'origines.
 *
 * <p>Les origines autorisées sont fournies par la variable d'environnement {@code
 * CORS_ALLOWED_ORIGINS} (valeurs séparées par des virgules). Seule une origine reçue et présente
 * dans la liste est renvoyée dans l'en-tête {@code Access-Control-Allow-Origin} ; aucune origine
 * n'est autorisée par défaut. L'en-tête {@code Vary: Origin} est toujours ajouté.
 *
 * <p>Le filtre s'exécute avant l'authentification afin de répondre aux requêtes preflight {@code
 * OPTIONS} sans exiger de jeton.
 */
@Provider
@Priority(Priorities.AUTHENTICATION - 10)
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

  private static final String ORIGIN_HEADER = "Origin";
  private final List<String> allowedOrigins;

  public CorsFilter() {
    this(System.getenv("CORS_ALLOWED_ORIGINS"));
  }

  CorsFilter(final String configuredOrigins) {
    this.allowedOrigins = parseOrigins(configuredOrigins);
  }

  static List<String> parseOrigins(final String raw) {
    if (raw == null || raw.isBlank()) {
      return List.of();
    }
    return Arrays.stream(raw.split(","))
        .map(String::strip)
        .filter(origin -> !origin.isEmpty())
        .toList();
  }

  @Override
  public void filter(final ContainerRequestContext requestContext) {
    if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
      requestContext.abortWith(Response.ok().build());
    }
  }

  @Override
  public void filter(
      final ContainerRequestContext requestContext,
      final ContainerResponseContext responseContext) {
    responseContext.getHeaders().add(HttpHeaders.VARY, ORIGIN_HEADER);

    final String origin = requestContext.getHeaderString(ORIGIN_HEADER);
    if (origin == null || !this.allowedOrigins.contains(origin)) {
      return;
    }

    responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
    responseContext
        .getHeaders()
        .add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    responseContext
        .getHeaders()
        .add("Access-Control-Allow-Headers", "Authorization, Content-Type, X-User-Id");
    responseContext.getHeaders().add("Access-Control-Max-Age", "3600");
  }
}
