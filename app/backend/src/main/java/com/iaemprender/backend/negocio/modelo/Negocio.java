package com.iaemprender.backend.negocio.modelo;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "negocios")
@Getter
@Setter
@NoArgsConstructor
public class Negocio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "usuario_id", nullable = false)
  private Long usuarioId;

  @Column(nullable = false, length = 150)
  private String nombre;

  @Column(nullable = false)
  private Rubro rubro;

  @Column(length = 255)
  private String ubicacion;

  @Column(name = "vende_en_linea", nullable = false)
  private boolean vendeEnLinea;

  @Column(name = "cobertura_envio", length = 255)
  private String coberturaEnvio;

  @Column(columnDefinition = "TEXT")
  private String descripcion;

  @Column(name = "publico_objetivo", columnDefinition = "TEXT")
  private String publicoObjetivo;

  @Column(columnDefinition = "TEXT")
  private String diferenciador;

  @Column(length = 255)
  private String tagline;

  @Column(length = 255)
  private String tono;

  @Column(length = 255)
  private String colores;

  @Column(name = "referencias_estilo", length = 255)
  private String referenciasEstilo;

  @Column(name = "costos_fijos_mensuales", precision = 12, scale = 2)
  private BigDecimal costosFijosMensuales;

  @Column(nullable = false)
  private boolean activo;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "negocio_redes_sociales", joinColumns = @JoinColumn(name = "negocio_id"))
  @Column(name = "red_social")
  private Set<RedSocial> redesActivas = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "negocio_pilares", joinColumns = @JoinColumn(name = "negocio_id"))
  @Column(name = "pilar")
  private Set<Pilar> pilares = new HashSet<>();

  @Column(name = "creado_en", insertable = false, updatable = false)
  private LocalDateTime creadoEn;

  @Column(name = "actualizado_en", insertable = false, updatable = false)
  private LocalDateTime actualizadoEn;
}
