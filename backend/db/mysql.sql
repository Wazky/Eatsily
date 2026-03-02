DROP DATABASE IF EXISTS `eatsily`;
CREATE DATABASE `eatsily`;

CREATE TABLE `eatsily`.`people` (
	`id_person` BIGINT NOT NULL AUTO_INCREMENT,
	`name` varchar(50) NOT NULL,
	`surname` varchar(100) NOT NULL,
	PRIMARY KEY (`id_person`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`users` (
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

CREATE TABLE `eatsily`.`tokens` (
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

CREATE TABLE `eatsily`.`recipes` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name`varchar(100) NOT NULL,
	`description` text,
	`preparation_time` int NOT NULL,
	`cooking_time` int,
	`servings` int NOT NULL,
	PRIMARY KEY (`id`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE USER IF NOT EXISTS 'eatsily_user'@'localhost' IDENTIFIED WITH mysql_native_password BY 'eatsily_password';
GRANT ALL ON `eatsily`.* TO 'eatsily_user'@'localhost';