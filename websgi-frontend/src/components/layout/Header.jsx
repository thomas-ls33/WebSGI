import { NavLink, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Button from '../ui/Button';
import logo from '../../assets/logo.png';

const navLinkClass = ({ isActive }) =>
  `text-sm tracking-wide transition-colors ${
    isActive ? 'text-cyan-glow' : 'text-ink-secondary hover:text-ink-primary'
  }`;

export default function Header() {
  const { user, logout } = useAuth();

  return (
    <header className="sticky top-0 z-50 border-b border-cyan-glow/10 bg-navy/85 backdrop-blur-md">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
        <Link to="/" className="flex items-center gap-2">
          <img src={logo} alt="WebSGI" className="h-9 w-9 object-contain drop-shadow-[0_0_10px_rgba(115,218,234,0.4)]" />
          <span className="font-heading text-lg font-bold tracking-widest text-ink-primary">
            WEB<span className="text-cyan-glow">SGI</span>
          </span>
        </Link>

        <nav className="hidden items-center gap-8 md:flex">
          <NavLink to="/" end className={navLinkClass}>
            Accueil
          </NavLink>
          <NavLink to="/#services" className={navLinkClass}>
            Services
          </NavLink>
          <NavLink to="/demande" className={navLinkClass}>
            Demande d'hébergement
          </NavLink>
          {user?.role === 'admin' && (
            <NavLink to="/admin" className={navLinkClass}>
              Admin
            </NavLink>
          )}
          {user && (
            <NavLink to="/dashboard" className={navLinkClass}>
              Mes demandes
            </NavLink>
          )}
        </nav>

        <div className="flex items-center gap-3">
          {user ? (
            <>
              <span className="hidden text-sm text-ink-secondary sm:inline font-mono">{user.email}</span>
              <Button variant="outline" size="sm" onClick={logout}>
                Déconnexion
              </Button>
            </>
          ) : (
            <>
              <Button as={Link} to="/login" variant="ghost" size="sm">
                Connexion
              </Button>
              <Button as={Link} to="/demande" variant="primary" size="sm">
                Demander un hébergement
              </Button>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
