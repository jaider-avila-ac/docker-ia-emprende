SET NAMES utf8mb4;

INSERT INTO usuarios (correo, password_hash, nombre, apellido, fecha_nacimiento) VALUES
  ('marce@example.com', '$2a$10$demoDemoDemoDemoDemoDemoDemoDemoDemoDemoDemoDemoDemoDe', 'Marcela', 'Restrepo', '1990-04-12');

SET @usuario_id = LAST_INSERT_ID();

INSERT INTO negocios (
  usuario_id, nombre, rubro, ubicacion, vende_en_linea, descripcion,
  publico_objetivo, diferenciador, tagline, tono, colores,
  costos_fijos_mensuales, activo
) VALUES (
  @usuario_id, 'El Fogón de Marce', 'Restaurante', 'Montería, barrio El Recreo', FALSE,
  'Comida casera de la costa, para almorzar rico sin salir del barrio.',
  'Familias y trabajadores de oficinas cercanas que buscan un almuerzo casero a buen precio.',
  'Sazón de casa, porciones generosas y atención de toda la vida.',
  'Comida de casa, como la de tu mamá', 'Cercano, familiar, sin tecnicismos', 'Tonos tierra, cálidos',
  900000, TRUE
);

SET @negocio_id = LAST_INSERT_ID();

INSERT INTO negocio_redes_sociales (negocio_id, red_social, comentario) VALUES
  (@negocio_id, 'Instagram', 'La tengo pero casi no publico');

INSERT INTO negocio_pilares (negocio_id, pilar) VALUES
  (@negocio_id, 'Oferta'), (@negocio_id, 'Prueba social');

INSERT INTO ofertas (negocio_id, tipo, nombre, categoria, precio, destacar) VALUES
  (@negocio_id, 'Producto', 'Bandeja paisa', 'Plato fuerte', 22000, 'Porción grande, se sirve con chicharrón crocante.'),
  (@negocio_id, 'Producto', 'Sancocho de gallina', 'Plato del día', 18000, 'Receta de la casa, ideal recién servido.'),
  (@negocio_id, 'Producto', 'Menú ejecutivo (almuerzo)', 'Combo del día', 15000, 'Sopa + seco + jugo, cambia cada día.');

INSERT INTO competidores (negocio_id, nombre, canal, notas) VALUES
  (@negocio_id, 'Restaurante El Sazón', 'Instagram (~1.200 seguidores)', 'Publica el menú del día todas las mañanas.');

INSERT INTO foda_items (negocio_id, tipo, contenido, generado_por_ia) VALUES
  (@negocio_id, 'Fortaleza', 'Comida casera con buena sazón; clientes fieles que recomiendan de boca en boca.', TRUE),
  (@negocio_id, 'Oportunidad', 'Oficinas y negocios cerca que buscan opciones de almuerzo diario.', TRUE),
  (@negocio_id, 'Debilidad', 'No hay fotos buenas del local ni de los platos.', TRUE),
  (@negocio_id, 'Amenaza', 'Otros negocios de la zona empiezan a mostrarse mejor en redes.', TRUE);

INSERT INTO metas_smart (negocio_id, titulo, especifico, numero_meta, fecha_limite, medicion, pasos, generado_por_ia) VALUES
  (@negocio_id, 'Meta 1 · Constancia', 'Publicar 3 veces por semana durante 30 días sin fallar', '3 por semana', '2025-10-15', 'Publicaciones reales hechas cada semana', 'Fijar un día y hora fija para grabar/fotografiar', TRUE);

INSERT INTO iniciativas (negocio_id, titulo, meta_tipo, pilar, estado, impacto, confianza, esfuerzo, descripcion, ia_tip, generada_por_ia) VALUES
  (@negocio_id, 'Publicar el menú del día en fotos', 'Constancia', 'Oferta', 'En prueba', 5, 4, 2,
   'Publicar todas las mañanas una foto del menú del día.', 'Publícalo siempre a la misma hora.', TRUE);

SET @iniciativa_id = LAST_INSERT_ID();
SET @oferta_menu_id = (SELECT id FROM ofertas WHERE negocio_id = @negocio_id AND nombre = 'Menú ejecutivo (almuerzo)');

INSERT INTO iniciativa_ofertas (iniciativa_id, oferta_id) VALUES (@iniciativa_id, @oferta_menu_id);

INSERT INTO planes_semanales (negocio_id, anio, semana_numero, tiempo_disponible, publicaciones_sugeridas, historias_sugeridas, ventana_horaria) VALUES
  (@negocio_id, 2025, 34, 'Medio', 3, 4, '18:00–20:00');

SET @plan_id = LAST_INSERT_ID();

INSERT INTO plan_acciones (plan_semanal_id, dia_semana, descripcion) VALUES
  (@plan_id, 'Lunes', 'Post: menú del día en foto'),
  (@plan_id, 'Miércoles', 'Post: plato insignia bien presentado');

INSERT INTO resultados_semanales (negocio_id, anio, semana_numero, ingresos_aprox, clientes_aprox) VALUES
  (@negocio_id, 2025, 33, 1000000, 80),
  (@negocio_id, 2025, 34, 1000000, 78);

INSERT INTO evaluaciones (iniciativa_id, plan_semanal_id, se_logro, dificultad, repetiria, comentarios) VALUES
  (@iniciativa_id, @plan_id, 'Parcial', 'Baja', 'Sí', 'Faltó publicar el viernes.');

