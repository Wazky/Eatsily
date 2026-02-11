DROP DATABASE IF EXISTS `daaexample`;
CREATE DATABASE `daaexample`;

CREATE TABLE `daaexample`.`people` (
	`id_person` BIGINT NOT NULL AUTO_INCREMENT,
	`name` varchar(50) NOT NULL,
	`surname` varchar(100) NOT NULL,
	PRIMARY KEY (`id_person`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `daaexample`.`users` (
	`id_user` BIGINT NOT NULL AUTO_INCREMENT,
	`username` VARCHAR(50) UNIQUE NOT NULL, 
	`password_hash`VARCHAR(255) NOT NULL,
	`email` VARCHAR(100) NOT NULL,
	`role` VARCHAR(20) NOT NULL,
	`active` BOOLEAN NOT NULL DEFAULT TRUE,
	`blocked` BOOLEAN NOT NULL DEFAULT FALSE,
	`failed_login_attempts` INT NOT NULL DEFAULT 0,
	`creation_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`last_login` TIMESTAMP NULL,
	`person_id` BIGINT NOT NULL,
	PRIMARY KEY (`id_user`),
	FOREIGN KEY (`person_id`) REFERENCES `people`(`id_person`) ON DELETE CASCADE,
	INDEX `idx_username` (`username`),
	INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `daaexample`.`tokens` (
	`id_token` BIGINT NOT NULL AUTO_INCREMENT,
	`token` VARCHAR(255) NOT NULL,
	`token_type` VARCHAR(20) NOT NULL,
	`expired` BOOLEAN NOT NULL DEFAULT FALSE,
	`revoked` BOOLEAN NOT NULL DEFAULT FALSE,
	`user_id` BIGINT NOT NULL,
	PRIMARY KEY (`id_token`),
	FOREIGN KEY (`user_id`) REFERENCES `users`(`id_user`) ON DELETE CASCADE,
	INDEX `idx_token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE USER IF NOT EXISTS 'daa'@'localhost' IDENTIFIED WITH mysql_native_password BY 'daa';
GRANT ALL ON `daaexample`.* TO 'daa'@'localhost';

USE `daaexample`;

-- Insertar datos en la tabla 'people'
INSERT INTO `people` (`name`, `surname`) VALUES
('Juan', 'Pérez García'),
('María', 'López Martínez'),
('Carlos', 'Rodríguez Fernández'),
('Ana', 'Gómez Sánchez'),
('Pedro', 'Martín Díaz'),
('Laura', 'Hernández Ruiz'),
('Miguel', 'Jiménez Castro'),
('Elena', 'Torres Ortega'),
('David', 'Navarro Ramos'),
('Sofía', 'Romero Molina'),
('Javier', 'Morales Gil'),
('Carmen', 'Serrano Vázquez'),
('Daniel', 'Ortega Blanco'),
('Patricia', 'Delgado Márquez'),
('Francisco', 'Castro Herrera'),
('Isabel', 'Santos Peña'),
('Alejandro', 'Gutiérrez Rojas'),
('Teresa', 'Reyes Cabrera'),
('Raúl', 'Luna Campos'),
('Beatriz', 'Marín León');

-- Insertar datos en la tabla 'users' (las contraseñas son "password123" encriptadas con BCrypt)
-- Nota: BCrypt genera hashes diferentes cada vez, así que estos son ejemplos
INSERT INTO `users` (`username`, `password_hash`, `email`, `role`, `active`, `blocked`, `failed_login_attempts`, `person_id`) VALUES
('juanpg', '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'juan.perez@example.com', 'ADMIN', TRUE, FALSE, 0, 1),
('marialm', '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'maria.lopez@example.com', 'USER', TRUE, FALSE, 0, 2),
('carlosrf', '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'carlos.rodriguez@example.com', 'USER', TRUE, FALSE, 0, 3),
('anags', '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'ana.gomez@example.com', 'USER', TRUE, FALSE, 0, 4),
('pedromd', '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'pedro.martin@example.com', 'USER', FALSE, TRUE, 5, 5),
('laurahr', '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'laura.hernandez@example.com', 'MODERATOR', TRUE, FALSE, 2, 6),
('migueljc', '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'miguel.jimenez@example.com', 'USER', TRUE, FALSE, 0, 7),
('elenato', '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'elena.torres@example.com', 'USER', TRUE, FALSE, 1, 8),
('davidnr', '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'david.navarro@example.com', 'USER', TRUE, FALSE, 0, 9),
('sofiar', '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'sofia.romero@example.com', 'USER', TRUE, FALSE, 0, 10);

