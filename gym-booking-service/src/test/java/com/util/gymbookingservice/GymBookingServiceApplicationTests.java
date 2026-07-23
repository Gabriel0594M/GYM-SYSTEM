package com.util.gymbookingservice;

import org.junit.jupiter.api.Test; // Anotacion de JUnit 5 para marcar un metodo de prueba
import org.springframework.boot.test.context.SpringBootTest; // Levanta el contexto completo de Spring Boot

// Prueba minima que verifica que el contexto de la aplicacion levanta sin errores
@SpringBootTest
class GymBookingServiceApplicationTests {

    @Test
    void contextLoads() {
        // Prueba intencionalmente vacia: si el contexto no levanta, la prueba falla sola
    }
}
