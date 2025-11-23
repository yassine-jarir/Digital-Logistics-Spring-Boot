# Slide 1 — Security Fundamentals

## 🔐 1. Authentication vs Authorization

* **Authentication:** Who are you? (identity verification)
* **Authorization:** What can you access? (permissions)

```
Authentication → Username + Password
Authorization → Roles / Permissions
```

---

## ⚠️ 2. Common Security Attacks

### Brute Force

* Guessing many password combinations.
* **Defense:** BCrypt, rate limiting.

### XSS

* Injecting malicious JavaScript.
* **Defense:** Validation, output encoding.

### CSRF

* Forcing user actions without consent.
* **Defense:** CSRF tokens (NOT needed in stateless APIs).

### Session Fixation

* Attacker controls session ID.
* **Defense:** Regenerate session.

---

## 🔒 3. Importance of HTTPS

* Encrypts credentials.
* Prevents Man‑In‑The‑Middle attacks.
* Required for Basic Auth in production.

---

## 🛡 4. Defense in Depth

Layers of security:

* Network → Firewalls
* App → Authentication / Authorization
* DB → Hash passwords
* Monitoring → Logging

---

## 💬 5. Why Backend Security?

* Frontend validation is never enough.
* APIs must enforce full security.
* Clients can be bypassed easily.
