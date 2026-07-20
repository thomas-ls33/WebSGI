import logo from '../../assets/logo.png';

export default function Footer() {
  return (
    <footer className="border-t border-cyan-glow/10 bg-navy">
      <div className="mx-auto max-w-7xl px-6 py-10">
        <div className="flex flex-col items-center justify-between gap-6 md:flex-row">
          <div className="flex items-center gap-2">
            <img src={logo} alt="WebSGI" className="h-7 w-7 object-contain" />
            <span className="font-heading text-sm font-bold tracking-widest text-ink-primary">
              WEB<span className="text-cyan-glow">SGI</span>
            </span>
          </div>
          <p className="font-mono text-xs text-ink-secondary">
            $ uptime — <span className="text-cyan-glow">hébergé pour vous, codé par vous</span>
          </p>
          <p className="text-sm text-ink-secondary">ESGI Bordeaux — Équipe WebSGI</p>
        </div>
      </div>
    </footer>
  );
}
