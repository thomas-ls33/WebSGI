import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import AuthCard from '../components/AuthCard';
import { FieldGroup, Input } from '../components/ui/Field';
import Button from '../components/ui/Button';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const user = await login(form.email, form.password);
      const redirectTo = location.state?.from || (user.role === 'admin' ? '/admin' : '/dashboard');
      navigate(redirectTo, { replace: true });
    } catch (err) {
      setError(err.message || 'Identifiants invalides.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <AuthCard
      title="Connexion"
      subtitle="Accédez à vos demandes d'hébergement."
      footer={
        <>
          Pas encore de compte ?{' '}
          <Link to="/register" className="text-cyan-glow hover:underline">
            Inscrivez-vous
          </Link>
        </>
      }
    >
      <form onSubmit={handleSubmit} className="space-y-5">
        <FieldGroup label="Email" htmlFor="email" required>
          <Input id="email" name="email" type="email" required placeholder="prenom.nom@esgi.fr" value={form.email} onChange={handleChange} />
        </FieldGroup>
        <FieldGroup label="Mot de passe" htmlFor="password" required>
          <Input id="password" name="password" type="password" required placeholder="••••••••" value={form.password} onChange={handleChange} />
        </FieldGroup>

        <div className="flex justify-end text-sm">
          <Link to="/forgot-password" className="text-ink-secondary hover:text-cyan-glow">
            Mot de passe oublié ?
          </Link>
        </div>

        {error && <p className="text-sm text-rose-300">{error}</p>}

        <Button type="submit" className="w-full" disabled={loading}>
          {loading ? 'Connexion…' : 'Se connecter'}
        </Button>
      </form>
    </AuthCard>
  );
}
