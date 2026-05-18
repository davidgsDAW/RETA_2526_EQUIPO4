
CREATE DATABASE IF NOT EXISTS Taller_Informatica;

USE Taller_Informatica;


-- Roles de usuario
CREATE TABLE rol_usuario (
    id     INT          NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50)  NOT NULL,               -- 'ADMINISTRADOR', 'PROFESOR'
    PRIMARY KEY (id)
);

-- Estados posibles de un material
CREATE TABLE estado_elemento (
    id     INT         NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,                -- 'DISPONIBLE', 'PRESTADO', 'EN_REPARACION', 'BAJA'
    PRIMARY KEY (id)
);

-- Tipos de localización física
CREATE TABLE localizacion (
    id     INT         NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,                -- 'ARMARIO', 'CAJON', 'BALDA'
    PRIMARY KEY (id)
);


-- Tabla: categoria
CREATE TABLE categoria (
    id_categoria INT          NOT NULL AUTO_INCREMENT,
    nombre       VARCHAR(100) NOT NULL,
    descripcion  VARCHAR(255),
    PRIMARY KEY (id_categoria)
);

-- Tabla: ubicacion
CREATE TABLE ubicacion (
    id_ubicacion    INT          NOT NULL AUTO_INCREMENT,
    id_localizacion INT          NOT NULL,
    codigo_armario  VARCHAR(50),
    codigo_balda    VARCHAR(50),
    descripcion     VARCHAR(255),
    PRIMARY KEY (id_ubicacion),
    CONSTRAINT fk_ubic_localizacion
        FOREIGN KEY (id_localizacion) REFERENCES localizacion(id)
);

-- Tabla: usuario
CREATE TABLE usuario (
    id_usuario  INT          NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL,
    contrasena  VARCHAR(255) NOT NULL,        
    id_rol      INT          NOT NULL,
    PRIMARY KEY (id_usuario),
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol) REFERENCES rol_usuario(id)
);

-- Tabla: material  (entidad central del inventario)
CREATE TABLE material (
    id_material   INT          NOT NULL AUTO_INCREMENT,
    nombre        VARCHAR(150) NOT NULL,
    descripcion   VARCHAR(500),
    cantidad      INT          NOT NULL DEFAULT 1,
    fecha_alta    DATE         NOT NULL,
    observaciones VARCHAR(500),
    id_categoria  INT          NOT NULL,
    id_estado     INT          NOT NULL,
    id_ubicacion  INT          NOT NULL,
    PRIMARY KEY (id_material),
    -- Restricción: la cantidad no puede ser negativa
    CONSTRAINT chk_cantidad_positiva CHECK (cantidad >= 0),
    CONSTRAINT fk_mat_categoria
        FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
    CONSTRAINT fk_mat_estado
        FOREIGN KEY (id_estado)    REFERENCES estado_elemento(id),
    CONSTRAINT fk_mat_ubicacion
        FOREIGN KEY (id_ubicacion) REFERENCES ubicacion(id_ubicacion)
);


-- Tabla: prestamo
CREATE TABLE prestamo (
    id_prestamo      INT  NOT NULL AUTO_INCREMENT,
    id_material      INT  NOT NULL,
    id_usuario       INT  NOT NULL,
    fecha_prestamo   DATE NOT NULL,
    fecha_devolucion DATE,                      -- NULL si aún no devuelto
    PRIMARY KEY (id_prestamo),
    CONSTRAINT fk_pres_material
        FOREIGN KEY (id_material) REFERENCES material(id_material),
    CONSTRAINT fk_pres_usuario
        FOREIGN KEY (id_usuario)  REFERENCES usuario(id_usuario)
);

-- Tabla: historial_movimiento
CREATE TABLE historial_movimiento (
    id_movimiento   INT          NOT NULL AUTO_INCREMENT,
    id_material     INT          NOT NULL,
    id_usuario      INT          NOT NULL,
    tipo_movimiento VARCHAR(50)  NOT NULL,      -- 'ALTA', 'BAJA', 'PRESTAMO', 'DEVOLUCION', 'REPARACION'
    fecha           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones   VARCHAR(500),
    PRIMARY KEY (id_movimiento),
    CONSTRAINT fk_mov_material
        FOREIGN KEY (id_material) REFERENCES material(id_material),
    CONSTRAINT fk_mov_usuario
        FOREIGN KEY (id_usuario)  REFERENCES usuario(id_usuario)
);


INSERT INTO rol_usuario (nombre) VALUES
    ('ADMINISTRADOR'),
    ('PROFESOR');

INSERT INTO estado_elemento (nombre) VALUES
    ('DISPONIBLE'),
    ('PRESTADO'),
    ('EN_REPARACION'),
    ('BAJA');

INSERT INTO localizacion (nombre) VALUES
    ('ARMARIO'),
    ('CAJON'),
    ('BALDA');

INSERT INTO categoria (nombre, descripcion) VALUES
    ('Componentes de red',  'Switches, routers, cables, tarjetas de red, etc.'),
    ('Hardware',            'Placas base, procesadores, RAM, discos duros, SSD, etc.'),
    ('Herramientas',        'Destornilladores, pulseras antiestáticas, polímetros, etc.'),
    ('Material fungible',   'Tornillos, bridas, pasta térmica, etc.'),
    ('Equipos completos',   'PCs, portátiles, Raspberry Pi, etc.');

INSERT INTO usuario (nombre,contrasena,id_rol)VALUES
	("david","david",1),
    ("alejandro","alejandro",1),
    ("juanca","juanca",1),
    ("angel","angel",1),
    ("mario","mario",1);

