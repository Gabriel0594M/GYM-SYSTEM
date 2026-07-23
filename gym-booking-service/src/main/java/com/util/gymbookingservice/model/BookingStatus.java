package com.util.gymbookingservice.model;

// Enum que representa el estado del ciclo de vida de una reserva de clase
public enum BookingStatus {
    RESERVED, // La reserva fue creada y aun no se dicta/asiste la clase
    ATTENDED, // El entrenador marco al socio como asistente a la clase
    CANCELLED // La reserva fue cancelada (no se expone endpoint para esto en esta actividad)
}
