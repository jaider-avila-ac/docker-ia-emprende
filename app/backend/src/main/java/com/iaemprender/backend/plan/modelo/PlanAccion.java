package com.iaemprender.backend.plan.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plan_acciones")
@Getter
@Setter
@NoArgsConstructor
public class PlanAccion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plan_semanal_id", nullable = false)
  private PlanSemanal planSemanal;

  @Column(name = "dia_semana", nullable = false)
  private DiaSemana diaSemana;

  @Column(nullable = false, length = 255)
  private String descripcion;
}
