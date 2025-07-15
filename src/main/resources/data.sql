-- Insertar categorías
INSERT INTO categorias (nombre, descripcion, fecha_creacion) VALUES
('Polos', 'Polos de diferentes estilos y materiales', NOW()),
('Poleras', 'Poleras casuales y deportivas', NOW()),
('Jeans', 'Pantalones de mezclilla para hombre y mujer', NOW()),
('Camisas', 'Camisas formales e informales', NOW()),
('Vestidos', 'Vestidos para diferentes ocasiones', NOW());

-- Insertar usuario administrador
INSERT INTO usuarios (nombre, email, password, rol, telefono, activo, fecha_registro) VALUES
('Administrador', 'admin@yorusito.com', '$2a$10$Y7JjXJQGgO5oZrZLnDLtZOE1qXvKfH.gKbGpqRoYvKkSPQsB7jlL2', 'ADMIN', '999888777', true, NOW()),
('Usuario Demo', 'usuario@demo.com', '$2a$10$Y7JjXJQGgO5oZrZLnDLtZOE1qXvKfH.gKbGpqRoYvKkSPQsB7jlL2', 'USER', '999888666', true, NOW());

-- Insertar productos
INSERT INTO productos (nombre, descripcion, precio, stock, imagen_url, categoria_id, activo, fecha_creacion, fecha_actualizacion) VALUES
-- Polos
('Polo Básico Blanco', 'Polo básico de algodón 100% en color blanco', 29.90, 50, 'https://example.com/polo-blanco.jpg', 1, true, NOW(), NOW()),
('Polo Rayas Azul', 'Polo con rayas horizontales en tonos azules', 35.90, 30, 'https://example.com/polo-rayas.jpg', 1, true, NOW(), NOW()),
('Polo Deportivo Negro', 'Polo deportivo con tecnología dry-fit', 45.90, 40, 'https://example.com/polo-deportivo.jpg', 1, true, NOW(), NOW()),

-- Poleras
('Polera Vintage Rock', 'Polera con estampado vintage de bandas de rock', 39.90, 25, 'https://example.com/polera-rock.jpg', 2, true, NOW(), NOW()),
('Polera Oversize Gris', 'Polera oversize en color gris melange', 32.90, 35, 'https://example.com/polera-gris.jpg', 2, true, NOW(), NOW()),
('Polera Estampada Flores', 'Polera con estampado floral para mujer', 42.90, 20, 'https://example.com/polera-flores.jpg', 2, true, NOW(), NOW()),

-- Jeans
('Jean Skinny Azul', 'Jean skinny de corte ajustado en azul clásico', 89.90, 45, 'https://example.com/jean-skinny.jpg', 3, true, NOW(), NOW()),
('Jean Mom Negro', 'Jean mom de tiro alto en color negro', 95.90, 30, 'https://example.com/jean-mom.jpg', 3, true, NOW(), NOW()),
('Jean Recto Clásico', 'Jean de corte recto tradicional', 79.90, 40, 'https://example.com/jean-recto.jpg', 3, true, NOW(), NOW()),

-- Camisas
('Camisa Formal Blanca', 'Camisa formal de algodón en color blanco', 65.90, 25, 'https://example.com/camisa-blanca.jpg', 4, true, NOW(), NOW()),
('Camisa Cuadros Azul', 'Camisa informal con estampado de cuadros', 55.90, 35, 'https://example.com/camisa-cuadros.jpg', 4, true, NOW(), NOW()),

-- Vestidos
('Vestido Casual Verano', 'Vestido ligero perfecto para el verano', 75.90, 20, 'https://example.com/vestido-verano.jpg', 5, true, NOW(), NOW()),
('Vestido Elegante Negro', 'Vestido elegante para ocasiones especiales', 125.90, 15, 'https://example.com/vestido-negro.jpg', 5, true, NOW(), NOW());
