package ca.ulaval.coroute.domain.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import java.util.UUID;

@Entity
public class Reservation {

    @Id
    private UUID id;

    @Property
    private String passagerId;

    @Property
    private int nombrePlaces;

    /**
     * Constructeur vide requis par Morphia pour la désérialisation.
     * Ne pas utiliser directement - utiliser le constructeur avec paramètres.
     */
    protected Reservation() {
        // Required by Morphia for deserialization
    }

    public Reservation(final String candidatPassagerId, final int nombrePlaces) {
        this.id = UUID.randomUUID();
        this.passagerId = candidatPassagerId;
        this.nombrePlaces = nombrePlaces;
    }

    public boolean appartientA(final String candidatPassagerId) {
        return this.passagerId.equals(candidatPassagerId);
    }

    public UUID getId() {
        return id;
    }

    public String getPassagerId() {
        return passagerId;
    }

    public int getNombrePlaces() {
        return nombrePlaces;
    }
}
