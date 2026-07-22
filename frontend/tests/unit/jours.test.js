import { describe, it, expect } from 'vitest';
import {
  JOUR_LABEL_TO_ENUM,
  JOUR_ENUM_TO_LABEL,
  labelsToEnums,
  enumsToLabels,
} from '@/shared/constants/jours';

describe('JOUR_LABEL_TO_ENUM', () => {
  it('mappe Lun → LUNDI', () => {
    expect(JOUR_LABEL_TO_ENUM['Lun']).toBe('LUNDI');
  });
  it('mappe Ven → VENDREDI', () => {
    expect(JOUR_LABEL_TO_ENUM['Ven']).toBe('VENDREDI');
  });
  it('couvre tous les 7 jours', () => {
    expect(Object.keys(JOUR_LABEL_TO_ENUM)).toHaveLength(7);
  });
});

describe('JOUR_ENUM_TO_LABEL', () => {
  it('mappe LUNDI → Lun', () => {
    expect(JOUR_ENUM_TO_LABEL['LUNDI']).toBe('Lun');
  });
  it('mappe DIMANCHE → Dim', () => {
    expect(JOUR_ENUM_TO_LABEL['DIMANCHE']).toBe('Dim');
  });
});

describe('labelsToEnums', () => {
  it('convertit une liste de labels en enums', () => {
    expect(labelsToEnums(['Lun', 'Mer', 'Ven'])).toEqual([
      'LUNDI',
      'MERCREDI',
      'VENDREDI',
    ]);
  });

  it('ignore les labels inconnus', () => {
    expect(labelsToEnums(['Lun', 'INVALID'])).toEqual(['LUNDI']);
  });

  it('retourne un tableau vide si input vide', () => {
    expect(labelsToEnums([])).toEqual([]);
  });
});

describe('enumsToLabels', () => {
  it('convertit une liste d\'enums en labels', () => {
    expect(enumsToLabels(['LUNDI', 'MERCREDI'])).toEqual(['Lun', 'Mer']);
  });

  it('retourne un tableau vide si null', () => {
    expect(enumsToLabels(null)).toEqual([]);
  });

  it('retourne un tableau vide si undefined', () => {
    expect(enumsToLabels(undefined)).toEqual([]);
  });
});
