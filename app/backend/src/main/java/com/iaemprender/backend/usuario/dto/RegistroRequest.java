package com.iaemprender.backend.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record RegistroRequest(
    @NotBlank @Email String correo,
    @NotBlank @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password,
    @NotBlank String nombre,
    @NotBlank String apellido,
    @NotNull @Past(message = "La fecha de nacimiento debe ser en el pasado") LocalDate fechaNacimiento) {}
