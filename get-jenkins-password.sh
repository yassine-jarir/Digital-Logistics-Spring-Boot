#!/bin/bash
echo "🔍 Recherche du mot de passe Jenkins..."
echo ""
# Méthode 1: Essayer de lire le fichier de mot de passe initial
PASSWORD=$(docker exec my-jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>/dev/null)
if [ -n "$PASSWORD" ]; then
    echo "✅ Mot de passe trouvé dans le fichier initial:"
    echo "=========================================="
    echo "$PASSWORD"
    echo "=========================================="
    echo ""
    echo "📋 Copie ce mot de passe et colle-le dans Jenkins à:"
    echo "   http://localhost:9090"
    exit 0
fi
# Méthode 2: Chercher dans les logs
echo "⚠️  Fichier de mot de passe initial vide ou inexistant."
echo "🔍 Recherche dans les logs Docker..."
echo ""
PASSWORD_FROM_LOGS=$(docker logs my-jenkins 2>&1 | grep -A 2 "Please use the following password" | tail -1 | xargs)
if [ -n "$PASSWORD_FROM_LOGS" ]; then
    echo "✅ Mot de passe trouvé dans les logs:"
    echo "=========================================="
    echo "$PASSWORD_FROM_LOGS"
    echo "=========================================="
    echo ""
    echo "📋 Copie ce mot de passe et colle-le dans Jenkins."
    exit 0
fi
# Si rien n'est trouvé
echo "❌ Jenkins semble déjà configuré. Mot de passe initial supprimé."
echo ""
echo "🔧 Solutions:"
echo ""
echo "1️⃣  RÉINITIALISER JENKINS (recommandé):"
echo "   ./reset-jenkins.sh"
echo ""
echo "2️⃣  VOIR LES LOGS COMPLETS:"
echo "   docker logs my-jenkins | less"
