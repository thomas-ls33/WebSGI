import { X } from 'lucide-react';
import Badge from './ui/Badge';
import { Select, Label } from './ui/Field';

const STATUSES = ['En attente', 'En cours de traitement', 'Validée', 'Refusée'];

export default function RequestDetailDrawer({ request, onClose, onStatusChange }) {
  if (!request) return null;

  return (
    <div className="fixed inset-0 z-50 flex justify-end bg-black/60 backdrop-blur-sm">
      <div className="h-full w-full max-w-lg overflow-y-auto border-l border-cyan-glow/20 bg-navy p-8">
        <div className="flex items-start justify-between">
          <div>
            <p className="font-mono text-xs text-cyan-glow">demande #{request.id}</p>
            <h2 className="mt-1 font-heading text-2xl font-semibold text-ink-primary">{request.projectName}</h2>
          </div>
          <button onClick={onClose} className="text-ink-secondary hover:text-ink-primary">
            <X size={22} />
          </button>
        </div>

        <div className="mt-6">
          <Badge status={request.status} />
        </div>

        <dl className="mt-8 space-y-5 text-sm">
          <Row label="Demandeur" value={request.fullName} />
          <Row label="Email" value={request.email} />
          <Row label="Type de projet" value={request.projectType} />
          <Row label="Type d'hébergement" value={request.hostingType} />
          <Row label="Base de données" value={request.needsDatabase === 'oui' ? request.databaseType : 'Non requise'} />
          {request.gitRepo && <Row label="Dépôt Git" value={request.gitRepo} link />}
          <Row label="Trafic estimé" value={request.expectedTraffic || '—'} />
          <div>
            <dt className="text-ink-secondary">Description</dt>
            <dd className="mt-1 whitespace-pre-line text-ink-primary">{request.description}</dd>
          </div>
          {request.message && (
            <div>
              <dt className="text-ink-secondary">Message complémentaire</dt>
              <dd className="mt-1 whitespace-pre-line text-ink-primary">{request.message}</dd>
            </div>
          )}
          <Row label="Reçue le" value={new Date(request.createdAt).toLocaleString('fr-FR')} />
        </dl>

        <div className="mt-10">
          <Label htmlFor="status">Changer le statut</Label>
          <Select id="status" value={request.status} onChange={(e) => onStatusChange(request.id, e.target.value)}>
            {STATUSES.map((s) => (
              <option key={s} value={s}>
                {s}
              </option>
            ))}
          </Select>
        </div>
      </div>
    </div>
  );
}

function Row({ label, value, link }) {
  return (
    <div className="flex items-center justify-between gap-4 border-b border-cyan-glow/10 pb-3">
      <dt className="text-ink-secondary">{label}</dt>
      {link ? (
        <a href={value} target="_blank" rel="noreferrer" className="truncate text-cyan-glow hover:underline">
          {value}
        </a>
      ) : (
        <dd className="text-right text-ink-primary">{value}</dd>
      )}
    </div>
  );
}
