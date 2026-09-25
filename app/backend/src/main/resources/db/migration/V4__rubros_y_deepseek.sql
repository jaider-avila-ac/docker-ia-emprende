ALTER TABLE negocios MODIFY rubro ENUM('Restaurante','Peluquería / estética','Consultorio / servicio profesional','Tienda física','Software como servicio (SaaS)','Aplicación móvil / software a medida','Agencia digital / marketing','Tienda en línea / e-commerce','Educación / cursos en línea','Consultor / freelancer independiente','Salud y bienestar / gimnasio','Turismo / hospedaje','Fotografía / producción audiovisual','Otro') NOT NULL DEFAULT 'Otro';

INSERT IGNORE INTO proveedores_ia (codigo, nombre) VALUES ('deepseek', 'DeepSeek');
