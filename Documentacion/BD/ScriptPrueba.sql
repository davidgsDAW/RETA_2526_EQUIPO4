

-- Roles de usuario 
CREATE TABLE rol_usuario (
    id   INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre ENUM('ADMINISTRADOR', 'PROFESOR') NOT NULL DEFAULT "PROFESOR"
);

-- Estados posibles de un material
CREATE TABLE estado_elemento (
    id     INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre ENUM('DISPONIBLE', 'PRESTADO', 'EN_REPARACION', 'BAJA') NOT NULL DEFAULT "DISPONIBLE"
);

-- Tipos de localización física 
CREATE TABLE localizacion (
    id     INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre ENUM('ARMARIO', 'CAJON', 'BALDA') NOT NULL DEFAULT "ARMARIO"
);


-- Tabla: categoria
CREATE TABLE categoria (
    id_categoria INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

-- Tabla: ubicacion
CREATE TABLE ubicacion (
    id_ubicacion  INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_localizacion INT NOT NULL,
    codigo_armario VARCHAR(50),
    codigo_balda   VARCHAR(50),
    descripcion    VARCHAR(255),
    CONSTRAINT fk_ubic_localizacion
        FOREIGN KEY (id_localizacion) REFERENCES localizacion(id)
);

-- Tabla: usuario
CREATE TABLE usuario (
    id_usuario  INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    contrasena  VARCHAR(255) NOT NULL,          
    id_rol      INT NOT NULL,
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol) REFERENCES rol_usuario(id)
);

-- Tabla: material  (entidad principal del inventario)
CREATE TABLE material (
    id_material  INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(150) NOT NULL,
    descripcion  VARCHAR(500),
    cantidad     INT NOT NULL DEFAULT 1,
    fecha_alta   DATE NOT NULL,
    observaciones VARCHAR(500),
    id_categoria  INT NOT NULL,
    id_estado     INT NOT NULL,
    id_ubicacion  INT NOT NULL,
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
    fecha_devolucion DATE,                     
    PRIMARY KEY (id_prestamo),
    CONSTRAINT fk_pres_material
        FOREIGN KEY (id_material)  REFERENCES material(id_material),
    CONSTRAINT fk_pres_usuario
        FOREIGN KEY (id_usuario)   REFERENCES usuario(id_usuario)
);

-- Tabla: historial_movimiento
-- Registra cualquier cambio sobre un material, realizado por un usuario
CREATE TABLE historial_movimiento (
    id_movimiento    INT NOT NULL AUTO_INCREMENT,
    id_material      INT NOT NULL,
    id_usuario       INT NOT NULL,
    tipo_movimiento  VARCHAR(50)  NOT NULL,    
    fecha            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones    VARCHAR(500),
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
    ('Componentes de red',    'Switches, routers, cables, tarjetas de red, etc.'),
    ('Hardware',              'Placas base, procesadores, RAM, discos duros, SSD, etc.'),
    ('Herramientas',          'Destornilladores, pulseras antiestáticas, polímetros, etc.'),
    ('Material fungible',     'Tornillos, bridas, pasta térmica, etc.'),
    ('Equipos completos',     'PCs, portátiles, Raspberry Pi, etc.');

