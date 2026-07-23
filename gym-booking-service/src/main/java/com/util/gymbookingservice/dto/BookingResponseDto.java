package com.util.gymbookingservice.dto;

import com.util.gymbookingservice.model.BookingStatus; // Enum de estado que se expone al cliente

import java.time.LocalDateTime; // Tipo de dato para la fecha y hora de la clase

// DTO de salida; es lo unico que la API expone, la entidad JPA nunca se devuelve directo
public record BookingResponseDto(
        Long id, // Identificador de la reserva
        String memberUsername, // Socio dueño de la reserva
        String trainerUsername, // Entrenador asignado a la clase
        String className, // Nombre de la clase
        LocalDateTime classDate, // Fecha y hora de la clase
        BookingStatus status // Estado actual de la reserva
) {
}
