import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import AuthCard from '../components/AuthCard';
import { FieldGroup, Input } from '../components/ui/Field';
import Button from '../components/ui/Button';
import { api } from '../api/client';

export default function ResetPassword() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token') || '';
  const navigate = useNavigate();
  const [form, setForm] = useState({ password: '', confirmPassword: '' });
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
      await api.post('/auth/reset-password', { token, password: form.password });
      navigate('/login', { replace: true });
    } catch (err) {
      setError(err.message || 'Lien invalide ou expiré.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <AuthCard
      title="Réinitialiser le mot de passe"
      subtitle="Choisissez un nouveau mot de passe."
      footer={
        <Link to="/login" className="text-cyan-glow hover:underline">
          Retour à la connexion
        </Link>
      }
    >
      <form onSubmit={handleSubmit} className="space-y-5">
        <FieldGroup label="Nouveau mot de passe" htmlFor="password" required>
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
          {loading ? 'Enregistrement…' : 'Réinitialiser'}
        </Button>
      </form>
    </AuthCard>
  );
}
