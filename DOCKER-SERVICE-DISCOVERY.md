# 🐳 Docker Service Discovery - Solution Complète

## ✅ 1. La Propriété Corrigée

**Avant (ne fonctionne pas dans Docker):**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/my_db
```

**Après (fonctionne dans Docker):**
```properties
spring.datasource.url=jdbc:postgresql://postgres-db:5432/my_db
```

## 📍 2. Emplacement du Fichier

**Fichier:** `src/test/resources/application-test.properties`

**Pourquoi cet emplacement?**
- Ce fichier est lu automatiquement lors des tests avec `@ActiveProfiles("test")`
- Il override les propriétés de `application.properties` principal
- Il est inclus dans le classpath des tests Maven/Jenkins

## 🔍 3. Pourquoi Ça Fonctionne?

### Docker Service Discovery (DNS interne)

Quand plusieurs conteneurs Docker tournent sur le **même réseau Docker**, Docker fournit un **DNS interne automatique** qui permet aux conteneurs de se découvrir mutuellement par leur **nom de service**.

**Comment ça marche:**

1. **Ton Docker Compose définit:**
   ```yaml
   services:
     postgres-db:        # ← Nom du service
       image: postgres
   ```

2. **Docker crée automatiquement:**
   - Un réseau virtuel privé entre les conteneurs
   - Une entrée DNS: `postgres-db` → adresse IP du conteneur PostgreSQL

3. **Quand Jenkins (dans son conteneur) exécute les tests:**
   - Il lit `spring.datasource.url=jdbc:postgresql://postgres-db:5432/my_db`
   - Il demande au DNS Docker: "Quelle est l'adresse de `postgres-db`?"
   - Docker répond avec l'IP interne du conteneur PostgreSQL
   - La connexion s'établit correctement!

### Pourquoi `localhost` ne fonctionne pas?

- `localhost` dans un conteneur = **le conteneur lui-même**
- PostgreSQL tourne dans un **autre conteneur**
- Il faut utiliser le **nom du service Docker** pour accéder aux autres conteneurs du réseau

## 🚀 Configuration Complète Appliquée

J'ai mis à jour ton fichier `src/test/resources/application-test.properties`:

```properties
# PostgreSQL Configuration - Docker Network
spring.datasource.url=jdbc:postgresql://postgres-db:5432/my_db
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=create-drop
```

## 🔧 Configuration Jenkins/Docker Requise

Pour que ça fonctionne, assure-toi que:

### 1. Docker Compose (ou équivalent) définit bien le service:
```yaml
version: '3.8'
services:
  postgres-db:  # ← Ce nom DOIT correspondre
    image: postgres:17
    environment:
      POSTGRES_DB: my_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    networks:
      - jenkins-network

  jenkins:
    image: jenkins/jenkins:lts
    depends_on:
      - postgres-db
    networks:
      - jenkins-network

networks:
  jenkins-network:
    driver: bridge
```

### 2. Jenkinsfile lance les tests avec le bon profil:
```groovy
stage('Unit Tests') {
    steps {
        sh './mvnw test -Dspring.profiles.active=test'
    }
}
```

## 📋 Vérification Rapide

Pour vérifier que le DNS Docker fonctionne:

```bash
# Depuis le conteneur Jenkins, teste la résolution DNS:
docker exec my-jenkins ping -c 2 postgres-db

# Si ça fonctionne, tu verras:
# PING postgres-db (172.18.0.2): 56 data bytes
# 64 bytes from 172.18.0.2: icmp_seq=0 ttl=64 time=0.123 ms
```

## ✅ Résumé

| Élément | Valeur |
|---------|--------|
| **URL corrigée** | `jdbc:postgresql://postgres-db:5432/my_db` |
| **Fichier** | `src/test/resources/application-test.properties` |
| **Raison technique** | **Docker Service Discovery (DNS interne)** |
| **Nom du service** | `postgres-db` (défini dans Docker Compose) |

Maintenant tes tests Jenkins devraient passer! 🎉

