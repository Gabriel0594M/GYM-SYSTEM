package com.util.gymbookingservice.controller;

import com.util.gymbookingservice.dto.BookingRequestDto; // DTO de entrada para crear reservas
import com.util.gymbookingservice.dto.BookingResponseDto; // DTO de salida expuesto por la API
import com.util.gymbookingservice.service.BookingService; // Logica de negocio de reservas
import jakarta.validation.Valid; // Activa la validacion de Bean Validation sobre el DTO
import org.springframework.http.HttpStatus; // Codigos de estado HTTP usados en las respuestas
import org.springframework.http.ResponseEntity; // Envoltorio estandar de respuesta HTTP
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Inyecta el JWT autenticado
import org.springframework.security.oauth2.jwt.Jwt; // Representa el token JWT ya validado
import org.springframework.web.bind.annotation.DeleteMapping; // Mapea peticiones DELETE
import org.springframework.web.bind.annotation.GetMapping; // Mapea peticiones GET
import org.springframework.web.bind.annotation.PatchMapping; // Mapea peticiones PATCH
import org.springframework.web.bind.annotation.PathVariable; // Extrae variables de la ruta
import org.springframework.web.bind.annotation.PostMapping; // Mapea peticiones POST
import org.springframework.web.bind.annotation.RequestBody; // Extrae el cuerpo de la peticion
import org.springframework.web.bind.annotation.RequestMapping; // Define el prefijo de rutas del controller
import org.springframework.web.bind.annotation.RestController; // Marca la clase como controlador REST

import java.util.List; // Tipo de retorno para los endpoints de listado

// Controlador REST con los endpoints de reservas de clases del gimnasio
@RestController
@RequestMapping("/api/bookings") // Prefijo comun a todos los endpoints de este controller
public class BookingController {

    private final BookingService bookingService; // Servicio inyectado por constructor

    // Constructor usado por Spring para inyectar el servicio (sin usar @Autowired en el campo)
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // POST /api/bookings -> Rol MEMBER: crea una reserva a nombre del socio autenticado
    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody BookingRequestDto request,
                                                              @AuthenticationPrincipal Jwt jwt) {
        String memberUsername = jwt.getClaimAsString("preferred_username"); // Username real del token
        BookingResponseDto created = bookingService.createBooking(request, memberUsername); // Crea la reserva
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // Responde 201 Created
    }

    // GET /api/bookings/my-bookings -> Rol MEMBER: solo las reservas del socio autenticado
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponseDto>> getMyBookings(@AuthenticationPrincipal Jwt jwt) {
        String memberUsername = jwt.getClaimAsString("preferred_username"); // Username real del token
        return ResponseEntity.ok(bookingService.getMyBookings(memberUsername)); // Responde 200 OK
    }

    // GET /api/bookings/my-classes -> Rol TRAINER: reservas de las clases que dicta
    @GetMapping("/my-classes")
    public ResponseEntity<List<BookingResponseDto>> getMyClasses(@AuthenticationPrincipal Jwt jwt) {
        String trainerUsername = jwt.getClaimAsString("preferred_username"); // Username real del token
        return ResponseEntity.ok(bookingService.getMyClasses(trainerUsername)); // Responde 200 OK
    }

    // PATCH /api/bookings/{id}/attend -> Rol TRAINER: marca la reserva como ATTENDED
    @PatchMapping("/{id}/attend")
    public ResponseEntity<BookingResponseDto> attendBooking(@PathVariable Long id,
                                                              @AuthenticationPrincipal Jwt jwt) {
        String trainerUsername = jwt.getClaimAsString("preferred_username"); // Necesario para validar dueño
        return ResponseEntity.ok(bookingService.attendBooking(id, trainerUsername)); // Responde 200 OK
    }

    // GET /api/bookings -> Rol ADMIN: todas las reservas del sistema, sin restriccion
    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings()); // Responde 200 OK
    }

    // DELETE /api/bookings/{id} -> Rol ADMIN: elimina cualquier reserva del sistema
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id); // Elimina la reserva indicada
        return ResponseEntity.noContent().build(); // Responde 204 No Content
    }
}
