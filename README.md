# WebSGI — Déploiement Docker

## 📋 Présentation

WebSGI est une application web composée de :

* **Frontend** : React + Vite
* **Backend** : Spring Boot + Java 17
* **Base de données** : PostgreSQL 16
* **Monitoring** : Prometheus + Grafana
* **Conteneurisation** : Docker / Docker Compose
* **Serveur web** : Nginx

L'application est déployée sur une VM Ubuntu 24.04.

---

# 📁 Architecture du projet

```text
WebSGI/
│
├── README.md
│
├── docker-compose.yml
│
├── websgi-backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
├── websgi-frontend/
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── src/
│
└── monitoring/
    ├── docker-compose.yml
    │
    └── prometheus/
        └── prometheus.yml
```

---

# 🐳 Architecture Docker

Le projet est séparé en deux stacks Docker.

## Stack WebSGI

Le fichier :

```text
docker-compose.yml
```

gère :

```text
Frontend
   │
   │ HTTP
   ▼
Nginx
   │
   │ /api/
   ▼
Backend Spring Boot
   │
   │ PostgreSQL
   ▼
PostgreSQL
```

Services :

| Service  | Technologie   | Port |
| -------- | ------------- | ---: |
| frontend | React + Nginx |   80 |
| backend  | Spring Boot   | 8080 |
| postgres | PostgreSQL 16 | 5432 |

Le backend et PostgreSQL ne sont pas directement exposés sur Internet.

---

# 📊 Stack Monitoring

Le monitoring est séparé dans :

```text
monitoring/docker-compose.yml
```

Il contient :

```text
Grafana
   │
   │ PromQL
   ▼
Prometheus
```

Services :

| Service    | Technologie | Port |
| ---------- | ----------- | ---: |
| grafana    | Grafana     | 3000 |
| prometheus | Prometheus  | 9090 |

Prometheus utilise :

```text
monitoring/prometheus/prometheus.yml
```

pour sa configuration.

---

# 💾 Volumes Docker

Les données persistantes sont stockées dans des volumes Docker.

## PostgreSQL

```text
postgres_data
```

Contient les données de la base PostgreSQL.

## Grafana

```text
monitoring_grafana_data
```

Contient notamment :

* utilisateurs Grafana
* dashboards
* datasources
* configuration persistante

## Prometheus

```text
monitoring_prometheus_data
```

Contient les données de métriques collectées par Prometheus.

⚠️ Ne pas supprimer ces volumes sauf si une suppression complète des données est souhaitée.

---

# 🚀 Démarrer WebSGI

Depuis la racine du projet :

```bash
cd ~/WebSGI
```

Démarrer la stack principale :

```bash
docker compose up -d
```

Pour reconstruire les images :

```bash
docker compose up -d --build
```

Voir l'état des conteneurs :

```bash
docker compose ps
```

Voir les logs :

```bash
docker compose logs
```

Voir les logs d'un service :

```bash
docker compose logs backend
docker compose logs frontend
docker compose logs postgres
```

Suivre les logs en temps réel :

```bash
docker compose logs -f backend
```

Arrêter la stack :

```bash
docker compose down
```

---

# 📊 Démarrer le monitoring

Le monitoring possède son propre Docker Compose.

```bash
cd ~/WebSGI/monitoring
```

Démarrer :

```bash
docker compose up -d
```

Vérifier :

```bash
docker compose ps
```

Arrêter :

```bash
docker compose down
```

Voir les logs :

```bash
docker compose logs
```

Ou :

```bash
docker compose logs -f prometheus
docker compose logs -f grafana
```

---

# 🌐 Accès aux services

## Application WebSGI

```text
http://<IP_SERVEUR>
```

Exemple :

```text
http://74.248.158.106
```

## Grafana

```text
http://<IP_SERVEUR>:3000
```

Exemple :

```text
http://74.248.158.106:3000
```

## Prometheus

```text
http://<IP_SERVEUR>:9090
```

Exemple :

```text
http://74.248.158.106:9090
```

---

# 🔗 Connexion Grafana → Prometheus

Dans Grafana, ajouter une datasource Prometheus.

URL à utiliser :

```text
http://prometheus:9090
```

⚠️ Ne pas utiliser :

```text
http://localhost:9090
```

car Grafana et Prometheus fonctionnent dans des conteneurs Docker.

---

# 🔥 Firewall

Les ports nécessaires côté serveur sont :

| Port | Utilisation        |
| ---: | ------------------ |
|   22 | SSH                |
|   80 | Application WebSGI |
| 3000 | Grafana            |
| 9090 | Prometheus         |

