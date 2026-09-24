package com.iaemprender.backend.apikeys.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario_api_keys")
@Getter
@Setter
@NoArgsConstructor
public class UsuarioApiKey {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "usuario_id", nullable = false)
  private Long usuarioId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "proveedor_id", nullable = false)
  private ProveedorIA proveedor;

  @Column(name = "clave_cifrada", nullable = false)
  private byte[] claveCifrada;

  @Column(nullable = false)
  private byte[] iv;

  @Column(name = "clave_ultimos4", nullable = false, length = 4)
  private String claveUltimos4;

  @Column(nullable = false)
  private EstadoApiKey estado;

  @Column(name = "tokens_usados_periodo", nullable = false)
  private long tokensUsadosPeriodo;

  @Column(name = "periodo_inicio", nullable = false)
  private LocalDate periodoInicio;

  @Column(name = "ultima_verificacion")
  private LocalDateTime ultimaVerificacion;

  @Column(name = "creado_en", insertable = false, updatable = false)
  private LocalDateTime creadoEn;

  @Column(name = "actualizado_en", insertable = false, updatable = false)
  private LocalDateTime actualizadoEn;
}
