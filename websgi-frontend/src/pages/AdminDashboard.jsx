import { useEffect, useMemo, useState } from 'react';
import { ClipboardList, Clock3, CheckCircle2, ArrowUpDown } from 'lucide-react';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';
import StatCard from '../components/ui/StatCard';
import Badge from '../components/ui/Badge';
import RequestDetailDrawer from '../components/RequestDetailDrawer';

const COLUMNS = [
  { key: 'id', label: 'ID' },
  { key: 'createdAt', label: 'Date' },
  { key: 'fullName', label: 'Demandeur' },
  { key: 'email', label: 'Email' },
  { key: 'projectName', label: 'Projet' },
  { key: 'projectType', label: 'Type' },
  { key: 'needsDatabase', label: 'BDD requise' },
  { key: 'status', label: 'Statut' },
];

export default function AdminDashboard() {
  const { token } = useAuth();
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [sortKey, setSortKey] = useState('createdAt');
  const [sortDir, setSortDir] = useState('desc');
  const [selected, setSelected] = useState(null);

  useEffect(() => {
    api
      .get('/admin/requests', token)
      .then(setRequests)
      .catch((err) => setError(err.message || 'Impossible de charger les demandes.'))
      .finally(() => setLoading(false));
  }, [token]);

  const stats = useMemo(
    () => ({
      total: requests.length,
      pending: requests.filter((r) => r.status === 'En attente').length,
      validated: requests.filter((r) => r.status === 'Validée').length,
    }),
    [requests]
  );

  const sorted = useMemo(() => {
    const copy = [...requests];
    copy.sort((a, b) => {
      const av = a[sortKey];
      const bv = b[sortKey];
      if (av < bv) return sortDir === 'asc' ? -1 : 1;
      if (av > bv) return sortDir === 'asc' ? 1 : -1;
      return 0;
    });
    return copy;
  }, [requests, sortKey, sortDir]);

  function toggleSort(key) {
    if (key === sortKey) {
      setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortKey(key);
      setSortDir('asc');
    }
  }

  async function handleStatusChange(id, status) {
    const previous = requests;
    setRequests((rs) => rs.map((r) => (r.id === id ? { ...r, status } : r)));
    setSelected((s) => (s && s.id === id ? { ...s, status } : s));
    try {
      await api.patch(`/admin/requests/${id}/status`, { status }, token);
    } catch (err) {
      setRequests(previous);
      setError(err.message || 'Impossible de changer le statut.');
    }
  }

  return (
    <div className="mx-auto max-w-7xl px-6 py-16">
      <p className="font-mono text-xs text-cyan-glow">// administration</p>
      <h1 className="mt-2 font-heading text-3xl font-semibold text-ink-primary">Demandes d'hébergement</h1>

      <div className="mt-8 grid gap-6 sm:grid-cols-3">
        <StatCard label="Total des demandes" value={stats.total} icon={ClipboardList} />
        <StatCard label="En attente" value={stats.pending} icon={Clock3} />
        <StatCard label="Validées" value={stats.validated} icon={CheckCircle2} />
      </div>

      <div className="mt-10 overflow-x-auto rounded-2xl border border-cyan-glow/20">
        <table className="w-full min-w-[900px] border-collapse text-left text-sm">
          <thead>
            <tr className="border-b border-cyan-glow/20 bg-navy-light/60">
              {COLUMNS.map((col) => (
                <th key={col.key} className="whitespace-nowrap px-4 py-3 font-medium text-ink-secondary">
                  <button onClick={() => toggleSort(col.key)} className="inline-flex items-center gap-1 hover:text-cyan-glow">
                    {col.label}
                    <ArrowUpDown size={12} />
                  </button>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {loading && (
              <tr>
                <td colSpan={COLUMNS.length} className="px-4 py-6 text-center font-mono text-ink-secondary">
                  chargement…
                </td>
              </tr>
            )}
            {!loading && sorted.length === 0 && (
              <tr>
                <td colSpan={COLUMNS.length} className="px-4 py-6 text-center text-ink-secondary">
                  Aucune demande pour le moment.
                </td>
              </tr>
            )}
            {!loading &&
              sorted.map((r) => (
                <tr
                  key={r.id}
                  onClick={() => setSelected(r)}
                  className="cursor-pointer border-b border-cyan-glow/10 transition-colors hover:bg-cyan-glow/5"
                >
                  <td className="px-4 py-3 font-mono text-ink-secondary">{r.id}</td>
                  <td className="px-4 py-3 text-ink-secondary">{new Date(r.createdAt).toLocaleDateString('fr-FR')}</td>
                  <td className="px-4 py-3 text-ink-primary">{r.fullName}</td>
                  <td className="px-4 py-3 text-ink-secondary">{r.email}</td>
                  <td className="px-4 py-3 text-ink-primary">{r.projectName}</td>
                  <td className="px-4 py-3 text-ink-secondary">{r.projectType}</td>
                  <td className="px-4 py-3 text-ink-secondary">{r.needsDatabase === 'oui' ? r.databaseType : 'Non'}</td>
                  <td className="px-4 py-3">
                    <Badge status={r.status} />
                  </td>
                </tr>
              ))}
          </tbody>
        </table>
      </div>

      {error && <p className="mt-4 text-sm text-rose-300">{error}</p>}

      {selected && (
        <RequestDetailDrawer request={selected} onClose={() => setSelected(null)} onStatusChange={handleStatusChange} />
      )}
    </div>
  );
}
