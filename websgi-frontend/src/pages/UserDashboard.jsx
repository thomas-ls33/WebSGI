import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/client';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';

export default function UserDashboard() {
  const { user, token } = useAuth();
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api
      .get('/requests/mine', token)
      .then(setRequests)
      .catch((err) => setError(err.message || 'Impossible de charger vos demandes.'))
      .finally(() => setLoading(false));
  }, [token]);

  return (
    <div className="mx-auto max-w-5xl px-6 py-16">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="font-mono text-xs text-cyan-glow">// mon espace</p>
          <h1 className="mt-2 font-heading text-3xl font-semibold text-ink-primary">
            Bonjour {user?.fullName || user?.email}
          </h1>
          <p className="mt-2 text-ink-secondary">Retrouvez ici l'historique de vos demandes d'hébergement.</p>
        </div>
        <Button as={Link} to="/demande">
          Nouvelle demande
        </Button>
      </div>

      <div className="mt-10">
        {loading && <p className="font-mono text-sm text-ink-secondary">chargement…</p>}
        {!loading && error && <p className="text-sm text-rose-300">{error}</p>}
        {!loading && !error && requests.length === 0 && (
          <Card className="text-center text-ink-secondary">
            Vous n'avez pas encore envoyé de demande.
          </Card>
        )}
        {!loading && requests.length > 0 && (
          <div className="space-y-4">
            {requests.map((r) => (
              <Card key={r.id} hover className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <p className="font-heading text-base font-semibold text-ink-primary">{r.projectName}</p>
                  <p className="mt-1 text-sm text-ink-secondary">
                    {r.projectType} · envoyée le {new Date(r.createdAt).toLocaleDateString('fr-FR')}
                  </p>
                </div>
                <Badge status={r.status} />
              </Card>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
