import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/client';
import { FieldGroup, Input, Select, Textarea, Label } from '../components/ui/Field';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';

const INITIAL_FORM = {
  fullName: '',
  email: '',
  projectType: 'static',
  projectName: '',
  description: '',
  hostingType: '',
  needsDatabase: 'non',
  databaseType: 'aucune',
  gitRepo: '',
  expectedTraffic: '',
  message: '',
};

export default function HostingRequest() {
  const { user, token } = useAuth();
  const [form, setForm] = useState({
    ...INITIAL_FORM,
    fullName: user?.fullName || '',
    email: user?.email || '',
  });
  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await api.post('/requests', form, token);
      setSubmitted(true);
    } catch (err) {
      setError(err.message || "L'envoi de la demande a échoué. Réessayez.");
    } finally {
      setLoading(false);
    }
  }

  if (submitted) {
    return (
      <div className="mx-auto flex max-w-2xl flex-col items-center px-6 py-24 text-center">
        <Card className="w-full">
          <p className="font-mono text-xs text-cyan-glow">statut : en attente</p>
          <h1 className="mt-3 font-heading text-2xl font-semibold text-ink-primary">
            Demande envoyée avec succès
          </h1>
          <p className="mt-4 text-ink-secondary">
            Votre demande pour <span className="text-ink-primary">{form.projectName}</span> a bien été
            enregistrée. Notre équipe la traite manuellement et reviendra vers vous par email.
          </p>
          <Button
            className="mt-8"
            variant="outline"
            onClick={() => {
              setForm(INITIAL_FORM);
              setSubmitted(false);
            }}
          >
            Envoyer une autre demande
          </Button>
        </Card>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-3xl px-6 py-20">
      <p className="font-mono text-xs text-cyan-glow">// nouvelle demande</p>
      <h1 className="mt-2 font-heading text-3xl font-semibold text-ink-primary">
        Demande d'hébergement
      </h1>
      <p className="mt-3 text-ink-secondary">
        Décrivez votre projet, notre équipe étudie chaque demande et vous répond manuellement.
      </p>

      <form onSubmit={handleSubmit} className="mt-10 space-y-8">
        <Card className="space-y-6">
          <div className="grid gap-6 sm:grid-cols-2">
            <FieldGroup label="Nom complet" htmlFor="fullName" required>
              <Input id="fullName" name="fullName" required value={form.fullName} onChange={handleChange} />
            </FieldGroup>
            <FieldGroup label="Email" htmlFor="email" required>
              <Input id="email" name="email" type="email" required value={form.email} onChange={handleChange} />
            </FieldGroup>
          </div>

          <div className="grid gap-6 sm:grid-cols-2">
            <FieldGroup label="Type de projet" htmlFor="projectType" required>
              <Select id="projectType" name="projectType" value={form.projectType} onChange={handleChange}>
                <option value="static">Site statique</option>
                <option value="php">Site dynamique PHP</option>
                <option value="react">Application React</option>
                <option value="other">Autre</option>
              </Select>
            </FieldGroup>
            <FieldGroup label="Nom du projet" htmlFor="projectName" required>
              <Input id="projectName" name="projectName" required value={form.projectName} onChange={handleChange} />
            </FieldGroup>
          </div>

          <FieldGroup label="Description du projet" htmlFor="description" required>
            <Textarea
              id="description"
              name="description"
              required
              placeholder="Décrivez l'objectif et le fonctionnement de votre projet…"
              value={form.description}
              onChange={handleChange}
            />
          </FieldGroup>

          <FieldGroup label="Type d'hébergement souhaité" htmlFor="hostingType" required>
            <Input
              id="hostingType"
              name="hostingType"
              required
              placeholder="Ex : hébergement mutualisé, environnement de démo…"
              value={form.hostingType}
              onChange={handleChange}
            />
          </FieldGroup>

          <div className="grid gap-6 sm:grid-cols-2">
            <div>
              <Label htmlFor="needsDatabase">Base de données nécessaire</Label>
              <div className="mt-2 flex gap-6">
                {['oui', 'non'].map((value) => (
                  <label key={value} className="flex items-center gap-2 text-sm text-ink-secondary">
                    <input
                      type="radio"
                      name="needsDatabase"
                      value={value}
                      checked={form.needsDatabase === value}
                      onChange={handleChange}
                      className="accent-cyan-glow"
                    />
                    {value === 'oui' ? 'Oui' : 'Non'}
                  </label>
                ))}
              </div>
            </div>
            <FieldGroup label="Type de base de données" htmlFor="databaseType">
              <Select
                id="databaseType"
                name="databaseType"
                value={form.databaseType}
                onChange={handleChange}
                disabled={form.needsDatabase === 'non'}
              >
                <option value="aucune">Aucune</option>
                <option value="mysql">MySQL</option>
                <option value="postgresql">PostgreSQL</option>
              </Select>
            </FieldGroup>
          </div>

          <div className="grid gap-6 sm:grid-cols-2">
            <FieldGroup label="Lien du dépôt Git (optionnel)" htmlFor="gitRepo">
              <Input
                id="gitRepo"
                name="gitRepo"
                type="url"
                placeholder="https://github.com/…"
                value={form.gitRepo}
                onChange={handleChange}
              />
            </FieldGroup>
            <FieldGroup label="Ressources estimées (trafic prévu)" htmlFor="expectedTraffic">
              <Input
                id="expectedTraffic"
                name="expectedTraffic"
                placeholder="Ex : ~200 visites / mois"
                value={form.expectedTraffic}
                onChange={handleChange}
              />
            </FieldGroup>
          </div>

          <FieldGroup label="Message complémentaire" htmlFor="message">
            <Textarea id="message" name="message" placeholder="Autre chose à préciser ?" value={form.message} onChange={handleChange} />
          </FieldGroup>
        </Card>

        {error && <p className="text-sm text-rose-300">{error}</p>}

        <Button type="submit" size="lg" disabled={loading}>
          {loading ? 'Envoi…' : 'Envoyer la demande'}
        </Button>
      </form>
    </div>
  );
}
