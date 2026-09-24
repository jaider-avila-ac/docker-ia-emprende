package com.iaemprender.backend.iniciativas.modelo;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "iniciativas")
@Getter
@Setter
@NoArgsConstructor
public class Iniciativa {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "negocio_id", nullable = false)
  private Long negocioId;

  @Column(nullable = false, length = 255)
  private String titulo;

  @Column(name = "meta_tipo", length = 100)
  private String metaTipo;

  @Column(length = 100)
  private String pilar;

  @Column(nullable = false)
  private EstadoIniciativa estado;

  @Column(nullable = false)
  private Integer impacto;

  @Column(nullable = false)
  private Integer confianza;

  @Column(nullable = false)
  private Integer esfuerzo;

  @Column(columnDefinition = "TEXT")
  private String descripcion;

  @Column(columnDefinition = "TEXT")
  private String notas;

  @Column(name = "generada_por_ia", nullable = false)
  private boolean generadaPorIa;

  @Column(name = "ia_tip", columnDefinition = "TEXT")
  private String iaTip;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "iniciativa_ofertas", joinColumns = @JoinColumn(name = "iniciativa_id"))
  @Column(name = "oferta_id")
  private Set<Long> ofertaIds = new HashSet<>();

  @Column(name = "creado_en", insertable = false, updatable = false)
  private LocalDateTime creadoEn;

  @Column(name = "actualizado_en", insertable = false, updatable = false)
  private LocalDateTime actualizadoEn;
}