Vérifier UFW :

```bash
sudo ufw status
```

Autoriser un port :

```bash
sudo ufw allow 3000/tcp
sudo ufw allow 9090/tcp
```

Les mêmes ports doivent être autorisés dans le réseau de sécurité Azure si un accès depuis Internet est nécessaire.

---

# 🔍 Commandes Docker utiles

Voir les conteneurs actifs :

```bash
docker ps
```

Voir tous les conteneurs :

```bash
docker ps -a
```

Voir les images :

```bash
docker images
```

Voir les volumes :

```bash
docker volume ls
```

Voir les réseaux :

```bash
docker network ls
```

Inspecter un réseau :

```bash
docker network inspect <nom_du_reseau>
```

Voir l'utilisation des ressources :

```bash
docker stats
```

---

# 🛠️ Déploiement après une modification du code

Après une modification du backend ou du frontend :

```bash
cd ~/WebSGI
docker compose up -d --build
```

Vérifier ensuite :

```bash
docker compose ps
```

Pour le monitoring :

```bash
cd ~/WebSGI/monitoring
docker compose up -d
```

---

# 🔄 Mise à jour du projet depuis Git

Depuis la racine :

```bash
cd ~/WebSGI
git pull
```

Puis reconstruire :

```bash
docker compose up -d --build
```

Si les fichiers de monitoring ont également été modifiés :

```bash
cd ~/WebSGI/monitoring
docker compose up -d
```

---

# 🧪 Vérification rapide

## WebSGI

```bash
curl -I http://localhost
```

## Grafana

```bash
curl -I http://localhost:3000
```

Une réponse :

```text
HTTP/1.1 302 Found
Location: /login
```

indique que Grafana répond correctement.

## Prometheus

```bash
curl -I http://localhost:9090
```

Une réponse `405 Method Not Allowed` avec `Allow: GET, OPTIONS` peut être normale avec une requête `HEAD`.

Pour tester réellement l'API :

```bash
curl http://localhost:9090/-/healthy
```

Une réponse :

```text
Prometheus Server is Healthy.
```

indique que Prometheus fonctionne.

---

# 🧹 Nettoyage

Voir les conteneurs arrêtés :

```bash
docker ps -a
```

Supprimer un conteneur :

```bash
docker rm <container>
```

Supprimer une image :

```bash
docker rmi <image>
```

⚠️ Éviter les commandes de type :

```bash
docker system prune -a --volumes
```

sur le serveur de production sans vérifier précisément ce qui sera supprimé.

Cette commande peut supprimer des images, conteneurs et volumes nécessaires au fonctionnement du projet.

---

# 📌 Architecture actuelle

```text
                         INTERNET
                             │
                             ▼
                    Azure VM / Ubuntu
                             │
              ┌──────────────┼──────────────┐
              │              │              │
             :80           :3000          :9090
              │              │              │
              ▼              ▼              ▼
          ┌────────┐    ┌─────────┐   ┌────────────┐
          │ Nginx  │    │ Grafana │   │ Prometheus │
          └───┬────┘    └────┬────┘   └────────────┘
              │              │
              │ /api/        │ PromQL
              ▼              ▼
        ┌───────────┐   ┌────────────┐
        │ Spring    │   │ Prometheus │
        │ Boot      │   └────────────┘
        └─────┬─────┘
              │
              ▼
        ┌───────────┐
        │ PostgreSQL│
        └───────────┘
```

---

# 🎯 Prochaines étapes

Le monitoring Docker est maintenant fonctionnel.

Les prochaines améliorations possibles sont :

1. Connecter Grafana à Prometheus.
2. Ajouter les métriques de l'application Spring Boot.
3. Ajouter un exporter PostgreSQL.
4. Ajouter les métriques CPU/RAM/disque de la VM.
5. Ajouter les métriques des conteneurs Docker.
6. Créer des dashboards Grafana.
7. Mettre en place des alertes Grafana/Prometheus.

---

# ⚠️ Bonnes pratiques

* Ne jamais committer les mots de passe dans Git.
* Utiliser `.env` pour les secrets locaux/serveur.
* Conserver les volumes Docker pour les données persistantes.
* Ne pas exposer PostgreSQL directement sur Internet.
* Éviter d'exposer Prometheus publiquement si son interface n'est pas nécessaire depuis Internet.
* Sauvegarder régulièrement les données importantes.
* Vérifier les logs après chaque déploiement.
