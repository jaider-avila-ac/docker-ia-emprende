CREATE TABLE negocio_ia_cache (
  id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  negocio_id        BIGINT UNSIGNED NOT NULL,
  tipo              VARCHAR(20) NOT NULL,
  contexto_json     TEXT NOT NULL,
  actualizado_en    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_ia_cache_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id) ON DELETE CASCADE,
  UNIQUE KEY uq_ia_cache_negocio_tipo (negocio_id, tipo)
) ENGINE=InnoDB;
