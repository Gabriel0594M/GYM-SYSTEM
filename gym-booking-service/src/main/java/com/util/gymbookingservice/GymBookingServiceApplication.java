package com.util.gymbookingservice;

import org.springframework.boot.SpringApplication; // Clase que arranca la aplicacion Spring Boot
import org.springframework.boot.autoconfigure.SpringBootApplication; // Habilita autoconfiguracion, component scan y config

// Clase de arranque del microservicio de reservas de clases del gimnasio
@SpringBootApplication
public class GymBookingServiceApplication {

    // Punto de entrada de la aplicacion
    public static void main(String[] args) {
        SpringApplication.run(GymBookingServiceApplication.class, args); // Levanta el contexto de Spring Boot
    }
}
