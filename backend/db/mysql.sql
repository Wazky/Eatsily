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

CREATE TABLE `daaexample`.`pets` (
	`pet_id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(50) NOT NULL,
	`specie` enum('DOG','CAT','BIRD','RABBIT','OTHER') NOT NULL,
	`breed` varchar(50),
	`owner_id` int,
	PRIMARY KEY (`pet_id`),
	FOREIGN KEY (`owner_id`) REFERENCES `people`(`id_person`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE USER IF NOT EXISTS 'daa'@'localhost' IDENTIFIED WITH mysql_native_password BY 'daa';
GRANT ALL ON `daaexample`.* TO 'daa'@'localhost';
