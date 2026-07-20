# WebSGI — Backend

API Spring Boot pour la plateforme WebSGI. Authentification par JWT maison (sans Spring Security),
persistance PostgreSQL via Spring Data JPA, migrations Flyway.

## Prérequis

- Java 17+
- Maven (ou le wrapper `mvn`)
- PostgreSQL en local (géré via DBeaver)

## 1. Créer la base de données

Dans DBeaver, connecte-toi à ton serveur PostgreSQL local et crée une base :

```sql
CREATE DATABASE websgi;
CREATE USER websgi WITH PASSWORD 'websgi';
GRANT ALL PRIVILEGES ON DATABASE websgi TO websgi;
```

(Adapte le nom/mot de passe si besoin, tant que ça correspond aux variables d'environnement ci-dessous.)

## 2. Configurer les variables d'environnement

Copie `.env.example` en `.env` (ou exporte-les directement dans ton terminal) :

```
DB_URL=jdbc:postgresql://localhost:5432/websgi
DB_USERNAME=websgi
DB_PASSWORD=websgi
JWT_SECRET=changez-cette-cle-secrete-en-production-au-moins-32-caracteres
JWT_EXPIRATION_MINUTES=1440
FRONTEND_URL=http://localhost:5173
```

## 3. Lancer le backend

```bash
mvn spring-boot:run
```

Au premier démarrage, Flyway crée automatiquement les tables (`users`, `hosting_requests`,
`password_reset_tokens`) et insère un compte admin de test.

L'API écoute sur `http://localhost:8080/api` — exactement l'URL attendue par le frontend React
(`VITE_API_URL` dans le `.env` du frontend).

## Compte admin de test (seedé par la migration Flyway)

```
email : admin@websgi.fr
mot de passe : admin123
```

Connecte-toi avec ce compte sur le frontend pour accéder à `/admin`.

## Points d'API

| Méthode | Route | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | non | Créer un compte (rôle USER) |
| POST | `/api/auth/login` | non | Connexion, renvoie `{ token, user }` |
| GET  | `/api/auth/me` | oui | Utilisateur courant |
| POST | `/api/auth/forgot-password` | non | Génère un token de reset (loggé en console, pas d'email envoyé) |
| POST | `/api/auth/reset-password` | non | Change le mot de passe avec le token |
| POST | `/api/requests` | oui | Envoyer une demande d'hébergement |
| GET  | `/api/requests/mine` | oui | Historique de mes demandes |
| GET  | `/api/admin/requests` | admin | Toutes les demandes |
| PATCH | `/api/admin/requests/{id}/status` | admin | Changer le statut d'une demande |

Toutes les routes authentifiées attendent un header `Authorization: Bearer <token>`.

## Choix techniques (par défaut, à ajuster si besoin)

- **JWT** : génération/validation via la librairie `io.jsonwebtoken:jjwt` (HMAC), pas de Spring Security.
  L'autorisation admin est vérifiée manuellement dans les contrôleurs.
- **Mots de passe** : hashés avec `jBCrypt`.
- **Migrations** : Flyway (`src/main/resources/db/migration`), plus propre à présenter que
  `ddl-auto: update` pour un rendu de projet.

## Vérifier les données dans DBeaver

Une fois le backend lancé au moins une fois (Flyway aura créé les tables), rafraîchis la connexion
dans DBeaver : tu dois voir `users`, `hosting_requests` et `password_reset_tokens` dans le schéma
`public`. Chaque demande envoyée depuis le formulaire React apparaît en direct dans
`hosting_requests`, avec le statut `EN_ATTENTE` par défaut — exactement ce qui remonte ensuite
dans le panel Admin du site.
