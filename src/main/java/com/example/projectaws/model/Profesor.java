package com.example.projectaws.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "teachers")
public class Profesor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "El número de empleado debe ser válido.")
    @Min(value = 1, message = "El número de empleado debe ser positivo.")
    private Integer numeroEmpleado;

    @NotBlank(message = "El nombre es obligatorio.")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios.")
    private String apellidos;

    @NotNull(message = "Las horas de clase son obligatorias.")
    @Min(value = 0, message = "Las horas deben ser positivas.")
    private Integer horasClase;

    public Profesor() {}

    public Profesor(Integer numeroEmpleado, String nombres, String apellidos, Integer horasClase) {
        this.numeroEmpleado = numeroEmpleado;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.horasClase = horasClase;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getNumeroEmpleado() { return numeroEmpleado; }
    public void setNumeroEmpleado(Integer numeroEmpleado) { this.numeroEmpleado = numeroEmpleado; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public Integer getHorasClase() { return horasClase; }
    public void setHorasClase(Integer horasClase) { this.horasClase = horasClase; }
}