# Basic Authentication Migration Guide

## Overview

This document describes the migration from JWT-based authentication to HTTP Basic Authentication in the Digital Logistics API.

## What Changed

### Removed Components

1. **JWT Authentication Filter** (`JwtAuthenticationFilter.java`) - DELETED
2. **JWT Utility Class** (`JwtUtil.java`) - DELETED
3. **JWT Dependencies** - Removed from SecurityConfig

### Updated Components

1. **SecurityConfig.java** - Completely rewritten with Basic Auth
2. **AuthService.java** - Removed JWT token generation

## New Security Configuration

### Authentication Method

- **Type**: HTTP Basic Authentication
- **Format**: `Authorization: Basic <base64(username:password)>`
- **Session**: Stateless (no server-side sessions)

### User Roles

The system now uses three predefined roles with in-memory users:

| Username | Password    | Role              | Access Level |
|----------|-------------|-------------------|--------------|
| admin    | admin123    | ADMIN             | Full access  |
| manager  | manager123  | WAREHOUSE_MANAGER | Inventory & Shipments |
| client   | client123   | CLIENT            | Orders only  |

## Access Control Rules

### Public Endpoints (No Authentication Required)

```
/                           - Root path
/error                      - Error pages
/favicon.ico                - Favicon
/api/auth/**                - Authentication endpoints
/swagger-ui/**              - Swagger UI
/v3/api-docs/**             - OpenAPI documentation
/css/**, /js/**, /images/** - Static resources
/webjars/**                 - WebJars resources
```

### Protected Endpoints (Authentication Required)

#### ADMIN Only
- `/api/admin/**` - Admin management endpoints
- `/api/products/**` - Product management

#### ADMIN or WAREHOUSE_MANAGER
- `/api/inventory/**` - Inventory management
- `/api/shipments/**` - Shipment management

#### CLIENT or ADMIN
- `/api/orders/**` - Order management

#### Any Authenticated User
- All other endpoints require authentication

## How to Use Basic Auth

### Using cURL

```bash
# Admin access
curl -u admin:admin123 http://localhost:8080/api/admin/users

# Warehouse Manager access
curl -u manager:manager123 http://localhost:8080/api/inventory

# Client access
curl -u client:client123 http://localhost:8080/api/orders
```

### Using Postman

1. Open your request
2. Go to the "Authorization" tab
3. Select "Basic Auth" from the Type dropdown
4. Enter username and password
5. Send the request

### Using Swagger UI

1. Navigate to `http://localhost:8080/swagger-ui.html`
2. Click the "Authorize" button (lock icon)
3. Enter username and password in the Basic Auth form
4. Click "Authorize"
5. All subsequent requests will include Basic Auth credentials

### Using JavaScript/Frontend

```javascript
// Using fetch API
const username = 'admin';
const password = 'admin123';
const credentials = btoa(`${username}:${password}`);

fetch('http://localhost:8080/api/admin/users', {
    headers: {
        'Authorization': `Basic ${credentials}`,
        'Content-Type': 'application/json'
    }
})
.then(response => response.json())
.then(data => console.log(data));

// Using Axios
const axios = require('axios');

axios.get('http://localhost:8080/api/admin/users', {
    auth: {
        username: 'admin',
        password: 'admin123'
    }
})
.then(response => console.log(response.data));
```

## Authentication Flow

### Before (JWT)
1. Login with `/api/auth/login` → Receive JWT token
2. Include token in header: `Authorization: Bearer <token>`
3. Token validated on each request

### Now (Basic Auth)
1. Include credentials in every request: `Authorization: Basic <base64(username:password)>`
2. Spring Security validates credentials on each request
3. No token storage or management needed

## Registration Endpoint

The `/api/auth/register` endpoint still works and allows creating new users in the database. However, for authentication, you must use the predefined in-memory users listed above.

**Note**: Database users are not used for Basic Auth in the current configuration. To use database users, you would need to implement a custom `UserDetailsService` that loads users from the database instead of using `InMemoryUserDetailsManager`.

## Testing

### Quick Test Commands

```bash
# Test admin access (should succeed)
curl -u admin:admin123 http://localhost:8080/api/admin/users

# Test client accessing admin endpoint (should fail with 403)
curl -u client:client123 http://localhost:8080/api/admin/users

# Test unauthenticated access (should fail with 401)
curl http://localhost:8080/api/inventory

# Test public endpoint (should succeed)
curl http://localhost:8080/swagger-ui.html
```

## Security Considerations

### Advantages of Basic Auth
- Simple to implement and use
- No token management complexity
- Stateless authentication
- Works well for server-to-server communication

### Important Security Notes

1. **HTTPS Required**: Always use HTTPS in production. Basic Auth sends credentials in base64 encoding, which is easily decodable.

2. **Credential Storage**: Store credentials securely:
   - Never hardcode credentials in frontend code
   - Use environment variables
   - Use secure credential storage (keychain, vault)

3. **Password Strength**: Use strong passwords in production:
   ```java
   // Change default passwords before deployment!
   UserDetails admin = User.builder()
       .username("admin")
       .password(passwordEncoder().encode("STRONG_PASSWORD_HERE"))
       .roles("ADMIN")
       .build();
   ```

4. **Rate Limiting**: Consider implementing rate limiting to prevent brute force attacks

5. **Audit Logging**: Log authentication attempts for security monitoring

## Configuration for Different Environments

### Development
Current in-memory users are suitable for development

### Production
Replace `InMemoryUserDetailsManager` with database-backed authentication:

```java
@Bean
public UserDetailsService userDetailsService(UserRepository userRepository) {
    return email -> {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .build();
    };
}
```

## Troubleshooting

### 401 Unauthorized
- Check username and password are correct
- Verify credentials are base64 encoded correctly
- Check Authorization header format: `Basic <credentials>`

### 403 Forbidden
- User is authenticated but doesn't have required role
- Check endpoint access rules in SecurityConfig
- Verify user has correct role assigned

### Swagger Not Working
- Clear browser cache
- Click "Authorize" button in Swagger UI
- Re-enter credentials

## Migration Checklist

- [x] Remove JwtAuthenticationFilter.java
- [x] Remove JwtUtil.java
- [x] Update SecurityConfig.java with Basic Auth
- [x] Update AuthService.java to remove JWT dependency
- [x] Configure in-memory users with roles
- [x] Set up role-based access control
- [x] Test all endpoints with Basic Auth
- [ ] Update API documentation
- [ ] Update frontend/client code to use Basic Auth
- [ ] Change default passwords for production
- [ ] Enable HTTPS for production
- [ ] Implement rate limiting (optional)
- [ ] Set up audit logging (optional)

## Additional Resources

- [Spring Security Basic Auth Documentation](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/basic.html)
- [HTTP Basic Authentication RFC](https://datatracker.ietf.org/doc/html/rfc7617)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)

## Support

For questions or issues with the migration, please contact the development team.