CREATE INDEX idx_material_categoria ON material(id_categoria);
CREATE INDEX idx_material_estado    ON material(id_estado);
CREATE INDEX idx_material_ubicacion ON material(id_ubicacion);
CREATE INDEX idx_prestamo_usuario   ON prestamo(id_usuario);
CREATE INDEX idx_prestamo_material  ON prestamo(id_material);
CREATE INDEX idx_movimiento_mat     ON historial_movimiento(id_material);
CREATE INDEX idx_movimiento_usr     ON historial_movimiento(id_usuario);


DROP TRIGGER IF EXISTS trg_alta_material;
DROP TRIGGER IF EXISTS trg_cambio_estado_material;
DROP TRIGGER IF EXISTS trg_corregir_cantidad;
DROP TRIGGER IF EXISTS trg_stock_agotado;
DROP TRIGGER IF EXISTS trg_nuevo_prestamo;
DROP TRIGGER IF EXISTS trg_devolucion_prestamo;



USE taller_informatica;
DELIMITER $$

--  TRIGGER 1: Alta de material
--  Registra automáticamente en historial cuando se añade un nuevo elemento al inventario.


CREATE TRIGGER trg_alta_material
AFTER INSERT ON material
FOR EACH ROW
BEGIN
    INSERT INTO historial_movimiento (
        id_material,
        id_usuario,
        tipo_movimiento,
        fecha,
        observaciones
    )
    VALUES (
        NEW.id_material,
        1,
        'ALTA',
        NOW(),
        CONCAT('Alta automática del material: ', NEW.nombre)
    );
END$$



--  TRIGGER 2: Cambio de estado del material



CREATE TRIGGER trg_cambio_estado_material
AFTER UPDATE ON material
FOR EACH ROW
BEGIN
    -- Solo actúa si el estado cambió Y no es un cambio de préstamo/devolución
    IF OLD.id_estado <> NEW.id_estado
       AND NOT (OLD.id_estado = 1 AND NEW.id_estado = 2)
       AND NOT (OLD.id_estado = 2 AND NEW.id_estado = 1)
    THEN
        INSERT INTO historial_movimiento (
            id_material,
            id_usuario,
            tipo_movimiento,
            fecha,
            observaciones
        )
        VALUES (
            NEW.id_material,
            1,
            'CAMBIO_ESTADO',
            NOW(),
            CONCAT(
                'Estado cambiado en "', NEW.nombre, '": ',
                OLD.id_estado, ' → ', NEW.id_estado
            )
        );
    END IF;
END$$



--  TRIGGER 3a: Corregir cantidad negativa 
--  Este trigger SOLO corrige la cantidad a 0 si alguien intenta poner un valor negativo


CREATE TRIGGER trg_corregir_cantidad
BEFORE UPDATE ON material
FOR EACH ROW
BEGIN
    IF NEW.cantidad < 0 THEN
        SET NEW.cantidad = 0;
    END IF;
END$$



--  TRIGGER 3b: Avisar cuando el stock llega a 0

CREATE TRIGGER trg_stock_agotado
AFTER UPDATE ON material
FOR EACH ROW
BEGIN
    IF NEW.cantidad = 0 AND OLD.cantidad > 0 THEN
        INSERT INTO historial_movimiento (
            id_material,
            id_usuario,
            tipo_movimiento,
            fecha,
            observaciones
        )
        VALUES (
            NEW.id_material,
            1,
            'STOCK_AGOTADO',
            NOW(),
            CONCAT('AVISO: el material "', NEW.nombre, '" ha llegado a cantidad 0.')
        );
    END IF;
END$$



--  TRIGGER 4: Nuevo préstamo
--  Cambia el estado del material a PRESTADO (id=2) y lo registra
--  en historial. 


CREATE TRIGGER trg_nuevo_prestamo
AFTER INSERT ON prestamo
FOR EACH ROW
BEGIN
    UPDATE material
    SET id_estado = 2
    WHERE id_material = NEW.id_material;

    INSERT INTO historial_movimiento (
        id_material,
        id_usuario,
        tipo_movimiento,
        fecha,
        observaciones
    )
    VALUES (
        NEW.id_material,
        NEW.id_usuario,
        'PRESTAMO',
        NOW(),
        CONCAT(
            'Material prestado al usuario id: ', NEW.id_usuario,
            '. Devolución prevista: ',
            IFNULL(NEW.fecha_devolucion, 'Sin fecha')
        )
    );
END$$



--  TRIGGER 5: Devolución de préstamo
--  Cuando se rellena fecha_devolucion, vuelve a poner el material
--  como DISPONIBLE y lo registra.


CREATE TRIGGER trg_devolucion_prestamo
AFTER UPDATE ON prestamo
FOR EACH ROW
BEGIN
    IF OLD.fecha_devolucion IS NULL AND NEW.fecha_devolucion IS NOT NULL THEN

        UPDATE material
        SET id_estado = 1
        WHERE id_material = NEW.id_material;

        INSERT INTO historial_movimiento (
            id_material,
            id_usuario,
            tipo_movimiento,
            fecha,
            observaciones
        )
        VALUES (
            NEW.id_material,
            NEW.id_usuario,
            'DEVOLUCION',
            NOW(),
            CONCAT(
                'Material devuelto por usuario id: ', NEW.id_usuario,
                '. Fecha de devolución: ', NEW.fecha_devolucion
            )
        );

    END IF;
END$$

DELIMITER ;
