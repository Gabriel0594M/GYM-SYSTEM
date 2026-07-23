# gym-booking-service

Microservicio de reservas de clases del gimnasio (Actividad 6: Autenticación y Autorización con JWT, OAuth2/OIDC y Keycloak). Arquitectura en capas: `model` → `repository` → `service`/`service.impl` → `controller`, con DTOs de entrada/salida y OAuth2 Resource Server contra Keycloak.

La infraestructura (Postgres, Keycloak, Nginx) se levanta desde el `docker-compose.yml` de la raíz del repositorio, no desde esta carpeta: este módulo reutiliza el mismo Keycloak (agregando un realm nuevo) y el mismo gateway Nginx (agregando una ruta nueva) que ya usa el resto del repo.

Los puertos de host (`8280` para Keycloak, `8090` para el gateway) son distintos de los "estándar" (`8180`/`8080`) a propósito, para no chocar con otro proyecto de microservicios que use esos mismos puertos/nombres de contenedor en la misma máquina.

## Parte A — Checklist de configuración manual en Keycloak

1. Desde la raíz del repo, levanta la infraestructura: `docker compose up postgres-keycloak keycloak postgres-gym gym-booking-service`.
2. Entra a la consola de administración en `http://localhost:8280` (usuario `admin` / contraseña `admin`).
3. **Crear el realm**: menú superior izquierdo → *Create realm* → nombre `gym-system`.
4. **Crear el client**:
   - *Clients* → *Create client* → Client ID: `gym-app`.
   - *Capability config*: `Client authentication: ON` (client confidencial), habilitar `Standard flow` y `Direct access grants`.
   - En *Login settings*, agrega en *Valid redirect URIs* algo como `https://oauth.pstmn.io/v1/callback` (callback estándar de Postman) y `http://localhost/*` si vas a probar el Authorization Code flow.
   - Guarda y copia el *Client secret* desde la pestaña *Credentials* (lo necesitarás en la colección de Postman).
5. **Crear los roles de realm**: *Realm roles* → *Create role* → crea `MEMBER`, `TRAINER` y `ADMIN` (mayúsculas).
6. **Crear los usuarios de prueba**: *Users* → *Add user* para cada uno de:
   - `member.test`
   - `trainer.test`
   - `admin.test`

   Para cada usuario: en *Credentials* asigna una contraseña marcando `Temporary: OFF` (no temporal), y en *Role mapping* asigna el rol de realm correspondiente (uno por usuario).

## Notas de arquitectura

- El `memberUsername`/`trainerUsername` de cada reserva se obtiene siempre del claim `preferred_username` del JWT ya validado (ver `BookingController`), nunca del cuerpo de la petición.
- La autorización por rol se resuelve en `SecurityConfig` (matchers por ruta/método + rol).
- La autorización a nivel de dato (que un entrenador solo pueda marcar asistencia en sus propias clases) se valida en `BookingServiceImplement.attendBooking`, comparando el `trainerUsername` del JWT contra el dueño de la reserva.

## Ejecución local (sin Docker)

1. Desde la raíz del repo: `docker compose up postgres-keycloak keycloak postgres-gym`.
2. Completa la Parte A de este checklist.
3. Ejecuta `./mvnw spring-boot:run` desde esta carpeta (usa `application.properties`, que apunta a `localhost:5434` y al issuer `http://localhost:8280/realms/gym-system`).

## Ejecución con Docker Compose completo

Desde la raíz del repositorio (no desde esta carpeta):

```
docker compose up
```

La API queda expuesta a través del gateway en `http://localhost:8090/api/bookings/...`.
