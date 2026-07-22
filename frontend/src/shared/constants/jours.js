/** Labels courts affichés dans l'UI */
export const JOURS_LABELS = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'];
export const JOURS_SEMAINE = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven'];

/** Correspondance label UI → valeur enum JourSemaine du backend */
export const JOUR_LABEL_TO_ENUM = {
  Lun: 'LUNDI',
  Mar: 'MARDI',
  Mer: 'MERCREDI',
  Jeu: 'JEUDI',
  Ven: 'VENDREDI',
  Sam: 'SAMEDI',
  Dim: 'DIMANCHE',
};

/** Correspondance enum JourSemaine → label court UI */
export const JOUR_ENUM_TO_LABEL = {
  LUNDI: 'Lun',
  MARDI: 'Mar',
  MERCREDI: 'Mer',
  JEUDI: 'Jeu',
  VENDREDI: 'Ven',
  SAMEDI: 'Sam',
  DIMANCHE: 'Dim',
};

/**
 * Convertit une liste de labels UI en valeurs enum pour l'API.
 * @param {string[]} labels - ex. ['Lun', 'Mer']
 * @returns {string[]} - ex. ['LUNDI', 'MERCREDI']
 */
export const labelsToEnums = (labels) => labels.map((l) => JOUR_LABEL_TO_ENUM[l]).filter(Boolean);

/**
 * Convertit une liste d'enums backend en labels courts UI.
 * @param {string[]} enums - ex. ['LUNDI', 'MERCREDI']
 * @returns {string[]} - ex. ['Lun', 'Mer']
 */
export const enumsToLabels = (enums) =>
  (enums || []).map((e) => JOUR_ENUM_TO_LABEL[e]).filter(Boolean);
