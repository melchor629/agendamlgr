-- -----------------------------------------------------
-- Schema AGENDAMLGR
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Table AGENDAMLGR.Evento
-- -----------------------------------------------------
CREATE TABLE Evento (
  id INT NOT NULL GENERATED ALWAYS AS IDENTITY
        (START WITH 1, INCREMENT BY 1),
  tipo DECIMAL(1,0) NOT NULL,
  nombre VARCHAR(45) NOT NULL,
  descripcion LONG VARCHAR NOT NULL,
  fecha TIMESTAMP NOT NULL,
  precio DECIMAL(9,2),
  direccion VARCHAR(200) NOT NULL,
  validado DECIMAL(1,0) NOT NULL DEFAULT 0,
  creador VARCHAR(45) NOT NULL,
  latitud DOUBLE PRECISION,
  longitud DOUBLE PRECISION,
  flickrUserId VARCHAR(45),
  flickrAlbumId VARCHAR(45),
  PRIMARY KEY (id));

-- -----------------------------------------------------
-- Table AGENDAMLGR.Usuario
-- -----------------------------------------------------
CREATE TABLE Usuario (
  id VARCHAR(45),
  tipo DECIMAL(1,0) NOT NULL,
  nombre VARCHAR(45) NOT NULL,
  apellidos VARCHAR(90) NOT NULL,
  email VARCHAR(45) NOT NULL,
  imagen VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (id));

ALTER TABLE AGENDAMLGR.USUARIO
ADD CONSTRAINT EMAIL_UNICO UNIQUE (email);

ALTER TABLE AGENDAMLGR.EVENTO
ADD CONSTRAINT fk_Evento_Usuario_Creador FOREIGN KEY (creador) REFERENCES AGENDAMLGR.Usuario (id) ON DELETE CASCADE;

-- -----------------------------------------------------
-- Table AGENDAMLGR.Categoria
-- -----------------------------------------------------
CREATE TABLE Categoria (
  id INT NOT NULL GENERATED ALWAYS AS IDENTITY
        (START WITH 1, INCREMENT BY 1),
  nombre VARCHAR(45) NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE AGENDAMLGR.CATEGORIA
ADD CONSTRAINT NOMBRE_UNICO UNIQUE (nombre);

-- -----------------------------------------------------
-- Table AGENDAMLGR.CategoriaEvento
-- -----------------------------------------------------
CREATE TABLE CategoriaEvento (
  Evento_id INT NOT NULL,
  Categoria_id INT NOT NULL,
  PRIMARY KEY (Evento_id, Categoria_id),
  CONSTRAINT fk_Evento_has_Categoria_Evento
    FOREIGN KEY (Evento_id)
    REFERENCES AGENDAMLGR.Evento (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT fk_Evento_has_Categoria_Categoria1
    FOREIGN KEY (Categoria_id)
    REFERENCES AGENDAMLGR.Categoria (id)
    ON DELETE CASCADE);

-- -----------------------------------------------------
-- Table AGENDAMLGR.Preferencias
-- -----------------------------------------------------
CREATE TABLE Preferencias (
  Usuario_id VARCHAR(45) NOT NULL,
  Categoria_id INT NOT NULL,
  PRIMARY KEY (Usuario_id, Categoria_id),
  CONSTRAINT fk_Usuario_has_Categoria_Usuario1
    FOREIGN KEY (Usuario_id)
    REFERENCES AGENDAMLGR.Usuario (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT fk_Usuario_has_Categoria_Categoria1
    FOREIGN KEY (Categoria_id)
    REFERENCES AGENDAMLGR.Categoria (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);
