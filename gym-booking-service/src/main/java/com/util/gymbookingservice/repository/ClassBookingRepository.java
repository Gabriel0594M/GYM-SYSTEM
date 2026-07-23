package com.util.gymbookingservice.repository;

import com.util.gymbookingservice.model.ClassBooking; // Entidad sobre la que opera el repositorio
import org.springframework.data.jpa.repository.JpaRepository; // Repositorio base de Spring Data JPA

import java.util.List; // Tipo de retorno para las consultas por username

// Repositorio JPA para la entidad ClassBooking; Spring genera la implementacion en runtime
public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {

    // Busca las reservas hechas por un socio especifico (usado en /my-bookings)
    List<ClassBooking> findByMemberUsername(String memberUsername);

    // Busca las reservas de las clases dictadas por un entrenador (usado en /my-classes)
    List<ClassBooking> findByTrainerUsername(String trainerUsername);
}
