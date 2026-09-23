SET NAMES utf8mb4;
SET time_zone = '+00:00';

CREATE TABLE usuarios (
  id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  correo            VARCHAR(255) NOT NULL,
  password_hash     VARCHAR(255) NOT NULL,
  nombre            VARCHAR(100) NOT NULL,
  apellido          VARCHAR(100) NOT NULL,
  fecha_nacimiento  DATE NOT NULL,
  creado_en         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actualizado_en    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uq_usuarios_correo UNIQUE (correo)
) ENGINE=InnoDB;

CREATE TABLE negocios (
  id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  usuario_id              BIGINT UNSIGNED NOT NULL,
  nombre                  VARCHAR(150) NOT NULL,
  rubro                   ENUM('Restaurante','Peluquería / estética','Consultorio / servicio profesional','Tienda física','Otro') NOT NULL DEFAULT 'Otro',
  ubicacion               VARCHAR(255) NULL,
  vende_en_linea          BOOLEAN NOT NULL DEFAULT FALSE,
  cobertura_envio         VARCHAR(255) NULL,
  descripcion             TEXT NULL,
  publico_objetivo        TEXT NULL,
  diferenciador           TEXT NULL,
  tagline                 VARCHAR(255) NULL,
  tono                    VARCHAR(255) NULL,
  colores                 VARCHAR(255) NULL,
  referencias_estilo      VARCHAR(255) NULL,
  costos_fijos_mensuales  DECIMAL(12,2) NULL,
  activo                  BOOLEAN NOT NULL DEFAULT FALSE,
  creado_en               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actualizado_en          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_negocios_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  INDEX idx_negocios_usuario (usuario_id)
) ENGINE=InnoDB;

DELIMITER $$
CREATE TRIGGER trg_negocios_maximo_3
BEFORE INSERT ON negocios
FOR EACH ROW
BEGIN
  DECLARE total INT;
  SELECT COUNT(*) INTO total FROM negocios WHERE usuario_id = NEW.usuario_id;
  IF total >= 3 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Un usuario no puede tener más de 3 negocios.';
  END IF;
END$$
DELIMITER ;

CREATE TABLE negocio_redes_sociales (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  negocio_id    BIGINT UNSIGNED NOT NULL,
  red_social    ENUM('Instagram','Facebook','TikTok','WhatsApp Business') NOT NULL,
  comentario    VARCHAR(255) NULL,
  CONSTRAINT fk_redes_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id) ON DELETE CASCADE,
  CONSTRAINT uq_negocio_red UNIQUE (negocio_id, red_social)
) ENGINE=InnoDB;

CREATE TABLE negocio_pilares (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  negocio_id    BIGINT UNSIGNED NOT NULL,
  pilar         ENUM('Educativo','Oferta','Prueba social','Interacción','Servicio') NOT NULL,
  CONSTRAINT fk_pilares_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id) ON DELETE CASCADE,
  CONSTRAINT uq_negocio_pilar UNIQUE (negocio_id, pilar)
) ENGINE=InnoDB;

CREATE TABLE ofertas (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  negocio_id    BIGINT UNSIGNED NOT NULL,
  tipo          ENUM('Producto','Servicio') NOT NULL,
  nombre        VARCHAR(150) NOT NULL,
  categoria     VARCHAR(100) NULL,
  precio        DECIMAL(12,2) NULL,
  destacar      TEXT NULL,
  creado_en     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ofertas_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id) ON DELETE CASCADE,
  INDEX idx_ofertas_negocio (negocio_id)
) ENGINE=InnoDB;

CREATE TABLE competidores (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  negocio_id    BIGINT UNSIGNED NOT NULL,
  nombre        VARCHAR(150) NOT NULL,
  canal         VARCHAR(150) NULL,
  notas         TEXT NULL,
  creado_en     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_competidores_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE foda_items (
  id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  negocio_id        BIGINT UNSIGNED NOT NULL,
  tipo              ENUM('Fortaleza','Oportunidad','Debilidad','Amenaza') NOT NULL,
  contenido         TEXT NOT NULL,
  generado_por_ia   BOOLEAN NOT NULL DEFAULT FALSE,
  creado_en         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_foda_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id) ON DELETE CASCADE,
  INDEX idx_foda_negocio_tipo (negocio_id, tipo)
) ENGINE=InnoDB;

CREATE TABLE metas_smart (
  id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  negocio_id        BIGINT UNSIGNED NOT NULL,
  titulo            VARCHAR(255) NOT NULL,
  especifico        TEXT NULL,
  numero_meta       VARCHAR(100) NULL,
  fecha_limite      DATE NULL,
  medicion          TEXT NULL,
  pasos             TEXT NULL,
  generado_por_ia   BOOLEAN NOT NULL DEFAULT FALSE,
  creado_en         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_smart_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE negocio_ia_cache (
  id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  negocio_id        BIGINT UNSIGNED NOT NULL,
  tipo              VARCHAR(20) NOT NULL,
  contexto_json     TEXT NOT NULL,
  actualizado_en    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_ia_cache_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id) ON DELETE CASCADE,
  UNIQUE KEY uq_ia_cache_negocio_tipo (negocio_id, tipo)
) ENGINE=InnoDB;

