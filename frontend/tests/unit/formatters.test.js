import { describe, it, expect } from 'vitest';
import { formatHeure, formatDate } from '@/features/trajets/utils/formatters';

describe('formatHeure', () => {
  it('convertit "07:00:00" en "07h00"', () => {
    expect(formatHeure('07:00:00')).toBe('07h00');
  });

  it('convertit "14:30:00" en "14h30"', () => {
    expect(formatHeure('14:30:00')).toBe('14h30');
  });

  it('retourne une chaîne vide si undefined', () => {
    expect(formatHeure(undefined)).toBe('');
  });

  it('retourne une chaîne vide si null', () => {
    expect(formatHeure(null)).toBe('');
  });
});

describe('formatDate', () => {
  it('retourne une chaîne non vide pour une date ISO valide', () => {
    const result = formatDate('2024-06-15');
    expect(result).toBeTruthy();
    expect(typeof result).toBe('string');
  });

  it('retourne une chaîne vide si null', () => {
    expect(formatDate(null)).toBe('');
  });

  it('retourne une chaîne vide si undefined', () => {
    expect(formatDate(undefined)).toBe('');
  });
});
