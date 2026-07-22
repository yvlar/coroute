import { describe, it, expect } from 'vitest';
import { getTrajetStatus } from '@/features/trajets/utils/trajetStatus';

describe('getTrajetStatus', () => {
  it('retourne le statut "Complet" (rouge) quand placesRestantes === 0', () => {
    const status = getTrajetStatus(0);
    expect(status.label).toBe('Complet');
    expect(status.color).toBe('#EB5757');
    expect(status.badgeColor).toBe('#b91c1c');
  });

  it('retourne "1 place" (jaune) quand placesRestantes === 1', () => {
    const status = getTrajetStatus(1);
    expect(status.label).toBe('1 place');
    expect(status.color).toBe('#F2C94C');
    expect(status.badgeColor).toBe('#92400e');
  });

  it('retourne "N places" (vert) quand placesRestantes > 1', () => {
    const status = getTrajetStatus(3);
    expect(status.label).toBe('3 places');
    expect(status.color).toBe('#27AE60');
    expect(status.badgeColor).toBe('#166534');
  });

  it('inclut toutes les propriétés requises', () => {
    const status = getTrajetStatus(2);
    expect(status).toHaveProperty('color');
    expect(status).toHaveProperty('label');
    expect(status).toHaveProperty('badgeBg');
    expect(status).toHaveProperty('badgeColor');
  });

  it('fonctionne avec des valeurs élevées', () => {
    const status = getTrajetStatus(10);
    expect(status.label).toBe('10 places');
    expect(status.color).toBe('#27AE60');
  });
});
