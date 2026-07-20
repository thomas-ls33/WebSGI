import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import AuthCard from '../components/AuthCard';
import { FieldGroup, Input } from '../components/ui/Field';
import Button from '../components/ui/Button';
import { useAuth } from '../context/AuthContext';

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ fullName: '', email: '', password: '', confirmPassword: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    if (form.password !== form.confirmPassword) {
      setError('Les mots de passe ne correspondent pas.');
      return;
    }
    setLoading(true);
    try {
      await register({ fullName: form.fullName, email: form.email, password: form.password });
      navigate('/dashboard', { replace: true });
    } catch (err) {
      setError(err.message || "Impossible de créer le compte.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <AuthCard
      title="Créer un compte"
      subtitle="Rejoignez WebSGI pour héberger vos projets."
      footer={
        <>
          Déjà un compte ?{' '}
          <Link to="/login" className="text-cyan-glow hover:underline">
            Connectez-vous
          </Link>
        </>
      }
    >
      <form onSubmit={handleSubmit} className="space-y-5">
        <FieldGroup label="Nom complet" htmlFor="fullName" required>
          <Input id="fullName" name="fullName" required placeholder="Jean Dupont" value={form.fullName} onChange={handleChange} />
        </FieldGroup>
        <FieldGroup label="Email" htmlFor="email" required>
          <Input id="email" name="email" type="email" required placeholder="prenom.nom@esgi.fr" value={form.email} onChange={handleChange} />
        </FieldGroup>
        <FieldGroup label="Mot de passe" htmlFor="password" required>
          <Input id="password" name="password" type="password" required placeholder="••••••••" value={form.password} onChange={handleChange} />
        </FieldGroup>
        <FieldGroup label="Confirmer le mot de passe" htmlFor="confirmPassword" required>
          <Input
            id="confirmPassword"
            name="confirmPassword"
            type="password"
            required
            placeholder="••••••••"
            value={form.confirmPassword}
            onChange={handleChange}
          />
        </FieldGroup>

        {error && <p className="text-sm text-rose-300">{error}</p>}

        <Button type="submit" className="w-full" disabled={loading}>
          {loading ? 'Création…' : 'Créer mon compte'}
        </Button>
      </form>
    </AuthCard>
  );
}
