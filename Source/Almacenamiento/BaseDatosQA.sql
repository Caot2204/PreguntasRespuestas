CREATE DATABASE IF NOT EXISTS preguntas_respuestas_bd;

USE preguntas_respuestas_bd;

CREATE TABLE IF NOT EXISTS usuarios (nombre VARBINARY(200) NOT NULL PRIMARY KEY, 
                      correo VARBINARY(200) NOT NULL,
                      contrasenia VARBINARY(200) NOT NULL,
                      foto_perfil LONGBLOB)
                      ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cuestionarios (id VARBINARY(10) NOT NULL PRIMARY KEY,
                           nombre VARBINARY(250) NOT NULL,
                           veces_jugado INT NOT NULL,
                           ultimo_ganador VARBINARY(200),
                           creador VARBINARY(200) NOT NULL,
                           FOREIGN KEY (creador) REFERENCES usuarios (nombre)
                           ON DELETE CASCADE ON UPDATE CASCADE)
                           ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS preguntas (numero VARBINARY(10) NOT NULL,
                        descripcion VARBINARY(200),
                        imagen LONGBLOB,
                        cuestionario VARBINARY(10) NOT NULL,
                        PRIMARY KEY (numero, cuestionario),
                        FOREIGN KEY (cuestionario) REFERENCES cuestionarios (id)
                        ON DELETE CASCADE ON UPDATE CASCADE)
                        ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS respuestas (letra VARBINARY(1) NOT NULL,
                         descripcion VARBINARY(200),
                         imagen LONGBLOB,
                         correcta BOOLEAN NOT NULL,
                         pregunta VARBINARY(10) NOT NULL,
                         cuestionario VARBINARY(10) NOT NULL,
                         PRIMARY KEY (letra, pregunta, cuestionario),
                         FOREIGN KEY (pregunta) REFERENCES preguntas (numero) ON DELETE CASCADE,
                         FOREIGN KEY (cuestionario) REFERENCES cuestionarios (id) ON DELETE CASCADE)
                         ENGINE=InnoDB;
