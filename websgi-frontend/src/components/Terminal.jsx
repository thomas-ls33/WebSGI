const LINES = [
  { prompt: '$', text: 'websgi request --project mon-app-react', delay: 0 },
  { text: '✓ demande reçue — statut : en attente', tone: 'muted', delay: 0.4 },
  { text: '→ transmise à l’équipe WebSGI', tone: 'muted', delay: 0.8 },
  { prompt: '$', text: 'websgi status mon-app-react', delay: 1.4 },
  { text: '✓ en cours de traitement…', tone: 'cyan', delay: 1.9 },
];

export default function Terminal() {
  return (
    <div className="relative">
      <div className="absolute -inset-4 rounded-3xl bg-cyan-glow/10 blur-2xl" />
      <div className="relative rounded-2xl border border-cyan-glow/30 bg-navy-light shadow-glow-lg">
        <div className="flex items-center gap-2 border-b border-cyan-glow/10 px-5 py-3">
          <span className="h-2.5 w-2.5 rounded-full bg-rose-400/70" />
          <span className="h-2.5 w-2.5 rounded-full bg-amber-300/70" />
          <span className="h-2.5 w-2.5 rounded-full bg-emerald-400/70" />
          <span className="ml-3 font-mono text-xs text-ink-secondary">websgi — session</span>
        </div>
        <div className="space-y-3 px-6 py-8 font-mono text-sm">
          {LINES.map((line, i) => (
            <p
              key={i}
              className={
                line.tone === 'cyan'
                  ? 'text-cyan-glow'
                  : line.tone === 'muted'
                  ? 'text-ink-secondary pl-4'
                  : 'text-ink-primary'
              }
            >
              {line.prompt && <span className="mr-2 text-cyan-soft">{line.prompt}</span>}
              {line.text}
            </p>
          ))}
          <p className="text-ink-primary">
            <span className="mr-2 text-cyan-soft">$</span>
            <span className="border-r-2 border-cyan-glow pr-0.5 animate-blink">&nbsp;</span>
          </p>
        </div>
      </div>
    </div>
  );
}