CREATE TABLE iniciativas (
  id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  negocio_id        BIGINT UNSIGNED NOT NULL,
  titulo            VARCHAR(255) NOT NULL,
  meta_tipo         VARCHAR(100) NULL,
  pilar             VARCHAR(100) NULL,
  estado            ENUM('Propuesta','En prueba','Aprobada','Descartada') NOT NULL DEFAULT 'Propuesta',
  impacto           INT UNSIGNED NOT NULL,
  confianza         INT UNSIGNED NOT NULL,
  esfuerzo          INT UNSIGNED NOT NULL,
  descripcion       TEXT NULL,
  notas             TEXT NULL,
  generada_por_ia   BOOLEAN NOT NULL DEFAULT FALSE,
  ia_tip            TEXT NULL,
  creado_en         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actualizado_en    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_iniciativas_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id) ON DELETE CASCADE,
  CONSTRAINT chk_impacto CHECK (impacto BETWEEN 1 AND 5),
  CONSTRAINT chk_confianza CHECK (confianza BETWEEN 1 AND 5),
  CONSTRAINT chk_esfuerzo CHECK (esfuerzo BETWEEN 1 AND 5),
  INDEX idx_iniciativas_negocio (negocio_id)
) ENGINE=InnoDB;

CREATE TABLE iniciativa_ofertas (
  iniciativa_id   BIGINT UNSIGNED NOT NULL,
  oferta_id       BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (iniciativa_id, oferta_id),
  CONSTRAINT fk_io_iniciativa FOREIGN KEY (iniciativa_id) REFERENCES iniciativas(id) ON DELETE CASCADE,
  CONSTRAINT fk_io_oferta FOREIGN KEY (oferta_id) REFERENCES ofertas(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE planes_semanales (
  id                        BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  negocio_id                BIGINT UNSIGNED NOT NULL,
  anio                      INT UNSIGNED NOT NULL,
  semana_numero             INT UNSIGNED NOT NULL,
  tiempo_disponible         ENUM('Poco','Medio','Bastante') NOT NULL,
  publicaciones_sugeridas   INT UNSIGNED NULL,
  historias_sugeridas       INT UNSIGNED NULL,
  ventana_horaria           VARCHAR(50) NULL,
  creado_en                 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_plan_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id) ON DELETE CASCADE,
  CONSTRAINT uq_plan_semana UNIQUE (negocio_id, anio, semana_numero)
) ENGINE=InnoDB;

CREATE TABLE plan_acciones (
  id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  plan_semanal_id   BIGINT UNSIGNED NOT NULL,
  dia_semana        ENUM('Lunes','Martes','Miércoles','Jueves','Viernes','Sábado','Domingo') NOT NULL,
  descripcion       VARCHAR(255) NOT NULL,
  CONSTRAINT fk_accion_plan FOREIGN KEY (plan_semanal_id) REFERENCES planes_semanales(id) ON DELETE CASCADE,
  INDEX idx_acciones_plan (plan_semanal_id)
) ENGINE=InnoDB;

CREATE TABLE evaluaciones (
  id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  iniciativa_id     BIGINT UNSIGNED NOT NULL,
  plan_semanal_id   BIGINT UNSIGNED NULL,
  se_logro          ENUM('Sí','Parcial','No') NOT NULL,
  dificultad        ENUM('Baja','Media','Alta') NOT NULL,
  repetiria         ENUM('Sí','Con cambios','No') NOT NULL,
  comentarios       TEXT NULL,
  creado_en         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_eval_iniciativa FOREIGN KEY (iniciativa_id) REFERENCES iniciativas(id) ON DELETE CASCADE,
  CONSTRAINT fk_eval_plan FOREIGN KEY (plan_semanal_id) REFERENCES planes_semanales(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE resultados_semanales (
  id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  negocio_id        BIGINT UNSIGNED NOT NULL,
  anio              INT UNSIGNED NOT NULL,
  semana_numero     INT UNSIGNED NOT NULL,
  ingresos_aprox    DECIMAL(12,2) NOT NULL,
  clientes_aprox    INT UNSIGNED NULL,
  creado_en         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_resultados_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id) ON DELETE CASCADE,
  CONSTRAINT uq_resultado_semana UNIQUE (negocio_id, anio, semana_numero)
) ENGINE=InnoDB;

CREATE TABLE microlecciones_guardadas (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  negocio_id    BIGINT UNSIGNED NOT NULL,
  titulo        VARCHAR(255) NOT NULL,
  texto         TEXT NOT NULL,
  guardado_en   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_microlecciones_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE proveedores_ia (
  id        INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  codigo    VARCHAR(30) NOT NULL,
  nombre    VARCHAR(50) NOT NULL,
  activo    BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT uq_proveedor_codigo UNIQUE (codigo)
) ENGINE=InnoDB;

INSERT INTO proveedores_ia (codigo, nombre) VALUES
  ('openai', 'OpenAI'),
  ('gemini', 'Google Gemini');

CREATE TABLE usuario_api_keys (
  id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  usuario_id              BIGINT UNSIGNED NOT NULL,
  proveedor_id            INT UNSIGNED NOT NULL,
  clave_cifrada           VARBINARY(1024) NOT NULL,
  iv                      VARBINARY(16) NOT NULL,
  clave_ultimos4          CHAR(4) NOT NULL,
  estado                  ENUM('activa','invalida','desactivada') NOT NULL DEFAULT 'activa',
  tokens_usados_periodo   BIGINT UNSIGNED NOT NULL DEFAULT 0,
  periodo_inicio          DATE NOT NULL,
  ultima_verificacion     TIMESTAMP NULL,
  creado_en               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actualizado_en          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_apikey_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  CONSTRAINT fk_apikey_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores_ia(id),
  CONSTRAINT uq_usuario_proveedor UNIQUE (usuario_id, proveedor_id)
) ENGINE=InnoDB;

CREATE TABLE refresh_tokens (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  usuario_id    BIGINT UNSIGNED NOT NULL,
  token_hash    CHAR(64) NOT NULL,
  expira_en     DATETIME NOT NULL,
  revocado_en   DATETIME NULL,
  creado_en     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_refresh_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  CONSTRAINT uq_refresh_hash UNIQUE (token_hash)
) ENGINE=InnoDB;
