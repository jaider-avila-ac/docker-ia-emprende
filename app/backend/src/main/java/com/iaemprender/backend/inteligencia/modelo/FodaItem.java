package com.iaemprender.backend.inteligencia.modelo;

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
@Table(name = "foda_items")
@Getter
@Setter
@NoArgsConstructor
public class FodaItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "negocio_id", nullable = false)
  private Long negocioId;

  @Column(nullable = false)
  private TipoFoda tipo;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String contenido;

  @Column(name = "generado_por_ia", nullable = false)
  private boolean generadoPorIa;

  @Column(name = "creado_en", insertable = false, updatable = false)
  private LocalDateTime creadoEn;
}
