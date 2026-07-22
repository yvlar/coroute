import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { JoursPicker } from '@/shared/components/JoursPicker';

describe('JoursPicker', () => {
  it('affiche tous les jours de la semaine', () => {
    render(<JoursPicker selected={[]} onChange={() => {}} />);
    for (const jour of ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim']) {
      expect(screen.getByText(jour)).toBeInTheDocument();
    }
  });

  it('les jours sélectionnés ont aria-pressed=true', () => {
    render(<JoursPicker selected={['Lun', 'Mer']} onChange={() => {}} />);
    expect(screen.getByText('Lun')).toHaveAttribute('aria-pressed', 'true');
    expect(screen.getByText('Mer')).toHaveAttribute('aria-pressed', 'true');
    expect(screen.getByText('Mar')).toHaveAttribute('aria-pressed', 'false');
  });

  it('appelle onChange avec le jour ajouté au clic sur un jour non sélectionné', () => {
    const onChange = vi.fn();
    render(<JoursPicker selected={['Lun']} onChange={onChange} />);
    fireEvent.click(screen.getByText('Mar'));
    expect(onChange).toHaveBeenCalledWith(['Lun', 'Mar']);
  });

  it('appelle onChange sans le jour au clic sur un jour déjà sélectionné', () => {
    const onChange = vi.fn();
    render(<JoursPicker selected={['Lun', 'Mer']} onChange={onChange} />);
    fireEvent.click(screen.getByText('Lun'));
    expect(onChange).toHaveBeenCalledWith(['Mer']);
  });
});
