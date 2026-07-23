package com.util.gymbookingservice.dto;

import jakarta.validation.constraints.Future; // Valida que la fecha de la clase sea futura
import jakarta.validation.constraints.NotBlank; // Valida que el texto no sea nulo ni vacio
import jakarta.validation.constraints.NotNull; // Valida que el campo no sea nulo

import java.time.LocalDateTime; // Tipo de dato para la fecha y hora de la clase

// DTO de entrada para crear una reserva; notese que NO incluye memberUsername,
// porque ese dato se obtiene siempre del JWT autenticado, nunca del cliente
public record BookingRequestDto(

        @NotBlank(message = "El username del entrenador es obligatorio") // No puede llegar vacio
        String trainerUsername,

        @NotBlank(message = "El nombre de la clase es obligatorio") // No puede llegar vacio
        String className,

        @NotNull(message = "La fecha de la clase es obligatoria") // Debe venir en la peticion
        @Future(message = "La fecha de la clase debe ser futura") // No se permiten clases pasadas
        LocalDateTime classDate
) {
}
