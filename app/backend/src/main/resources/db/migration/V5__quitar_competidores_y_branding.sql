DELETE FROM negocio_ia_cache;

DROP TABLE competidores;

ALTER TABLE negocios
  DROP COLUMN tagline,
  DROP COLUMN colores,
  DROP COLUMN referencias_estilo;
