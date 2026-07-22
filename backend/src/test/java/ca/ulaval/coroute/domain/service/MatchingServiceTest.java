package ca.ulaval.coroute.domain.service;

import ca.ulaval.coroute.domain.model.JourSemaine;
import ca.ulaval.coroute.domain.model.Trajet;
import ca.ulaval.coroute.domain.model.TrajetType;
import ca.ulaval.coroute.dto.request.MatchingRequest;
import ca.ulaval.coroute.dto.response.MatchingResponse;
import ca.ulaval.coroute.repository.TrajetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MatchingServiceTest {

    private static final String DEPART = "Roxton";
    private static final String DESTINATION = "Drummondville";

    @Mock
    private TrajetRepository trajetRepository;
    @Mock
    private Trajet trajetRegulierMock;
    @Mock
    private Trajet trajetPonctuelMock;
    @Mock
    private Trajet trajetSansPlaceMock;

    @InjectMocks
    private MatchingService matchingService;

    @BeforeEach
    void setUp() {
        // Trajet régulier Lun-Ven
        when(trajetRegulierMock.getId()).thenReturn(UUID.randomUUID());
        when(trajetRegulierMock.getDepart()).thenReturn(DEPART);
        when(trajetRegulierMock.getDestination()).thenReturn(DESTINATION);
        when(trajetRegulierMock.getHeure()).thenReturn(LocalTime.of(7, 15));
        when(trajetRegulierMock.getPrixParPassager()).thenReturn(8.0);
        when(trajetRegulierMock.getPlacesDisponibles()).thenReturn(2);
        when(trajetRegulierMock.getType()).thenReturn(TrajetType.REGULIER);
        when(trajetRegulierMock.getJoursRecurrence()).thenReturn(
                List.of(JourSemaine.LUNDI, JourSemaine.MARDI, JourSemaine.MERCREDI,
                        JourSemaine.JEUDI, JourSemaine.VENDREDI));
        when(trajetRegulierMock.getDate()).thenReturn(null);
        when(trajetRegulierMock.getDateDebut()).thenReturn(LocalDate.of(2026, 4, 1));
        when(trajetRegulierMock.getDateFin()).thenReturn(LocalDate.of(2026, 6, 30));
        when(trajetRegulierMock.getConducteurId()).thenReturn("conducteur-123");

        // Trajet ponctuel
        when(trajetPonctuelMock.getId()).thenReturn(UUID.randomUUID());
        when(trajetPonctuelMock.getDepart()).thenReturn(DEPART);
        when(trajetPonctuelMock.getDestination()).thenReturn(DESTINATION);
        when(trajetPonctuelMock.getHeure()).thenReturn(LocalTime.of(8, 30));
        when(trajetPonctuelMock.getPrixParPassager()).thenReturn(15.0);
        when(trajetPonctuelMock.getPlacesDisponibles()).thenReturn(3);
        when(trajetPonctuelMock.getType()).thenReturn(TrajetType.PONCTUEL);
        when(trajetPonctuelMock.getJoursRecurrence()).thenReturn(List.of());
        when(trajetPonctuelMock.getDate()).thenReturn(LocalDate.of(2026, 4, 15));
        when(trajetPonctuelMock.getDateDebut()).thenReturn(null);
        when(trajetPonctuelMock.getDateFin()).thenReturn(null);
        when(trajetPonctuelMock.getConducteurId()).thenReturn("conducteur-456");

        // Trajet sans place
        when(trajetSansPlaceMock.getDepart()).thenReturn(DEPART);
        when(trajetSansPlaceMock.getDestination()).thenReturn(DESTINATION);
        when(trajetSansPlaceMock.getPlacesDisponibles()).thenReturn(0);
        when(trajetSansPlaceMock.getType()).thenReturn(TrajetType.REGULIER);
        when(trajetSansPlaceMock.getJoursRecurrence()).thenReturn(List.of(JourSemaine.LUNDI));
    }

    // ─── trouverMatches ──────────────────────────────────────────────────

    @Test
    void givenTrajetsDisponibles_whenTrouverMatches_thenRetourneTrajetsCompatibles() {
        when(trajetRepository.findAll()).thenReturn(List.of(trajetRegulierMock, trajetPonctuelMock));

        final MatchingRequest request = new MatchingRequest(DEPART, DESTINATION, List.of(JourSemaine.LUNDI));

        final List<MatchingResponse> result = matchingService.trouverMatches(request);

        assertEquals(2, result.size());
    }

    @Test
    void givenTrajetSansPlace_whenTrouverMatches_thenExcluTrajetSansPlace() {
        when(trajetRepository.findAll()).thenReturn(List.of(trajetSansPlaceMock));

        final MatchingRequest request = new MatchingRequest(DEPART, DESTINATION, List.of(JourSemaine.LUNDI));

        final List<MatchingResponse> result = matchingService.trouverMatches(request);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenDepartDifferent_whenTrouverMatches_thenExcluTrajet() {
        when(trajetRepository.findAll()).thenReturn(List.of(trajetRegulierMock));

        final MatchingRequest request = new MatchingRequest("Quebec", DESTINATION, List.of());

        final List<MatchingResponse> result = matchingService.trouverMatches(request);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenDestinationDifferente_whenTrouverMatches_thenExcluTrajet() {
        when(trajetRepository.findAll()).thenReturn(List.of(trajetRegulierMock));

        final MatchingRequest request = new MatchingRequest(DEPART, "Montreal", List.of());

        final List<MatchingResponse> result = matchingService.trouverMatches(request);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenAucunFiltre_whenTrouverMatches_thenRetourneTous() {
        when(trajetRepository.findAll()).thenReturn(List.of(trajetRegulierMock, trajetPonctuelMock));

        final MatchingRequest request = new MatchingRequest(null, null, null);

        final List<MatchingResponse> result = matchingService.trouverMatches(request);

        assertEquals(2, result.size());
    }

    @Test
    void givenAucunTrajet_whenTrouverMatches_thenRetourneListeVide() {
        when(trajetRepository.findAll()).thenReturn(List.of());

        final MatchingRequest request = new MatchingRequest(DEPART, DESTINATION, List.of(JourSemaine.LUNDI));

        final List<MatchingResponse> result = matchingService.trouverMatches(request);

        assertTrue(result.isEmpty());
    }

    // ─── score compatibilite ─────────────────────────────────────────────

    @Test
    void givenTrajetRegulier_whenTrouverMatches_thenScoreSuperieurAuPonctuel() {
        when(trajetRepository.findAll()).thenReturn(List.of(trajetPonctuelMock, trajetRegulierMock));

        final MatchingRequest request = new MatchingRequest(DEPART, DESTINATION, List.of(JourSemaine.LUNDI));

        final List<MatchingResponse> result = matchingService.trouverMatches(request);

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertTrue(result.get(0).scoreCompatibilite() >= result.get(1).scoreCompatibilite())
        );
    }

    @Test
    void givenJoursCompatibles_whenTrouverMatches_thenJoursCompatiblesRetournes() {
        when(trajetRepository.findAll()).thenReturn(List.of(trajetRegulierMock));

        final MatchingRequest request = new MatchingRequest(
                DEPART, DESTINATION, List.of(JourSemaine.LUNDI, JourSemaine.SAMEDI));

        final List<MatchingResponse> result = matchingService.trouverMatches(request);

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertTrue(result.get(0).joursCompatibles().contains(JourSemaine.LUNDI)),
                () -> assertFalse(result.get(0).joursCompatibles().contains(JourSemaine.SAMEDI))
        );
    }

    // ─── jours incompatibles ─────────────────────────────────────────────

    @Test
    void givenJoursIncompatibles_whenTrouverMatches_thenExcluTrajetRegulier() {
        when(trajetRepository.findAll()).thenReturn(List.of(trajetRegulierMock));

        final MatchingRequest request = new MatchingRequest(
                DEPART, DESTINATION, List.of(JourSemaine.SAMEDI, JourSemaine.DIMANCHE));

        final List<MatchingResponse> result = matchingService.trouverMatches(request);

        assertTrue(result.isEmpty());
    }
}
