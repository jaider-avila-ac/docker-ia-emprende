package com.iaemprender.backend.apikeys.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proveedores_ia")
@Getter
@Setter
@NoArgsConstructor
public class ProveedorIA {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, unique = true, length = 30)
  private String codigo;

  @Column(nullable = false, length = 50)
  private String nombre;

  @Column(nullable = false)
  private boolean activo;
}
