package com.util.gymbookingservice.config;

import org.springframework.context.annotation.Bean; // Declara un bean administrado por Spring
import org.springframework.context.annotation.Configuration; // Marca la clase como configuracion
import org.springframework.http.HttpMethod; // Enum de metodos HTTP usados en las reglas de rutas
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Builder de la cadena de seguridad
import org.springframework.security.config.http.SessionCreationPolicy; // Politica de creacion de sesion
import org.springframework.security.core.GrantedAuthority; // Representa un permiso/rol otorgado
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Implementacion simple de GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt; // Representa el token JWT ya validado
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter; // Convierte el JWT en Authentication
import org.springframework.security.web.SecurityFilterChain; // Cadena de filtros de seguridad HTTP

import java.util.Collection; // Tipo de retorno de la coleccion de autoridades
import java.util.List; // Lista vacia usada cuando el token no trae roles
import java.util.Map; // Tipo del claim realm_access del token
import java.util.stream.Collectors; // Utilidad para colectar el stream de roles

// Configuracion de seguridad: OAuth2 Resource Server contra el realm gym-system de Keycloak
@Configuration
public class SecurityConfig {

    // Define la cadena de filtros de seguridad y las reglas de autorizacion por ruta y rol
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable()) // API stateless con JWT: no se necesita proteccion CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sin sesion HTTP
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/bookings").hasRole("MEMBER") // Crear reserva: solo MEMBER
                        .requestMatchers("/api/bookings/my-bookings").hasRole("MEMBER") // Reservas propias: solo MEMBER
                        .requestMatchers("/api/bookings/my-classes").hasRole("TRAINER") // Clases propias: solo TRAINER
                        .requestMatchers(HttpMethod.PATCH, "/api/bookings/*/attend").hasRole("TRAINER") // Marcar asistencia: solo TRAINER
                        .requestMatchers(HttpMethod.GET, "/api/bookings").hasRole("ADMIN") // Listar todas: solo ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/bookings/**").hasRole("ADMIN") // Eliminar cualquiera: solo ADMIN
                        .anyRequest().authenticated()) // Cualquier otra ruta solo requiere estar autenticado
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))); // Habilita validacion de JWT
        return httpSecurity.build(); // Construye la cadena de filtros configurada
    }

    // Crea el conversor que transforma el JWT de Keycloak en un objeto Authentication de Spring
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter(); // Conversor base de Spring Security
        converter.setJwtGrantedAuthoritiesConverter(this::extractRealmRoles); // Usa nuestra logica de extraccion de roles
        return converter; // Devuelve el conversor ya configurado
    }

    // Extrae los roles del claim realm_access.roles del token y los mapea a authorities ROLE_*
    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access"); // Claim estandar de Keycloak con los roles
        if (realmAccess == null || realmAccess.get("roles") == null) { // Si no hay roles en el token
            return List.of(); // El usuario queda sin autoridades (autenticado pero sin roles)
        }
        List<String> roles = (List<String>) realmAccess.get("roles"); // Lista cruda de roles del realm
        return roles.stream() // Recorre cada rol del token
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Antepone el prefijo ROLE_ requerido por hasRole
                .collect(Collectors.toList()); // Colecta el resultado como coleccion de authorities
    }
}
