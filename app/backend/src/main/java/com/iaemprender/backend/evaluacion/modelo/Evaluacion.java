package com.iaemprender.backend.evaluacion.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "evaluaciones")
@Getter
@Setter
@NoArgsConstructor
public class Evaluacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "iniciativa_id", nullable = false)
  private Long iniciativaId;

  @Column(name = "plan_semanal_id")
  private Long planSemanalId;

  @Column(name = "se_logro", nullable = false)
  private SeLogro seLogro;

  @Column(nullable = false)
  private Dificultad dificultad;

  @Column(nullable = false)
  private Repetiria repetiria;

  @Column(columnDefinition = "TEXT")
  private String comentarios;

  @Column(name = "creado_en", insertable = false, updatable = false)
  private LocalDateTime creadoEn;
}
