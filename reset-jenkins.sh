#!/bin/bash
echo "⚠️  ATTENTION: Ceci va SUPPRIMER toutes les configurations Jenkins!"
echo ""
read -p "Êtes-vous sûr de vouloir continuer? (oui/non): " confirmation
if [ "$confirmation" != "oui" ]; then
    echo "❌ Annulé."
    exit 1
fi
echo ""
echo "🛑 Arrêt du conteneur Jenkins actuel..."
docker stop my-jenkins 2>/dev/null
echo "🗑️  Suppression du conteneur..."
docker rm my-jenkins 2>/dev/null
echo "📦 Démarrage d'un nouveau Jenkins..."
docker run -d \
  --name my-jenkins \
  -p 9090:8080 \
  -p 50000:50000 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts
echo ""
echo "⏳ Attente du démarrage de Jenkins (30 secondes)..."
sleep 30
echo ""
echo "🔑 Récupération du mot de passe initial..."
PASSWORD=$(docker exec my-jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>/dev/null)
if [ -n "$PASSWORD" ]; then
    echo ""
    echo "=========================================="
    echo "✅ NOUVEAU MOT DE PASSE JENKINS:"
    echo "=========================================="
    echo "$PASSWORD"
    echo "=========================================="
    echo ""
    echo "📋 Utilise ce mot de passe sur: http://localhost:9090"
else
    echo "⚠️  Attends encore 30 secondes et exécute:"
    echo "   docker exec my-jenkins cat /var/jenkins_home/secrets/initialAdminPassword"
fi
