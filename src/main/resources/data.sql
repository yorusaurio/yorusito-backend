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

-- Insertar perfiles de usuario
INSERT INTO user_profiles (usuario_id, direccion_envio, ciudad, codigo_postal, pais, preferencias_notificaciones, acepta_marketing, fecha_actualizacion) VALUES
(1, 'Av. Principal 123', 'Lima', '15001', 'Perú', true, false, NOW()),
(2, 'Jr. Secundario 456', 'Cusco', '08001', 'Perú', true, true, NOW());

-- Insertar algunas reviews de ejemplo
INSERT INTO reviews (producto_id, usuario_id, comment, rating, fecha_creacion, fecha_actualizacion, activo) VALUES
(1, 2, 'Excelente calidad del polo, muy cómodo y la tela es suave. Recomendado!', 5, NOW(), NOW(), true),
(2, 2, 'Buen diseño pero la talla viene un poco grande. Considerarlo al comprar.', 4, NOW(), NOW(), true),
(4, 2, 'Me encanta el estampado vintage, muy original y de buena calidad.', 5, NOW(), NOW(), true),
(7, 2, 'El jean se ve bien pero después de varios lavados se decolora un poco.', 3, NOW(), NOW(), true),
(10, 2, 'Perfecta para la oficina, tela de excelente calidad y buen corte.', 5, NOW(), NOW(), true);

-- Insertar movimientos de inventario iniciales
INSERT INTO inventory_movements (producto_id, tipo, cantidad, stock_anterior, stock_actual, motivo, fecha_movimiento, usuario_responsable) VALUES
(1, 'ENTRADA', 50, 0, 50, 'Stock inicial', NOW(), 'admin@yorusito.com'),
(2, 'ENTRADA', 30, 0, 30, 'Stock inicial', NOW(), 'admin@yorusito.com'),
(3, 'ENTRADA', 40, 0, 40, 'Stock inicial', NOW(), 'admin@yorusito.com'),
(4, 'ENTRADA', 25, 0, 25, 'Stock inicial', NOW(), 'admin@yorusito.com'),
(5, 'ENTRADA', 35, 0, 35, 'Stock inicial', NOW(), 'admin@yorusito.com'),
(6, 'ENTRADA', 20, 0, 20, 'Stock inicial', NOW(), 'admin@yorusito.com'),
(7, 'ENTRADA', 45, 0, 45, 'Stock inicial', NOW(), 'admin@yorusito.com'),
(8, 'ENTRADA', 30, 0, 30, 'Stock inicial', NOW(), 'admin@yorusito.com'),
(9, 'ENTRADA', 40, 0, 40, 'Stock inicial', NOW(), 'admin@yorusito.com'),
(10, 'ENTRADA', 25, 0, 25, 'Stock inicial', NOW(), 'admin@yorusito.com'),
(11, 'ENTRADA', 35, 0, 35, 'Stock inicial', NOW(), 'admin@yorusito.com'),
(12, 'ENTRADA', 20, 0, 20, 'Stock inicial', NOW(), 'admin@yorusito.com'),
(13, 'ENTRADA', 15, 0, 15, 'Stock inicial', NOW(), 'admin@yorusito.com');

-- Insertar algunas alertas de stock bajo para productos con poco stock
INSERT INTO inventory_alerts (producto_id, stock_minimo, stock_actual, mensaje, fecha_alerta, activa, notificada) VALUES
(13, 10, 15, 'Stock bajo: 15 unidades restantes', NOW(), true, false),
(6, 10, 20, 'Stock bajo: 20 unidades restantes', NOW(), true, false);

-- Insertar notificaciones de ejemplo
INSERT INTO notifications (usuario_id, titulo, mensaje, tipo, leida, enviada, fecha_creacion, fecha_envio, referencia_id, referencia_tipo) VALUES
(2, 'Bienvenido a Yorusito', 'Gracias por registrarte en nuestra tienda online. ¡Esperamos que encuentres productos de tu agrado!', 'SYSTEM', false, true, NOW(), NOW(), null, null),
(1, 'Alerta de Stock Bajo', 'El producto "Vestido Elegante Negro" tiene stock bajo (15 unidades)', 'STOCK_ALERT', false, true, NOW(), NOW(), 13, 'PRODUCT');

-- Insertar pedidos de ejemplo
INSERT INTO orders (usuario_id, numero_orden, estado, total, subtotal, impuestos, costo_envio, direccion_envio, ciudad_envio, codigo_postal_envio, pais_envio, telefono_contacto, fecha_creacion, fecha_actualizacion) VALUES
(2, 'ORD-1700000001', 'ENTREGADO', 75.80, 69.80, 0.00, 6.00, 'Jr. Secundario 456', 'Cusco', '08001', 'Perú', '999888666', DATEADD('DAY', -7, NOW()), DATEADD('DAY', -5, NOW())),
(2, 'ORD-1700000002', 'ENVIADO', 125.90, 125.90, 0.00, 0.00, 'Jr. Secundario 456', 'Cusco', '08001', 'Perú', '999888666', DATEADD('DAY', -2, NOW()), DATEADD('DAY', -1, NOW())),
(2, 'ORD-1700000003', 'PENDIENTE', 65.80, 59.80, 0.00, 6.00, 'Jr. Secundario 456', 'Cusco', '08001', 'Perú', '999888666', NOW(), NOW());

-- Insertar items de pedidos
INSERT INTO order_items (order_id, producto_id, cantidad, precio_unitario, precio_total, nombre_producto, descripcion_producto) VALUES
-- Pedido 1
(1, 1, 2, 29.90, 59.80, 'Polo Básico Blanco', 'Polo básico de algodón 100% en color blanco'),
(1, 5, 1, 32.90, 32.90, 'Polera Oversize Gris', 'Polera oversize en color gris melange'),
-- Pedido 2
(2, 13, 1, 125.90, 125.90, 'Vestido Elegante Negro', 'Vestido elegante para ocasiones especiales'),
-- Pedido 3
(3, 10, 1, 65.90, 65.90, 'Camisa Formal Blanca', 'Camisa formal de algodón en color blanco');
