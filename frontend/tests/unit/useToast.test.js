import { describe, it, expect } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useToast } from '@/shared/hooks/useToast';

describe('useToast', () => {
  it('toast est null par défaut', () => {
    const { result } = renderHook(() => useToast());
    expect(result.current.toast).toBeNull();
  });

  it('showToast met à jour le toast avec message et type', () => {
    const { result } = renderHook(() => useToast());
    act(() => {
      result.current.showToast('Réservation confirmée', 'success');
    });
    expect(result.current.toast).toEqual({ message: 'Réservation confirmée', type: 'success' });
  });

  it('showToast utilise "success" comme type par défaut', () => {
    const { result } = renderHook(() => useToast());
    act(() => {
      result.current.showToast('Bienvenue !');
    });
    expect(result.current.toast?.type).toBe('success');
  });

  it('clearToast remet toast à null', () => {
    const { result } = renderHook(() => useToast());
    act(() => {
      result.current.showToast('Test', 'error');
    });
    act(() => {
      result.current.clearToast();
    });
    expect(result.current.toast).toBeNull();
  });
});
