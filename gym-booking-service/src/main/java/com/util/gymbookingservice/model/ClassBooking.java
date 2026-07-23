package com.util.gymbookingservice.model;

import jakarta.persistence.Column; // Anotacion para mapear columnas de la tabla
import jakarta.persistence.Entity; // Anotacion que marca la clase como entidad JPA
import jakarta.persistence.EnumType; // Enum que indica como se persiste un Enum en la BD
import jakarta.persistence.Enumerated; // Anotacion para mapear campos de tipo Enum
import jakarta.persistence.GeneratedValue; // Anotacion para autogenerar el identificador
import jakarta.persistence.GenerationType; // Estrategia de autogeneracion del identificador
import jakarta.persistence.Id; // Anotacion que marca el campo como llave primaria
import jakarta.persistence.Table; // Anotacion para indicar el nombre de la tabla
import lombok.AllArgsConstructor; // Genera un constructor con todos los campos
import lombok.Data; // Genera getters, setters, equals, hashCode y toString
import lombok.NoArgsConstructor; // Genera un constructor vacio, requerido por JPA

import java.time.LocalDateTime; // Tipo usado para representar fecha y hora de la clase

// Entidad JPA que representa la reserva de un socio a una clase grupal del gimnasio
@Entity
@Table(name = "class_bookings") // Nombre de la tabla en la base de datos
@Data // Lombok genera los metodos boilerplate de la clase
@NoArgsConstructor // Constructor vacio requerido por Hibernate
@AllArgsConstructor // Constructor con todos los argumentos, util para tests
public class ClassBooking {

    @Id // Marca este campo como la llave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // El id lo autogenera la base de datos
    private Long id;

    @Column(name = "member_username", nullable = false) // Username del socio, nunca nulo
    private String memberUsername; // Se obtiene siempre del JWT, nunca del body de la peticion

    @Column(name = "trainer_username", nullable = false) // Username del entrenador asignado
    private String trainerUsername; // Usado para la autorizacion a nivel de dato en /attend

    @Column(name = "class_name", nullable = false) // Nombre de la clase reservada
    private String className; // Ejemplo: Spinning, Yoga, CrossFit

    @Column(name = "class_date", nullable = false) // Fecha y hora programada de la clase
    private LocalDateTime classDate;

    @Enumerated(EnumType.STRING) // Persiste el enum como texto legible en la BD
    @Column(name = "status", nullable = false) // Columna del estado de la reserva
    private BookingStatus status = BookingStatus.RESERVED; // Toda reserva nueva inicia en RESERVED
}
