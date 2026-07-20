import { useState } from 'react';
import { Link } from 'react-router-dom';
import AuthCard from '../components/AuthCard';
import { FieldGroup, Input } from '../components/ui/Field';
import Button from '../components/ui/Button';
import { api } from '../api/client';

export default function ForgotPassword() {
  const [email, setEmail] = useState('');
  const [sent, setSent] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await api.post('/auth/forgot-password', { email });
      setSent(true);
    } catch (err) {
      setError(err.message || "Une erreur est survenue.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <AuthCard
      title="Mot de passe oublié"
      subtitle="Recevez un lien de réinitialisation par email."
      footer={
        <Link to="/login" className="text-cyan-glow hover:underline">
          Retour à la connexion
        </Link>
      }
    >
      {sent ? (
        <p className="rounded-lg border border-cyan-glow/30 bg-cyan-glow/5 px-4 py-3 text-sm text-ink-primary">
          Si un compte existe pour {email}, un email de réinitialisation vient d'être envoyé.
        </p>
      ) : (
        <form onSubmit={handleSubmit} className="space-y-5">
          <FieldGroup label="Email" htmlFor="email" required>
            <Input
              id="email"
              type="email"
              required
              placeholder="prenom.nom@esgi.fr"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </FieldGroup>
          {error && <p className="text-sm text-rose-300">{error}</p>}
          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? 'Envoi…' : 'Envoyer le lien'}
          </Button>
        </form>
      )}
    </AuthCard>
  );
}
