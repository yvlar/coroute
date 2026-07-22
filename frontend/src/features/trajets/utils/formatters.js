/**
 * Formate une heure ISO (ex. "07:00:00") retournée par le backend en "07h00".
 * @param {string} heureIso
 * @returns {string}
 */
export function formatHeure(heureIso) {
  if (!heureIso) {
    return '';
  }
  const [h, m] = heureIso.split(':');
  return `${h}h${m}`;
}

/**
 * Formate une date ISO (ex. "2024-06-15") en date courte française.
 * @param {string} dateIso
 * @returns {string}
 */
export function formatDate(dateIso) {
  if (!dateIso) {
    return '';
  }
  return new Date(dateIso).toLocaleDateString('fr-CA', {
    day: 'numeric',
    month: 'short',
  });
}
