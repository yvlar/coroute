import { useEffect } from 'react';

const ICONS = { success: '✅', error: '❌', info: 'ℹ️' };
const AUTO_CLOSE_MS = 3500;

export function Toast({ message, type = 'info', onClose }) {
  useEffect(() => {
    const timer = setTimeout(onClose, AUTO_CLOSE_MS);
    return () => clearTimeout(timer);
  }, [onClose]);

  return (
    <div className={`toast ${type}`} role="alert" aria-live="polite" aria-atomic="true">
      <span aria-hidden="true">{ICONS[type]}</span>
      <span>{message}</span>
    </div>
  );
}
