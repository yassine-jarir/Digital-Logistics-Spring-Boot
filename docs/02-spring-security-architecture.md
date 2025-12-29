# 02 — Architecture Moderne de Spring Security (Version Simple)

## 1. SecurityFilterChain
Le cœur de Spring Security.  
Chaque requête HTTP passe dedans.

Il contient :
- Basic Auth
- CSRF
- CORS
- Permissions (roles)
- Sessions
- Exception Handling

➡️ Sans SecurityFilterChain → aucune sécurité ne fonctionne.

## 3. AuthenticationManager
Le *chef* de l’authentification.

Il :
- reçoit username/password
- essaye de vérifier l’utilisateur grâce aux AuthenticationProviders

Si tout est bon → authenticated  
Si non → 401 Unauthorized

---

## 4. AuthenticationProvider
Le vrai vérificateur des credentials.

Il :
- cherche le user via UserDetailsService
- compare le password via PasswordEncoder
- charge les rôles
- valide ou rejette l’authentification

Dans Spring :
- **DaoAuthenticationProvider** est celui qui gère username/password.

---

## 5. UserDetails & UserDetailsService
- **UserDetails** : représente l’utilisateur (username, password, roles)
- **UserDetailsService** : où et comment on charge l’utilisateur

Dans notre projet :
- on utilise **InMemoryUserDetailsManager**

Plus tard, on pourra utiliser une base de données.

---

## 6. PasswordEncoder (BCrypt)
Spring interdit les mots de passe en clair.  
BCrypt :
- hash sécurisé
- sel automatique
- conçu pour éviter brute-force

Toujours utiliser :
`BCryptPasswordEncoder`.

---

## 7. Roles vs Authorities
Dans Spring :

- `hasRole("ADMIN")` attend “ROLE_ADMIN”
- `hasAuthority("ADMIN")` est plus bas niveau

Spring ajoute automatiquement `ROLE_` devant les rôles.

---

## 8. Disparition de WebSecurityConfigurerAdapter
Depuis Spring Security 6 :
❌ plus de WebSecurityConfigurerAdapter  
✔ configuration via des beans :

- SecurityFilterChain
- PasswordEncoder
- UserDetailsService

Plus moderne et plus clair.

---

## 9. Schéma simple du flux d'une requête. 

Client
↓
DelegatingFilterProxy
↓
SecurityFilterChain
↓
BasicAuthenticationFilter
↓
AuthenticationManager
↓
AuthenticationProvider
↓
UserDetailsService
↓
PasswordEncoder
↓
(OK → 200) / (KO → 401 ou 403)