const STATUS_STYLES = {
  'En attente': 'bg-ink-secondary/10 text-ink-secondary border-ink-secondary/30',
  'En cours de traitement': 'bg-cyan-glow/10 text-cyan-glow border-cyan-glow/40',
  'Validée': 'bg-emerald-400/10 text-emerald-300 border-emerald-400/40',
  'Refusée': 'bg-rose-400/10 text-rose-300 border-rose-400/40',
};

export default function Badge({ status, children }) {
  const label = children ?? status;
  const style = STATUS_STYLES[status] || 'bg-ink-secondary/10 text-ink-secondary border-ink-secondary/30';
  return (
    <span className={`inline-flex items-center rounded-full border px-3 py-1 text-xs font-medium font-mono ${style}`}>
      {label}
    </span>
  );
}
