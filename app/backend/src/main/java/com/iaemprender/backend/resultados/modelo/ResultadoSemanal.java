package com.iaemprender.backend.resultados.modelo;

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
@Table(name = "resultados_semanales")
@Getter
@Setter
@NoArgsConstructor
public class ResultadoSemanal {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "negocio_id", nullable = false)
  private Long negocioId;

  @Column(nullable = false)
  private Integer anio;

  @Column(name = "semana_numero", nullable = false)
  private Integer semanaNumero;

  @Column(name = "ingresos_aprox", nullable = false, precision = 12, scale = 2)
  private BigDecimal ingresosAprox;

  @Column(name = "clientes_aprox")
  private Integer clientesAprox;

  @Column(name = "creado_en", insertable = false, updatable = false)
  private LocalDateTime creadoEn;
}
