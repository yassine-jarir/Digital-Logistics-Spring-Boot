# Slide 4 — HTTP Basic Authentication

## 🔐 1. What is Basic Auth?

Basic Auth is a simple authentication mechanism where:

* The client sends **username:password** encoded in Base64
* Sent in every request via:

```
Authorization: Basic <Base64(username:password)>
```

* Stateless → No session saved on server

---

## 📦 2. Base64 is NOT Encryption

Base64 is only **encoding**, anyone can decode it.
Example:

```
echo "YWRtaW46YWRtaW4xMjM=" | base64 -d
→ admin:admin123
```

➡️ This is why HTTPS is mandatory.

---

## 🔒 3. Why HTTPS is Required

Without HTTPS:

* Passwords travel in plain text (Base64 is reversible)
* Exposed to sniffing / MITM attacks

With HTTPS:

* Entire request is encrypted
* Credentials protected in transit

---

## 🧩 4. How BasicAuthenticationFilter Works

Simplified flow:

```
1. Request arrives
2. Filter checks 'Authorization: Basic ...' header
3. Decodes Base64 → username + password
4. Creates Authentication token
5. Sends to AuthenticationManager
6. Provider verifies credentials via UserDetailsService + BCrypt
7. On success → SecurityContext updated
```

---

## 🚫 5. Limitations of Basic Auth

* Credentials sent with every request
* No built-in logout
* Base64 is reversible
* Not ideal for public production apps

---

## ✅ 6. When Basic Auth is Good

* Internal tools
* Quick prototypes
* Backend-only APIs
* Educational projects

Used here because:
✔ Easy to implement
✔ Perfect for learning Spring Security
✔ Matches project brief requirements
