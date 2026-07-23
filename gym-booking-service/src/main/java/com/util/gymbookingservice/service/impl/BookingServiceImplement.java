package com.util.gymbookingservice.service.impl;

import com.util.gymbookingservice.dto.BookingRequestDto; // DTO de entrada para crear reservas
import com.util.gymbookingservice.dto.BookingResponseDto; // DTO de salida expuesto por la API
import com.util.gymbookingservice.model.BookingStatus; // Enum de estado de la reserva
import com.util.gymbookingservice.model.ClassBooking; // Entidad JPA de la reserva
import com.util.gymbookingservice.repository.ClassBookingRepository; // Acceso a datos de reservas
import com.util.gymbookingservice.service.BookingService; // Interfaz que esta clase implementa
import org.springframework.security.access.AccessDeniedException; // Excepcion para 403 Forbidden
import org.springframework.stereotype.Service; // Marca la clase como un bean de servicio
import org.springframework.transaction.annotation.Transactional; // Delimita transacciones de BD

import java.util.List; // Tipo de retorno para las operaciones de listado
import java.util.NoSuchElementException; // Excepcion cuando no se encuentra una reserva por id

// Implementacion de la logica de negocio de reservas de clases del gimnasio
@Service
public class BookingServiceImplement implements BookingService {

    private final ClassBookingRepository bookingRepository; // Repositorio inyectado por constructor

    // Constructor usado por Spring para inyectar el repositorio (sin usar @Autowired en el campo)
    public BookingServiceImplement(ClassBookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional // Toda la operacion de creacion se ejecuta en una sola transaccion
    public BookingResponseDto createBooking(BookingRequestDto request, String memberUsername) {
        ClassBooking booking = new ClassBooking(); // Nueva entidad vacia a completar
        booking.setMemberUsername(memberUsername); // Username tomado del JWT, nunca del body
        booking.setTrainerUsername(request.trainerUsername()); // Entrenador indicado por el socio
        booking.setClassName(request.className()); // Nombre de la clase reservada
        booking.setClassDate(request.classDate()); // Fecha y hora de la clase
        booking.setStatus(BookingStatus.RESERVED); // Toda reserva nueva inicia en RESERVED

        ClassBooking saved = bookingRepository.save(booking); // Persiste la reserva en la BD
        return toResponseDto(saved); // Convierte la entidad guardada al DTO de salida
    }

    @Override
    @Transactional
    public List<BookingResponseDto> getMyBookings(String memberUsername) {
        return bookingRepository.findByMemberUsername(memberUsername) // Solo reservas del socio
                .stream() // Convierte la lista de entidades en un stream
                .map(this::toResponseDto) // Mapea cada entidad a su DTO de salida
                .toList(); // Colecta el resultado en una lista inmutable
    }

    @Override
    @Transactional
    public List<BookingResponseDto> getMyClasses(String trainerUsername) {
        return bookingRepository.findByTrainerUsername(trainerUsername) // Solo clases del entrenador
                .stream() // Convierte la lista de entidades en un stream
                .map(this::toResponseDto) // Mapea cada entidad a su DTO de salida
                .toList(); // Colecta el resultado en una lista inmutable
    }

    @Override
    @Transactional
    public BookingResponseDto attendBooking(Long bookingId, String trainerUsername) {
        ClassBooking booking = bookingRepository.findById(bookingId) // Busca la reserva por id
                .orElseThrow(() -> new NoSuchElementException("No se encontro la reserva con id " + bookingId));

        // Autorizacion a nivel de dato: el entrenador solo puede marcar asistencia en SUS clases
        if (!booking.getTrainerUsername().equals(trainerUsername)) {
            throw new AccessDeniedException("La reserva no pertenece a una clase dictada por este entrenador");
        }

        // Solo se puede marcar asistencia si la reserva esta en estado RESERVED
        if (booking.getStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Solo se puede atender reservas en estado RESERVED. Estado actual: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.ATTENDED); // Cambia el estado a ATTENDED
        ClassBooking updated = bookingRepository.save(booking); // Persiste el cambio de estado
        return toResponseDto(updated); // Convierte la entidad actualizada al DTO de salida
    }

    @Override
    @Transactional
    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository.findAll() // Sin filtro: acceso total reservado a ADMIN
                .stream() // Convierte la lista de entidades en un stream
                .map(this::toResponseDto) // Mapea cada entidad a su DTO de salida
                .toList(); // Colecta el resultado en una lista inmutable
    }

    @Override
    @Transactional
    public void deleteBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) { // Valida que la reserva exista antes de borrar
            throw new NoSuchElementException("No se encontro la reserva con id: " + bookingId);
        }
        bookingRepository.deleteById(bookingId); // Elimina la reserva de la base de datos
    }

    // Metodo privado de mapeo: convierte la entidad JPA en el DTO que se expone por la API
    private BookingResponseDto toResponseDto(ClassBooking booking) {
        return new BookingResponseDto(
                booking.getId(), // Identificador de la reserva
                booking.getMemberUsername(), // Socio dueño de la reserva
                booking.getTrainerUsername(), // Entrenador asignado
                booking.getClassName(), // Nombre de la clase
                booking.getClassDate(), // Fecha y hora de la clase
                booking.getStatus() // Estado actual de la reserva
        );
    }
}
