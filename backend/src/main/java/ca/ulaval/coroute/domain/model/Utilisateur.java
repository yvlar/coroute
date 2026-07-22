package ca.ulaval.coroute.domain.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import java.util.UUID;

@Entity("utilisateurs")
public class Utilisateur {

    @Id
    private UUID id;

    @Property
    private String nom;

    @Property
    private String email;

    @Property
    private String motDePasseHash;

    /**
     * Constructeur vide requis par Morphia pour la désérialisation.
     * Ne pas utiliser directement - utiliser le constructeur avec paramètres.
     */
    protected Utilisateur() {
        // Required by Morphia for deserialization
    }

    public Utilisateur(final String nom, final String email, final String candidatMotDePasseHash) {
        this.id = UUID.randomUUID();
        this.nom = nom;
        this.email = email;
        this.motDePasseHash = candidatMotDePasseHash;
    }

    public boolean verifierMotDePasse(final String candidatMotDePasseHash) {
        return this.motDePasseHash.equals(candidatMotDePasseHash);
    }

    public UUID getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }
}
