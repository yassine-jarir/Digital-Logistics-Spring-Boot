# Slide 3 — Modern Configuration (Spring Security 6+)

## ⚙️ 1. HttpSecurity Core Setup

Modern Spring Security uses `SecurityFilterChain` instead of the old WebSecurityConfigurerAdapter.

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth 
            .requestMatchers("/public/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults())
        .formLogin(AbstractHttpConfigurer::disable);

    return http.build();
}
```

---

## 🛡️ 2. Key Configuration Concepts

### 🔹 CSRF Disabled

* REST APIs are **stateless** → no cookies → CSRF not needed.

### 🔹 Stateless Sessions

* No JSESSIONID
* Every request must send credentials
* Scalable + clean

### 🔹 Basic Auth Enabled

* Simple
* Built-in
* Ideal for learning phase / internal APIs

### 🔹 Form Login Disabled

* No login page
* Not needed in API context

---

## 🔑 3. Users & PasswordEncoder

### PasswordEncoder

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

* Secure hashing
* Salted
* Slow for brute-force protection

### In-Memory Users

```java
@Bean
public UserDetailsService userDetailsService() {
    UserDetails admin = User.builder()
        .username("admin")
        .password(passwordEncoder().encode("admin123"))
        .roles("ADMIN")
        .build();

    return new InMemoryUserDetailsManager(admin);
}
```

---

## 📌 4. Summary

* Configuration = SecurityFilterChain
* Stateless API = No sessions
* Basic Auth = enabled
* BCrypt hashing = required
* Users stored in memory for the POC
