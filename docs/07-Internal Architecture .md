# Slide 7 — Internal Architecture (UserDetails, Providers, Encoders)

## 👤 1. UserDetails

Represents the authenticated user inside Spring Security.
Contains:

* username
* password (hashed)
* roles / authorities
* account flags

```java
public interface UserDetails {
    String getUsername();
    String getPassword();
    Collection<? extends GrantedAuthority> getAuthorities();
    boolean isAccountNonExpired();
    boolean isAccountNonLocked();
    boolean isCredentialsNonExpired();
    boolean isEnabled();
}
```

Spring provides a ready implementation:

```java
User.builder()
    .username("admin")
    .password(encodedPassword)
    .roles("ADMIN")
    .build();
```

---

## 🔍 2. UserDetailsService

Loads a user from:

* Memory
* Database (JPA)
* LDAP
* API

Used by AuthenticationProvider.

```java
UserDetails loadUserByUsername(String username);
```

Example:

```java
return userRepository.findByUsername(username)
    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
```

---

## 🔐 3. AuthenticationProvider

The component that **performs authentication**.
Default provider: **DaoAuthenticationProvider**.

Flow:

```
1. Receives username/password
2. Calls UserDetailsService → loads user
3. Uses PasswordEncoder to verify password
4. Returns authenticated token
```

Code (simplified):

```java
if (!passwordEncoder.matches(raw, storedHash)) {
    throw new BadCredentialsException("Invalid password");
}
```

---

## 🔑 4. PasswordEncoder (BCrypt)

Why BCrypt?

* Slow algorithm → prevents brute force
* Salted → different hash each time
* Secure & recommended

Example:

```java
String hash = passwordEncoder.encode("admin123");
passwordEncoder.matches("admin123", hash);  // true
```

---

## 📌 Summary

* UserDetails = user model used by Spring Security
* UserDetailsService = loads user from data source
* AuthenticationProvider = validates credentials
* BCryptPasswordEncoder = secure hashing algorithm

▶ These components work together to authenticate any user.
