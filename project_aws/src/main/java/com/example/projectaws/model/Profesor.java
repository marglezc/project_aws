package com.example.projectaws.model;
import jakarta.validation.constraints.*;

public class Profesor {

    @NotNull(message = "El ID es obligatorio")
    @Min(value = 1, message = "El ID debe ser un entero positivo")
    private Integer id;

    @NotNull(message = "El número de empleado debe ser un entero válido.")
    @Min(value = 1, message = "El número de empleado debe ser un entero positivo.")
    private Integer numeroEmpleado;

    @NotBlank(message = "El nombre es obligatorio y debe ser un texto válido.")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios y deben ser un texto válido.")
    private String apellidos;

    @NotNull(message = "Las horas de clase deben ser un número válido.")
    @Min(value = 0, message = "Las horas de clase deben ser positivas.")
    private Integer horasClase;

    public Profesor() {}

    // Getters
    public int getId() { return id; }
    public Integer getNumeroEmpleado() { return numeroEmpleado; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public Integer getHorasClase() { return horasClase; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNumeroEmpleado(Integer numeroEmpleado) { this.numeroEmpleado = numeroEmpleado; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setHorasClase(Integer horasClase) { this.horasClase = horasClase; }
}