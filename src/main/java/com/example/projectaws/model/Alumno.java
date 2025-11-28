package com.example.projectaws.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "students")
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "foto_perfil_url")
    private String fotoPerfilUrl;

    @Column(name = "password")
    private String password;

    public Alumno() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public Float getPromedio() { return promedio; }
    public void setPromedio(Float promedio) { this.promedio = promedio; }

    public String getFotoPerfilUrl() { return fotoPerfilUrl; }
    public void setFotoPerfilUrl(String fotoPerfilUrl) { this.fotoPerfilUrl = fotoPerfilUrl; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}