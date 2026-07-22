package ca.ulaval.coroute.config;

import ca.ulaval.coroute.domain.service.JwtService;
import jakarta.annotation.Priority;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

  public static final String USER_AUTH_HEADER = "X-User-Id";
  private static final String BEARER_PREFIX = "Bearer ";

  @Context private ResourceInfo resourceInfo;

  @Inject private JwtService jwtService;

  @Override
  public void filter(final ContainerRequestContext requestContext) {
    final boolean isPermitAll =
        resourceInfo.getResourceMethod() != null
            && resourceInfo.getResourceMethod().isAnnotationPresent(PermitAll.class);

    if (isPermitAll) {
      return;
    }

    final String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      requestContext.abortWith(
          Response.status(Response.Status.UNAUTHORIZED)
              .entity("Token JWT manquant ou invalide.")
              .build());
      return;
    }

    final String token = authHeader.substring(BEARER_PREFIX.length());

    if (!jwtService.estValide(token)) {
      requestContext.abortWith(
          Response.status(Response.Status.UNAUTHORIZED)
              .entity("Token JWT expiré ou invalide.")
              .build());
      return;
    }

    // Injecte l'userId dans le header pour que les controllers puissent l'utiliser
    final String userId = jwtService.extraireUtilisateurId(token);
    requestContext.getHeaders().putSingle(USER_AUTH_HEADER, userId);
  }
}
