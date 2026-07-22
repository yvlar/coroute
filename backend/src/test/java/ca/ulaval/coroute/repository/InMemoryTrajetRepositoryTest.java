package ca.ulaval.coroute.repository;

import ca.ulaval.coroute.domain.model.Trajet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class InMemoryTrajetRepositoryTest {

    private static final UUID TRAJET_ID = UUID.randomUUID();
    private static final String DEPART = "Québec";
    private static final String DESTINATION = "Montréal";
    private static final String DATE = "2026-04-01";

    @Mock
    private Trajet trajetMock;
    @Mock
    private Trajet trajetMock2;

    private InMemoryTrajetRepository repository;

    @BeforeEach
    void setUp() {
        this.repository = new InMemoryTrajetRepository();
    }

    // ─── save / findById ────────────────────────────────────────────────

    @Test
    void givenTrajet_whenSave_thenTrouvableParId() {
        when(trajetMock.getId()).thenReturn(TRAJET_ID);

        repository.save(trajetMock);

        final Optional<Trajet> result = repository.findById(TRAJET_ID);
        assertTrue(result.isPresent());
        assertEquals(trajetMock, result.get());
    }

    @Test
    void givenIdInexistant_whenFindById_thenRetourneEmpty() {
        final Optional<Trajet> result = repository.findById(UUID.randomUUID());
        assertFalse(result.isPresent());
    }

    // ─── findAll ────────────────────────────────────────────────────────

    @Test
    void givenTrajetsEnregistres_whenFindAll_thenRetourneTous() {
        when(trajetMock.getId()).thenReturn(TRAJET_ID);
        when(trajetMock2.getId()).thenReturn(UUID.randomUUID());

        repository.save(trajetMock);
        repository.save(trajetMock2);

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void givenAucunTrajet_whenFindAll_thenRetourneListeVide() {
        assertTrue(repository.findAll().isEmpty());
    }

    // ─── findByFiltres ───────────────────────────────────────────────────

    @Test
    void givenFiltreDepart_whenFindByFiltres_thenRetourneTrajetsCorrespondants() {
        when(trajetMock.getId()).thenReturn(TRAJET_ID);
        when(trajetMock.getDepart()).thenReturn(DEPART);

        repository.save(trajetMock);

        final List<Trajet> result = repository.findByFiltres(DEPART, null, null);
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(trajetMock, result.get(0))
        );
    }

    @Test
    void givenFiltreDestination_whenFindByFiltres_thenRetourneTrajetsCorrespondants() {
        when(trajetMock.getId()).thenReturn(TRAJET_ID);
        when(trajetMock.getDestination()).thenReturn(DESTINATION);

        repository.save(trajetMock);

        final List<Trajet> result = repository.findByFiltres(null, DESTINATION, null);
        assertEquals(1, result.size());
    }

    @Test
    void givenFiltreDate_whenFindByFiltres_thenRetourneTrajetsCorrespondants() {
        when(trajetMock.getId()).thenReturn(TRAJET_ID);
        when(trajetMock.getDate()).thenReturn(LocalDate.parse(DATE));

        repository.save(trajetMock);

        final List<Trajet> result = repository.findByFiltres(null, null, DATE);
        assertEquals(1, result.size());
    }

    @Test
    void givenTousFiltres_whenFindByFiltres_thenRetourneTrajetExact() {
        when(trajetMock.getId()).thenReturn(TRAJET_ID);
        when(trajetMock.getDepart()).thenReturn(DEPART);
        when(trajetMock.getDestination()).thenReturn(DESTINATION);
        when(trajetMock.getDate()).thenReturn(LocalDate.parse(DATE));

        repository.save(trajetMock);

        final List<Trajet> result = repository.findByFiltres(DEPART, DESTINATION, DATE);
        assertEquals(1, result.size());
    }

    @Test
    void givenFiltreNonCorrespondant_whenFindByFiltres_thenRetourneListeVide() {
        when(trajetMock.getId()).thenReturn(TRAJET_ID);
        when(trajetMock.getDepart()).thenReturn(DEPART);

        repository.save(trajetMock);

        final List<Trajet> result = repository.findByFiltres("Sherbrooke", null, null);
        assertTrue(result.isEmpty());
    }

    // ─── delete ─────────────────────────────────────────────────────────

    @Test
    void givenTrajetExistant_whenDelete_thenPlusTrouvable() {
        when(trajetMock.getId()).thenReturn(TRAJET_ID);

        repository.save(trajetMock);
        repository.delete(TRAJET_ID);

        assertFalse(repository.findById(TRAJET_ID).isPresent());
    }

    @Test
    void givenTrajetExistant_whenDelete_thenFindAllRetourneMoins() {
        when(trajetMock.getId()).thenReturn(TRAJET_ID);

        repository.save(trajetMock);
        repository.delete(TRAJET_ID);

        assertTrue(repository.findAll().isEmpty());
    }

    

    
}