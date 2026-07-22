package ca.ulaval.coroute.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.coroute.domain.service.JwtService;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Response;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthenticationFilterTest {

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyLTEyMyJ9.abc";
    private static final String INVALID_TOKEN = "token.invalide.ici";

    @InjectMocks
    private AuthenticationFilter authenticationFilter;

    @Mock
    private JwtService jwtService;

    @Mock
    private ResourceInfo resourceInfo;

    @Mock
    private ContainerRequestContext requestContext;

    @Mock
    private Method method;

    @BeforeEach
    void setUp() {
        when(this.resourceInfo.getResourceMethod()).thenReturn(this.method);
    }

    // ─── @PermitAll ──────────────────────────────────────────────────────

    @Test
    void givenMethodWithPermitAll_whenFilter_thenAucuneValidation() {
        when(this.method.isAnnotationPresent(PermitAll.class)).thenReturn(true);

        this.authenticationFilter.filter(this.requestContext);

        verify(this.requestContext, never()).abortWith(any());
    }

    // ─── Token manquant ──────────────────────────────────────────────────

    @Test
    void givenHeaderAbsent_whenFilter_thenAbortAvec401() {
        when(this.method.isAnnotationPresent(PermitAll.class)).thenReturn(false);
        when(this.requestContext.getHeaderString("Authorization")).thenReturn(null);

        this.authenticationFilter.filter(this.requestContext);

        verify(this.requestContext).abortWith(any(Response.class));
    }

    @Test
    void givenHeaderSansBearer_whenFilter_thenAbortAvec401() {
        when(this.method.isAnnotationPresent(PermitAll.class)).thenReturn(false);
        when(this.requestContext.getHeaderString("Authorization")).thenReturn("Basic sometoken");

        this.authenticationFilter.filter(this.requestContext);

        verify(this.requestContext).abortWith(any(Response.class));
    }

    // ─── Token invalide ──────────────────────────────────────────────────

    @Test
    void givenTokenInvalide_whenFilter_thenAbortAvec401() {
        when(this.method.isAnnotationPresent(PermitAll.class)).thenReturn(false);
        when(this.requestContext.getHeaderString("Authorization"))
                .thenReturn("Bearer " + INVALID_TOKEN);
        when(this.jwtService.estValide(INVALID_TOKEN)).thenReturn(false);

        this.authenticationFilter.filter(this.requestContext);

        verify(this.requestContext).abortWith(any(Response.class));
    }

    // ─── Token valide ────────────────────────────────────────────────────

    @Test
    void givenTokenValide_whenFilter_thenAucunAbort() {
        when(this.method.isAnnotationPresent(PermitAll.class)).thenReturn(false);
        when(this.requestContext.getHeaderString("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(this.jwtService.estValide(VALID_TOKEN)).thenReturn(true);
        when(this.jwtService.extraireUtilisateurId(VALID_TOKEN)).thenReturn("user-123");
        when(this.requestContext.getHeaders())
                .thenReturn(new jakarta.ws.rs.core.MultivaluedHashMap<>());

        this.authenticationFilter.filter(this.requestContext);

        verify(this.requestContext, never()).abortWith(any());
    }
}
