package io.github.yvlar.coroute.dto.response;

/** Informations publiques d'un utilisateur, renvoyées à la connexion. */
public record UtilisateurResponse(String id, String nom, String email) {}
