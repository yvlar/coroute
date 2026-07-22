package io.github.yvlar.coroute.dto.response;

/** Réponse de connexion : le jeton JWT et les informations de l'utilisateur authentifié. */
public record TokenResponse(String token, UtilisateurResponse utilisateur) {}
