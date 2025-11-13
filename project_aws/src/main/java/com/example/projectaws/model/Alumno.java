package com.example.projectaws.model;
import jakarta.validation.constraints.*;

public class Alumno {

    @NotNull(message = "El ID es obligatorio") // Asegura que el cliente lo envíe
    @Min(value = 1, message = "El ID debe ser un entero positivo")
    private Integer id;

    @NotBlank(message = "Nombre obligatorio.")
    private String nombres;

    @NotBlank(message = "Apellidos obligatorios.")
    private String apellidos;

    @Pattern(regexp = "A.*", message = "Matrícula inválida.")
    private String matricula;

    @NotNull(message = "Promedio inválido.")
    @Min(value = 0, message = "Promedio inválido.")
    private Float promedio;

    public Alumno() {}

    // Getters
    public int getId() { return id; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getMatricula() { return matricula; }
    public Float getPromedio() { return promedio; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public void setPromedio(Float promedio) { this.promedio = promedio; }
}