package com.iaemprender.backend.plan.modelo;

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
@Table(name = "planes_semanales")
@Getter
@Setter
@NoArgsConstructor
public class PlanSemanal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "negocio_id", nullable = false)
  private Long negocioId;

  @Column(nullable = false)
  private Integer anio;

  @Column(name = "semana_numero", nullable = false)
  private Integer semanaNumero;

  @Column(name = "tiempo_disponible", nullable = false)
  private TiempoDisponible tiempoDisponible;

  @Column(name = "publicaciones_sugeridas")
  private Integer publicacionesSugeridas;

  @Column(name = "historias_sugeridas")
  private Integer historiasSugeridas;

  @Column(name = "ventana_horaria", length = 50)
  private String ventanaHoraria;

  @Column(name = "creado_en", insertable = false, updatable = false)
  private LocalDateTime creadoEn;
}
