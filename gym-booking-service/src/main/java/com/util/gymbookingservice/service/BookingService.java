package com.util.gymbookingservice.service;

import com.util.gymbookingservice.dto.BookingRequestDto; // DTO de entrada para crear reservas
import com.util.gymbookingservice.dto.BookingResponseDto; // DTO de salida expuesto por la API

import java.util.List; // Tipo de retorno para las operaciones de listado

// Contrato de la logica de negocio de reservas de clases; el controller solo conoce esta interfaz
public interface BookingService {

    // Crea una reserva a nombre del socio autenticado (username ya extraido del JWT)
    BookingResponseDto createBooking(BookingRequestDto request, String memberUsername);

    // Devuelve unicamente las reservas del socio autenticado
    List<BookingResponseDto> getMyBookings(String memberUsername);

    // Devuelve unicamente las reservas de las clases dictadas por el entrenador autenticado
    List<BookingResponseDto> getMyClasses(String trainerUsername);

    // Marca una reserva como ATTENDED; valida que el entrenador autenticado sea el asignado
    BookingResponseDto attendBooking(Long bookingId, String trainerUsername);

    // Devuelve todas las reservas del sistema, sin restriccion de propietario (solo ADMIN)
    List<BookingResponseDto> getAllBookings();

    // Elimina cualquier reserva del sistema (solo ADMIN)
    void deleteBooking(Long bookingId);
}
