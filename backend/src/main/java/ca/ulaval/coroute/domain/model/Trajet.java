package ca.ulaval.coroute.domain.model;

import ca.ulaval.coroute.domain.exception.AccesInterditException;
import ca.ulaval.coroute.domain.exception.PlacesInsuffisantesException;
import ca.ulaval.coroute.domain.exception.ReservationNotFoundException;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity("trajets")
public class Trajet {

    @Id
    private UUID id;
    @Property
    private String conducteurId;
    @Property
    private String depart;
    @Property
    private String destination;
    @Property
    private LocalDate date;
    @Property
    private LocalTime heure;
    @Property
    private int placesDisponibles;
    @Property
    private double prixParPassager;
    @Property
    private TrajetType type;
    @Property
    private List<JourSemaine> joursRecurrence;
    @Property
    private LocalDate dateDebut;
    @Property
    private LocalDate dateFin;
    @Property
    private List<Reservation> reservations;

    private transient ReservationFactory reservationFactory;

    /**
     * Constructeur vide requis par Morphia pour la désérialisation.
     * Ne pas utiliser directement - utiliser le constructeur avec paramètres.
     */
    protected Trajet() {
        // Required by Morphia for deserialization
        this.reservations = new ArrayList<>();
        this.joursRecurrence = new ArrayList<>();
    }

    public Trajet(
            final String conducteurId,
            final String depart,
            final String destination,
            final LocalDate date,
            final LocalTime heure,
            final int placesDisponibles,
            final double prixParPassager,
            final TrajetType type,
            final List<JourSemaine> joursRecurrence,
            final LocalDate dateDebut,
            final LocalDate dateFin,
            final ReservationFactory reservationFactory) {
        this.id = UUID.randomUUID();
        this.conducteurId = conducteurId;
        this.depart = depart;
        this.destination = destination;
        this.date = date;
        this.heure = heure;
        this.placesDisponibles = placesDisponibles;
        this.prixParPassager = prixParPassager;
        this.type = type;
        this.joursRecurrence = joursRecurrence != null ? joursRecurrence : new ArrayList<>();
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.reservations = new ArrayList<>();
        this.reservationFactory = reservationFactory;
    }

    public void setReservationFactory(final ReservationFactory reservationFactory) {
        this.reservationFactory = reservationFactory;
    }

    public UUID ajouterReservation(final String passagerId, final int nombrePlaces) {
        if (nombrePlaces > this.placesDisponibles) {
            throw new PlacesInsuffisantesException(this.placesDisponibles, nombrePlaces);
        }
        final Reservation reservation = this.reservationFactory.creer(passagerId, nombrePlaces);
        this.reservations.add(reservation);
        this.placesDisponibles -= nombrePlaces;
        return reservation.getId();
    }

    public void annulerReservation(final UUID reservationId, final String candidatPassagerId) {
        final Reservation reservation = this.trouverReservation(reservationId);
        if (!reservation.appartientA(candidatPassagerId)) {
            throw new AccesInterditException("annuler la réservation d'un autre passager");
        }
        this.reservations.remove(reservation);
        this.placesDisponibles += reservation.getNombrePlaces();
    }

    public List<Reservation> getReservations(final String candidatConducteurId) {
        verifierProprietaire(candidatConducteurId);
        return Collections.unmodifiableList(this.reservations);
    }

    public void verifierProprietaire(final String candidatConducteurId) {
        if (!this.conducteurId.equals(candidatConducteurId)) {
            throw new AccesInterditException("accéder aux réservations d'un trajet dont vous n'êtes pas le conducteur");
        }
    }

    public boolean estRegulier() {
        return TrajetType.REGULIER.equals(this.type);
    }

    private Reservation trouverReservation(final UUID reservationId) {
        return this.reservations.stream()
                .filter(r -> r.getId().equals(reservationId))
                .findFirst()
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));
    }

    public UUID getId() {
        return id;
    }

    public String getConducteurId() {
        return conducteurId;
    }

    public String getDepart() {
        return depart;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public int getPlacesDisponibles() {
        return placesDisponibles;
    }

    public double getPrixParPassager() {
        return prixParPassager;
    }

    public TrajetType getType() {
        return type;
    }

    public List<JourSemaine> getJoursRecurrence() {
        return Collections.unmodifiableList(joursRecurrence);
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }
}
