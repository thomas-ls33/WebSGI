export default function StatCard({ label, value, icon: Icon }) {
  return (
    <div className="rounded-2xl border border-cyan-glow/25 bg-navy-light/70 p-6 shadow-glow-sm">
      <div className="flex items-center justify-between">
        <p className="text-sm text-ink-secondary">{label}</p>
        {Icon && <Icon size={18} className="text-cyan-glow" />}
      </div>
      <p className="mt-3 font-heading text-3xl font-bold text-ink-primary">{value}</p>
    </div>
  );
}
