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
@Table(name = "negocio_ia_cache")
@Getter
@Setter
@NoArgsConstructor
public class NegocioIaCache {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "negocio_id", nullable = false)
  private Long negocioId;

  @Column(nullable = false, length = 20)
  private String tipo;

  @Column(name = "contexto_json", nullable = false, columnDefinition = "TEXT")
  private String contextoJson;

  @Column(name = "actualizado_en", insertable = false, updatable = false)
  private LocalDateTime actualizadoEn;
}
