package com.iaemprender.backend.ofertas.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ofertas")
@Getter
@Setter
@NoArgsConstructor
public class Oferta {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "negocio_id", nullable = false)
  private Long negocioId;

  @Column(nullable = false)
  private TipoOferta tipo;

  @Column(nullable = false, length = 150)
  private String nombre;

  @Column(length = 100)
  private String categoria;

  @Column(precision = 12, scale = 2)
  private BigDecimal precio;

  @Column(columnDefinition = "TEXT")
  private String destacar;

  @Column(name = "creado_en", insertable = false, updatable = false)
  private LocalDateTime creadoEn;
}
