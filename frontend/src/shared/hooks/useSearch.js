import { useState, useCallback } from 'react';
import { matchTrajets, ApiError } from '@/shared/services/api';
import { labelsToEnums } from '@/shared/constants/jours';

/**
 * Hook encapsulant la logique de recherche via l'endpoint /trajets/match.
 * Convertit les labels UI (ex. "Lun") en enums backend (ex. "LUNDI").
 */
export function useSearch() {
  const [searchResults, setSearchResults] = useState(null);
  const [searchQuery, setSearchQuery] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const runSearch = useCallback(async ({ depart, destination, jourLabels }) => {
    setLoading(true);
    setError(null);
    try {
      const joursEnum = labelsToEnums(jourLabels);
      const results = await matchTrajets({ depart, destination, jours: joursEnum });
      setSearchResults(results ?? []);
      setSearchQuery({ depart, destination });
    } catch (err) {
      const message = err instanceof ApiError ? err.message : 'Erreur de connexion au serveur.';
      setError(message);
      setSearchResults([]);
    } finally {
      setLoading(false);
    }
  }, []);

  const handleSearch = useCallback(
    ({ depart, destination, jours }) => {
      runSearch({ depart, destination, jourLabels: jours });
    },
    [runSearch]
  );

  const handleSelectRoute = useCallback(
    (route) => {
      setSearchQuery({ depart: route.depart, destination: route.destination });
      runSearch({
        depart: route.depart,
        destination: route.destination,
        jourLabels: route.jours ?? [],
      });
    },
    [runSearch]
  );

  const resetSearch = useCallback(() => {
    setSearchResults(null);
    setSearchQuery({});
    setError(null);
  }, []);

  return {
    searchResults,
    searchQuery,
    loading,
    error,
    handleSearch,
    handleSelectRoute,
    resetSearch,
  };
}
