import { Link } from 'react-router-dom';
import Card from './ui/Card';
import logo from '../assets/logo.png';

export default function AuthCard({ title, subtitle, children, footer }) {
  return (
    <div className="relative flex min-h-[calc(100vh-64px)] items-center justify-center overflow-hidden px-6 py-16">
      <div className="absolute inset-0 bg-grid-pattern bg-[size:44px_44px] [mask-image:radial-gradient(ellipse_50%_50%_at_50%_30%,black,transparent)]" />
      <Card className="relative w-full max-w-md">
        <Link to="/" className="mb-6 inline-flex items-center gap-2">
          <img src={logo} alt="WebSGI" className="h-7 w-7 object-contain" />
          <span className="font-heading text-sm font-bold tracking-widest text-ink-primary">
            WEB<span className="text-cyan-glow">SGI</span>
          </span>
        </Link>
        <h1 className="font-heading text-2xl font-semibold text-ink-primary">{title}</h1>
        {subtitle && <p className="mt-2 text-sm text-ink-secondary">{subtitle}</p>}
        <div className="mt-8">{children}</div>
        {footer && <div className="mt-6 text-center text-sm text-ink-secondary">{footer}</div>}
      </Card>
    </div>
  );
}
