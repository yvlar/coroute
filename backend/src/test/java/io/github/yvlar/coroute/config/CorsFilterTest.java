package io.github.yvlar.coroute.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CorsFilterTest {

  private static final String ALLOWED = "http://localhost:5173";
  private static final String ALSO_ALLOWED = "http://localhost";
  private static final String ORIGINS = ALLOWED + "," + ALSO_ALLOWED;

  @Mock private ContainerRequestContext requestContext;
  @Mock private ContainerResponseContext responseContext;

  private MultivaluedMap<String, Object> headers;
  private CorsFilter corsFilter;

  @BeforeEach
  void setUp() {
    this.headers = new MultivaluedHashMap<>();
    this.corsFilter = new CorsFilter(ORIGINS);
    when(responseContext.getHeaders()).thenReturn(headers);
  }

  // ─── parseOrigins ────────────────────────────────────────────────────

  @Test
  void givenListeAvecEspaces_whenParseOrigins_thenNettoieEtSepare() {
    final List<String> result = CorsFilter.parseOrigins(" http://a.com , http://b.com ");

    assertEquals(List.of("http://a.com", "http://b.com"), result);
  }

  @Test
  void givenValeurVide_whenParseOrigins_thenListeVide() {
    assertTrue(CorsFilter.parseOrigins("  ").isEmpty());
    assertTrue(CorsFilter.parseOrigins(null).isEmpty());
  }

  // ─── réponse : origine autorisée ─────────────────────────────────────

  @Test
  void givenOrigineAutorisee_whenFilter_thenRenvoieCetteOrigineEtVary() {
    when(requestContext.getHeaderString("Origin")).thenReturn(ALLOWED);

    corsFilter.filter(requestContext, responseContext);

    assertAll(
        () -> assertEquals(ALLOWED, headers.getFirst("Access-Control-Allow-Origin")),
        () -> assertTrue(headers.get("Vary").contains("Origin")),
        () ->
            assertEquals(
                "GET, POST, PUT, DELETE, OPTIONS",
                headers.getFirst("Access-Control-Allow-Methods")));
  }

  // ─── réponse : origine non autorisée ─────────────────────────────────

  @Test
  void givenOrigineNonAutorisee_whenFilter_thenAucunAllowOriginMaisVaryPresent() {
    when(requestContext.getHeaderString("Origin")).thenReturn("https://evil.example.com");

    corsFilter.filter(requestContext, responseContext);

    assertAll(
        () -> assertFalse(headers.containsKey("Access-Control-Allow-Origin")),
        () -> assertTrue(headers.get("Vary").contains("Origin")));
  }

  @Test
  void givenAucuneOrigine_whenFilter_thenAucunAllowOrigin() {
    when(requestContext.getHeaderString("Origin")).thenReturn(null);

    corsFilter.filter(requestContext, responseContext);

    assertFalse(headers.containsKey("Access-Control-Allow-Origin"));
  }

  @Test
  void givenAucuneOrigineConfiguree_whenFilter_thenNAutorisePersonne() {
    final CorsFilter sansOrigine = new CorsFilter("");
    when(requestContext.getHeaderString("Origin")).thenReturn(ALLOWED);

    sansOrigine.filter(requestContext, responseContext);

    assertFalse(headers.containsKey("Access-Control-Allow-Origin"));
  }

  // ─── requête : preflight OPTIONS ─────────────────────────────────────

  @Test
  void givenRequeteOptions_whenFilter_thenAbortWith200() {
    when(requestContext.getMethod()).thenReturn("OPTIONS");

    corsFilter.filter(requestContext);

    final ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
    verify(requestContext).abortWith(captor.capture());
    assertEquals(200, captor.getValue().getStatus());
  }
}
