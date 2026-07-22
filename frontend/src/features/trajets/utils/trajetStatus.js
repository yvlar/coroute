/**
 * Retourne la couleur, le label et le style de badge selon le nombre de places disponibles.
 * Utilise le champ `placesRestantes` de TrajetResponse / MatchingResponse.
 * @param {number} placesRestantes
 * @returns {{ color: string, label: string, badgeBg: string, badgeColor: string }}
 */
export function getTrajetStatus(placesRestantes) {
  if (placesRestantes === 0) {
    return {
      color: '#EB5757',
      label: 'Complet',
      badgeBg: 'rgba(235,87,87,0.12)',
      badgeColor: '#b91c1c',
    };
  }
  if (placesRestantes === 1) {
    return {
      color: '#F2C94C',
      label: '1 place',
      badgeBg: 'rgba(242,201,76,0.18)',
      badgeColor: '#92400e',
    };
  }
  return {
    color: '#27AE60',
    label: `${placesRestantes} places`,
    badgeBg: 'rgba(39,174,96,0.12)',
    badgeColor: '#166534',
  };
}
