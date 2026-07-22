import { useState, useCallback } from 'react';
import { Navbar } from '@/shared/components/Navbar/Navbar';
import { Hero } from '@/shared/components/Hero/Hero';
import { Toast } from '@/shared/components/Toast/Toast';
import { AuthModal } from '@/features/auth';
import { ResultsSection } from '@/features/trajets';
import { CreateTrajetModal } from '@/features/trajets/components/CreateTrajetModal';
import { ReservationModal } from '@/features/trajets/components/ReservationModal';
import { RoutesFixesSection } from '@/features/routes';
import { useToast } from '@/shared/hooks/useToast';
import { useSearch } from '@/shared/hooks/useSearch';
import { useAuth } from '@/shared/hooks/useAuth';
import { createReservation, ApiError } from '@/shared/services/api';

export default function App() {
  const { user, token, login, register, logout } = useAuth();
  const [authModal, setAuthModal] = useState(null);
  const [showCreateTrajet, setShowCreateTrajet] = useState(false);
  const [trajetAReserver, setTrajetAReserver] = useState(null);
  const { toast, showToast, clearToast } = useToast();
  const {
    searchResults,
    searchQuery,
    loading,
    error,
    handleSearch,
    handleSelectRoute,
    resetSearch,
  } = useSearch();

  const isResultView = searchResults !== null;

  // Ouvre le modal de réservation (ou demande connexion)
  const handleReserve = useCallback(
    (trajet) => {
      if (!user || !token) {
        showToast('Connectez-vous pour réserver un trajet.', 'error');
        setAuthModal('login');
        return;
      }
      setTrajetAReserver(trajet);
    },
    [user, token, showToast]
  );

  // Confirme la réservation après sélection du nombre de places
  const handleConfirmReservation = useCallback(
    async (trajet, nombrePlaces) => {
      try {
        await createReservation(trajet.id, { nombrePlaces }, token);
        setTrajetAReserver(null);
        showToast(
          `✅ ${nombrePlaces} place${nombrePlaces > 1 ? 's' : ''} réservée${nombrePlaces > 1 ? 's' : ''} : ${trajet.depart} → ${trajet.destination} à ${trajet.heure} !`,
          'success'
        );
      } catch (err) {
        const message = err instanceof ApiError ? err.message : 'Impossible de réserver ce trajet.';
        showToast(message, 'error');
      }
    },
    [token, showToast]
  );

  const handleLogin = useCallback(
    async (credentials) => {
      const { prenom } = await login(credentials);
      setAuthModal(null);
      showToast(`Bienvenue, ${prenom} !`, 'success');
    },
    [login, showToast]
  );

  const handleRegister = useCallback(
    async (data) => {
      const { prenom } = await register(data);
      setAuthModal(null);
      showToast(`Compte créé ! Bienvenue, ${prenom} !`, 'success');
    },
    [register, showToast]
  );

  const handleLogout = useCallback(() => {
    logout();
    showToast('À bientôt !', 'success');
  }, [logout, showToast]);

  const handleOpenCreateTrajet = useCallback(() => {
    if (!user) {
      showToast('Connectez-vous pour proposer un trajet.', 'error');
      setAuthModal('login');
      return;
    }
    setShowCreateTrajet(true);
  }, [user, showToast]);

  const handleCreateSuccess = useCallback(() => {
    setShowCreateTrajet(false);
    showToast('Trajet publié avec succès ! 🎉', 'success');
  }, [showToast]);

  return (
    <div className="ar-app">
      <Navbar
        user={user}
        onLogin={() => setAuthModal('login')}
        onRegister={() => setAuthModal('register')}
        onLogout={handleLogout}
        onHome={resetSearch}
        onCreateTrajet={handleOpenCreateTrajet}
      />

      <Hero onSearch={handleSearch} />

      <main>
        {!isResultView && <RoutesFixesSection onSelectRoute={handleSelectRoute} />}
        {isResultView && (
          <ResultsSection
            results={searchResults}
            query={searchQuery}
            onReserve={handleReserve}
            loading={loading}
            error={error}
          />
        )}
      </main>

      <footer className="footer">
        <strong>AllerRetour</strong> — Le covoiturage du quotidien en région québécoise.
        <br />
        <span style={{ fontSize: '0.75rem', marginTop: 4, display: 'block' }}>
          CoRoute · Université Laval · GLO-2004
        </span>
      </footer>

      {authModal && (
        <AuthModal
          mode={authModal}
          onClose={() => setAuthModal(null)}
          onLogin={handleLogin}
          onRegister={handleRegister}
        />
      )}

      {showCreateTrajet && token && (
        <CreateTrajetModal
          token={token}
          onClose={() => setShowCreateTrajet(false)}
          onSuccess={handleCreateSuccess}
        />
      )}

      {trajetAReserver && token && (
        <ReservationModal
          trajet={trajetAReserver}
          token={token}
          onClose={() => setTrajetAReserver(null)}
          onConfirm={handleConfirmReservation}
        />
      )}

      {toast && <Toast message={toast.message} type={toast.type} onClose={clearToast} />}
    </div>
  );
}
