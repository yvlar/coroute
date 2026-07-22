package ca.ulaval.coroute.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InscriptionRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,
        @Email(message = "L'adresse courriel est invalide")
        @NotBlank(message = "L'adresse courriel est obligatoire")
        String email,
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
        String motDePasse) {
}
