package io.github.yvlar.coroute.domain.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Property;
import java.util.Locale;
import java.util.UUID;

@Entity("utilisateurs")
public class Utilisateur {

  @Id private UUID id;

  @Property private String nom;

  @Indexed(options = @IndexOptions(unique = true, name = "uq_utilisateurs_email"))
  @Property
  private String email;

  @Property private String motDePasseHash;

  /**
   * Constructeur vide requis par Morphia pour la désérialisation. Ne pas utiliser directement -
   * utiliser le constructeur avec paramètres.
   */
  protected Utilisateur() {
    // Required by Morphia for deserialization
  }

  public Utilisateur(final String nom, final String email, final String candidatMotDePasseHash) {
    this.id = UUID.randomUUID();
    this.nom = nom.strip();
    this.email = normaliserEmail(email);
    this.motDePasseHash = candidatMotDePasseHash;
  }

  public static String normaliserEmail(final String email) {
    return email.strip().toLowerCase(Locale.ROOT);
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
