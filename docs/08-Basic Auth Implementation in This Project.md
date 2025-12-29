# Slide 8 — Basic Auth Implementation in This Project

## 🧱 1. Security Configuration Overview

The project uses **Basic Authentication + Role-Based Access Control**.
Implementation file:

```
src/main/java/com/logistic/digitale_logistic/config/SecurityConfig.java
```

---

## 🔧 2. SecurityFilterChain (Core Configuration)

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/error", "/favicon.ico").permitAll()
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/products/**").hasRole("ADMIN")

            .requestMatchers("/api/inventory/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
            .requestMatchers("/api/shipments/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")

            .requestMatchers("/api/orders/**").hasAnyRole("CLIENT", "ADMIN")

            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults())
        .formLogin(AbstractHttpConfigurer::disable);

    return http.build();
}
```

---

## 👥 3. In-Memory Users (For the POC)

```java
@Bean
public UserDetailsService userDetailsService() {
    UserDetails admin = User.builder()
        .username("admin")
        .password(passwordEncoder().encode("admin123"))
        .roles("ADMIN")
        .build();

    UserDetails manager = User.builder()
        .username("manager")
        .password(passwordEncoder().encode("manager123"))
        .roles("WAREHOUSE_MANAGER")
        .build();

    UserDetails client = User.builder()
        .username("client")
        .password(passwordEncoder().encode("client123"))
        .roles("CLIENT")
        .build();

    return new InMemoryUserDetailsManager(admin, manager, client);
}
```

---

## 🔐 4. PasswordEncoder (BCrypt)

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

* Hashes all passwords
* Secure against brute force attacks

---

## 🎯 5. Protected Endpoints

| Endpoint            | Allowed Roles            |
| ------------------- | ------------------------ |
| `/api/admin/**`     | ADMIN                    |
| `/api/products/**`  | ADMIN                    |
| `/api/inventory/**` | ADMIN, WAREHOUSE_MANAGER |
| `/api/shipments/**` | ADMIN, WAREHOUSE_MANAGER |
| `/api/orders/**`    | CLIENT, ADMIN            |

---

## 🧪 6. Testing with cURL

```bash
curl -u admin:admin123 http://localhost:8080/api/products   # OK
curl -u client:client123 http://localhost:8080/api/products # 403 Forbidden
```

---

## 🧪 7. Testing with Postman

* Select **Authorization → Basic Auth**
* Enter username/password
* Test role-based access for each user

---

## 📌 Summary

* Basic Auth implemented using HttpBasic
* Stateless API → no sessions
* Role-based access applied at URL level and method level
* BCrypt ensures password security
* Implementation fully matches **project brief requirements**
