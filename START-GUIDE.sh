#!/bin/bash

echo "🚀 Guide de Démarrage - CI/CD Digital Logistics"
echo "================================================"
echo ""

cat << 'EOF'
📋 ÉTAPES À SUIVRE:

1️⃣  ARRÊTER LES CONTENEURS ACTUELS
   cd ~/Downloads/digitale_logistic
   docker stop my-jenkins postgres 2>/dev/null
   docker rm my-jenkins postgres 2>/dev/null

2️⃣  DÉMARRER AVEC DOCKER COMPOSE
   docker-compose up -d

3️⃣  VÉRIFIER QUE TOUT TOURNE
   docker-compose ps

   Tu devrais voir:
   ✅ postgres-db   (healthy)
   ✅ my-jenkins    (running)
   ✅ sonarqube     (running)

4️⃣  ATTENDRE LE DÉMARRAGE (2-3 minutes)
   docker-compose logs -f jenkins

   Appuie sur Ctrl+C quand tu vois: "Jenkins is fully up and running"

5️⃣  RÉCUPÉRER LE MOT DE PASSE JENKINS
   docker exec my-jenkins cat /var/jenkins_home/secrets/initialAdminPassword

6️⃣  ACCÉDER À JENKINS
   Ouvre: http://localhos t:9090
   Colle le mot de passe obtenu à l'étape 5

7️⃣  CONFIGURATION INITIALE JENKINS:

   a) Installer les plugins suggérés

   b) Plugins additionnels nécessaires:
      - Pipeline
      - Git
      - Maven Integration
      - JaCoCo
      - SonarQube Scanner
      - Docker Pipeline

   c) Créer un utilisateur admin

   d) Configurer les outils (Manage Jenkins → Tools):
      • JDK: Nom = "JDK17", Installation auto Java 17
      • Maven: Nom = "Maven3", Installation auto Maven 3.9.x

8️⃣  CONFIGURER SONARQUBE:

   a) Accède à: http://localhost:9000
      Login initial: admin / admin
      Change le mot de passe

   b) Génère un token:
      My Account → Security → Generate Token
      Nom: "jenkins-token"
      Copie le token généré

   c) Dans Jenkins (Manage Jenkins → Credentials):
      Add Credentials → Secret text
      Secret: (colle le token SonarQube)
      ID: "sonarqube-token"

   d) Configure SonarQube Server (Manage Jenkins → System):
      Nom: "SonarQube-Server"
      URL: http://sonarqube:9000
      Token: sélectionne "sonarqube-token"

9️⃣  CRÉER LE JOB JENKINS:

   a) New Item → Pipeline
      Nom: "digitale-logistic-ci"

   b) Dans Pipeline section:
      Definition: Pipeline script from SCM
      SCM: Git
      Repository URL: (ton repo Git)
      Branch: */main
      Script Path: Jenkinsfile

   c) Ou copie directement le Jenkinsfile dans le script

🔟  TESTER LE PIPELINE:

   a) Clique sur "Build Now"

   b) Vérifie les logs de chaque stage

   c) Les tests devraient maintenant passer avec PostgreSQL!

📊 SERVICES DISPONIBLES:

- Jenkins:     http://localhost:9090
- SonarQube:   http://localhost:9000
- PostgreSQL:  localhost:5432
  • Database:  my_db
  • User:      postgres
  • Password:  postgres

🔍 COMMANDES UTILES:

# Voir les logs
docker-compose logs -f

# Redémarrer un service
docker-compose restart jenkins

# Arrêter tout
docker-compose down

# Arrêter et supprimer les volumes (⚠️  efface tout!)
docker-compose down -v

# Voir l'état des conteneurs
docker-compose ps

# Tester la connexion PostgreSQL depuis Jenkins
docker exec my-jenkins ping -c 2 postgres-db

🐛 DÉPANNAGE:

Si les tests échouent encore:
1. Vérifie que PostgreSQL est healthy: docker-compose ps
2. Vérifie la connexion: docker exec my-jenkins ping postgres-db
3. Regarde les logs: docker-compose logs postgres-db
4. Vérifie que le profil test est activé dans Jenkinsfile

🎉 UNE FOIS TOUT CONFIGURÉ:

Chaque fois que tu push du code, Jenkins:
1. ✅ Build le projet
2. ✅ Lance les tests (avec PostgreSQL)
3. ✅ Génère le rapport de couverture JaCoCo
4. ✅ Analyse le code avec SonarQube
5. ✅ Vérifie le Quality Gate
6. ✅ Package l'application
7. ✅ Crée l'image Docker

================================================
EOF

echo ""
read -p "Veux-tu que je lance docker-compose maintenant? (oui/non): " response

if [ "$response" = "oui" ]; then
    echo ""
    echo "🚀 Lancement de docker-compose..."
    docker-compose up -d

    echo ""
    echo "⏳ Attente du démarrage (30 secondes)..."
    sleep 30

    echo ""
    echo "📊 État des services:"
    docker-compose ps

    echo ""
    echo "🔑 Récupération du mot de passe Jenkins..."
    PASSWORD=$(docker exec my-jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>/dev/null)

    if [ -n "$PASSWORD" ]; then
        echo ""
        echo "=========================================="
        echo "✅ MOT DE PASSE JENKINS:"
        echo "=========================================="
        echo "$PASSWORD"
        echo "=========================================="
        echo ""
        echo "📋 Utilise ce mot de passe sur: http://localhost:9090"
    else
        echo "⚠️  Attends encore 30 secondes et exécute:"
        echo "   docker exec my-jenkins cat /var/jenkins_home/secrets/initialAdminPassword"
    fi
else
    echo ""
    echo "👍 OK! Lance manuellement quand tu es prêt:"
    echo "   cd ~/Downloads/digitale_logistic"
    echo "   docker-compose up -d"
fi

