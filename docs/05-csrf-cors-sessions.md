# Slide 5 — CSRF & CORS in REST APIs

## 🛡️ 1. CSRF — Cross-Site Request Forgery

**What is CSRF?**

* Forcing a logged-in user to perform unwanted actions.
* Works only with **session-based apps** (cookies automatically sent).

### ❌ Why CSRF Is NOT Needed in REST APIs

REST API (with Basic Auth or JWT):

* Does **not** use session cookies
* Browser does **not** auto-send Authorization header
* Cannot be triggered by hidden forms

➡️ **Stateless API = Safe to disable CSRF**

```java
http.csrf(AbstractHttpConfigurer::disable);
```

---

## 🌐 2. What is CORS?

CORS = Cross-Origin Resource Sharing

* Controls which frontend origins can access your backend.

**Example of blocked request:**
Frontend: `http://localhost:3000`
API: `http://localhost:8080`
→ Browser blocks unless CORS allows it.

---

## 🔧 3. Enabling CORS

Option A — Global config

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*");
    }
}
```

Option B — SecurityFilterChain

```java
http.cors(Customizer.withDefaults());
```

---

## 📌 Summary

* CSRF = Disabled for REST APIs (stateless, no cookies)
* CORS = Must be enabled when frontend and backend run on different origins
* CORS protects the browser, **not** the API server
* Required when using React, Vue, Angular frontends
