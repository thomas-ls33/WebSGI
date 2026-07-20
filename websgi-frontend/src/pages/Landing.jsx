import { Link } from 'react-router-dom';
import { Globe2, GitBranch, Database, ShieldCheck, GraduationCap, Users, Rocket } from 'lucide-react';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import Terminal from '../components/Terminal';

const FEATURES = [
  {
    icon: Globe2,
    title: 'Sites statiques & dynamiques',
    text: "HTML, PHP ou React : votre projet tourne sur une infrastructure gérée, quel que soit le langage.",
  },
  {
    icon: GitBranch,
    title: 'Support Git',
    text: 'Connectez votre dépôt et livrez vos mises à jour sans vous soucier du serveur en dessous.',
  },
  {
    icon: Database,
    title: 'Base de données incluse',
    text: 'MySQL ou PostgreSQL provisionnée et administrée pour vous, prête à recevoir vos données.',
  },
  {
    icon: ShieldCheck,
    title: 'Sécurité gérée',
    text: 'Pare-feu et surveillance pris en charge par notre équipe, pour que vous dormiez tranquille.',
  },
];

const AUDIENCE = [
  {
    icon: GraduationCap,
    title: 'Étudiants ESGI',
    text: 'Un terrain de jeu réel pour vos projets de cours, mémoires et side-projects.',
  },
  {
    icon: Users,
    title: 'Associations étudiantes',
    text: 'Un site fiable pour votre asso, sans avoir à gérer un serveur en plus des events.',
  },
  {
    icon: Rocket,
    title: 'Junior-entreprises & freelances',
    text: 'Une infra pro pour livrer vos clients, pendant que vous vous concentrez sur le code.',
  },
];

export default function Landing() {
  return (
    <div>
      <section className="relative overflow-hidden">
        <div className="absolute inset-0 bg-grid-pattern bg-[size:44px_44px] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_0%,black,transparent)]" />
        <div className="relative mx-auto grid max-w-7xl gap-16 px-6 py-24 md:py-32 lg:grid-cols-2 lg:items-center">
          <div>
            <p className="mb-5 inline-flex items-center rounded-full border border-cyan-glow/30 px-4 py-1 font-mono text-xs text-cyan-glow">
              PaaS local — ESGI Bordeaux
            </p>
            <h1 className="font-heading text-4xl font-bold leading-tight tracking-tight text-ink-primary sm:text-5xl lg:text-6xl">
              We Host,<br />
              <span className="text-cyan-glow drop-shadow-[0_0_18px_rgba(115,218,234,0.5)]">You Post.</span>
            </h1>
            <p className="mt-6 max-w-md text-lg text-ink-secondary">
              Nous prenons en charge l'infrastructure pour que vous vous concentriez sur votre code.
            </p>
            <div className="mt-10 flex flex-wrap gap-4">
              <Button as={Link} to="/demande" size="lg">
                Demander un hébergement
              </Button>
              <Button as={Link} to="/#services" variant="outline" size="lg">
                Voir les services
              </Button>
            </div>
          </div>

          <Terminal />
        </div>
      </section>

      <section className="border-y border-cyan-glow/10 bg-navy-light/40 py-20">
        <div className="mx-auto max-w-4xl px-6 text-center">
          <h2 className="font-heading text-2xl font-semibold text-ink-primary sm:text-3xl">
            Nous prenons en charge <span className="text-cyan-glow">le Host</span>, pour que vous vous
            concentriez sur <span className="text-cyan-glow">le Post</span>.
          </h2>
          <p className="mx-auto mt-4 max-w-2xl text-ink-secondary">
            Pas de serveur à configurer, pas de firewall à maintenir : votre demande est étudiée par une
            vraie équipe, et votre projet est mis en ligne sur une infra pensée pour les étudiants.
          </p>
        </div>
      </section>

      <section id="services" className="mx-auto max-w-7xl px-6 py-24">
        <div className="mb-14 max-w-2xl">
          <p className="font-mono text-xs text-cyan-glow">// services</p>
          <h2 className="mt-2 font-heading text-3xl font-semibold text-ink-primary">Ce que couvre WebSGI</h2>
        </div>
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
          {FEATURES.map(({ icon: Icon, title, text }) => (
            <Card key={title} hover className="flex flex-col gap-4">
              <div className="flex h-11 w-11 items-center justify-center rounded-xl border border-cyan-glow/40 text-cyan-glow">
                <Icon size={20} />
              </div>
              <h3 className="font-heading text-base font-semibold text-ink-primary">{title}</h3>
              <p className="text-sm leading-relaxed text-ink-secondary">{text}</p>
            </Card>
          ))}
        </div>
      </section>

      <section className="bg-navy-light/40 py-24">
        <div className="mx-auto max-w-7xl px-6">
          <div className="mb-14 max-w-2xl">
            <p className="font-mono text-xs text-cyan-glow">// pour qui</p>
            <h2 className="mt-2 font-heading text-3xl font-semibold text-ink-primary">Conçu pour la vie ESGI</h2>
          </div>
          <div className="grid gap-6 md:grid-cols-3">
            {AUDIENCE.map(({ icon: Icon, title, text }) => (
              <Card key={title} className="flex flex-col gap-4">
                <div className="flex h-11 w-11 items-center justify-center rounded-xl border border-cyan-soft/50 text-cyan-soft">
                  <Icon size={20} />
                </div>
                <h3 className="font-heading text-base font-semibold text-ink-primary">{title}</h3>
                <p className="text-sm leading-relaxed text-ink-secondary">{text}</p>
              </Card>
            ))}
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-4xl px-6 py-24 text-center">
        <h2 className="font-heading text-3xl font-semibold text-ink-primary sm:text-4xl">
          Prêt à mettre votre projet en ligne&nbsp;?
        </h2>
        <p className="mx-auto mt-4 max-w-xl text-ink-secondary">
          Remplissez une demande, notre équipe l'étudie et revient vers vous. Simple, humain, sans surprise.
        </p>
        <Button as={Link} to="/demande" size="lg" className="mt-8">
          Demander un hébergement
        </Button>
      </section>
    </div>
  );
}
