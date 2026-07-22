import React, { useState } from 'react';
import type { MatchingResponse } from '@/shared/types/trajet';
import { formatHeure } from '../utils/formatters';

interface ReservationModalProps {
  trajet: MatchingResponse;
  token: string;
  onClose: () => void;
  onConfirm: (trajet: MatchingResponse, nombrePlaces: number) => Promise<void>;
}

export function ReservationModal({ trajet, onClose, onConfirm }: ReservationModalProps) {
  const [nombrePlaces, setNombrePlaces] = useState(1);
  const [loading, setLoading] = useState(false);

  const max = trajet.placesRestantes;
  const total = (nombrePlaces * trajet.prixParPassager).toFixed(2);

  const handleConfirm = async () => {
    setLoading(true);
    await onConfirm(trajet, nombrePlaces);
    setLoading(false);
  };

  const handleOverlay = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  const handleOverlayKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.key === 'Escape') {
      onClose();
    }
  };

  return (
    <div
      className="modal-overlay"
      role="presentation"
      onClick={handleOverlay}
      onKeyDown={handleOverlayKeyDown}
    >
      <div className="modal" role="dialog" aria-modal="true">
        <button className="modal-close" onClick={onClose} aria-label="Fermer">
          ✕
        </button>

        <div className="modal-title">Confirmer la réservation</div>
        <div className="modal-sub">
          {trajet.depart} → {trajet.destination} · {formatHeure(trajet.heure)}
        </div>

        <div className="reservation-summary">
          <div className="reservation-row">
            <span>Prix par place</span>
            <strong>{trajet.prixParPassager} $</strong>
          </div>
          <div className="reservation-row">
            <span>Places disponibles</span>
            <strong>{trajet.placesRestantes}</strong>
          </div>
        </div>

        <div className="modal-field">
          <label className="modal-label" htmlFor="nombrePlaces">
            Nombre de places
          </label>
          <input
            id="nombrePlaces"
            className="modal-input"
            type="number"
            min={1}
            max={max}
            value={nombrePlaces}
            onChange={(e) =>
              setNombrePlaces(Math.min(max, Math.max(1, parseInt(e.target.value) || 1)))
            }
            disabled={loading}
          />
        </div>

        <div className="reservation-total">
          Total : <strong>{total} $</strong>
        </div>

        <button className="modal-submit" onClick={handleConfirm} disabled={loading}>
          {loading
            ? 'Réservation en cours...'
            : `Réserver ${nombrePlaces} place${nombrePlaces > 1 ? 's' : ''} →`}
        </button>
      </div>
    </div>
  );
}
