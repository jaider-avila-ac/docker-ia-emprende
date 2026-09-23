package com.iaemprender.backend.inteligencia.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "metas_smart")
@Getter
@Setter
@NoArgsConstructor
public class MetaSmart {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "negocio_id", nullable = false)
  private Long negocioId;

  @Column(nullable = false)
  private String titulo;

  @Column(columnDefinition = "TEXT")
  private String especifico;

  @Column(name = "numero_meta")
  private String numeroMeta;

  @Column(name = "fecha_limite")
  private LocalDate fechaLimite;

  @Column(columnDefinition = "TEXT")
  private String medicion;

  @Column(columnDefinition = "TEXT")
  private String pasos;

  @Column(name = "generado_por_ia", nullable = false)
  private boolean generadoPorIa;

  @Column(name = "creado_en", insertable = false, updatable = false)
  private LocalDateTime creadoEn;
}
