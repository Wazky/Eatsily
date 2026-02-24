DROP DATABASE IF EXISTS `eatsily`;
CREATE DATABASE `eatsily`;

CREATE TABLE `eatsily`.`people` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(50) NOT NULL,
	`surname` varchar(100) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`users` (
	`login` varchar(100) NOT NULL,
	`password` varchar(64) NOT NULL,
	`role` varchar(10) NOT NULL,
	PRIMARY KEY (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`pets` (
	`pet_id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(50) NOT NULL,
	`specie` enum('DOG','CAT','BIRD','RABBIT','OTHER') NOT NULL,
	`breed` varchar(50),
	`owner_id` int,
	PRIMARY KEY (`pet_id`),
	FOREIGN KEY (`owner_id`) REFERENCES `people`(`id`) ON DELETE CASCADE
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

CREATE USER IF NOT EXISTS 'daa'@'localhost' IDENTIFIED WITH mysql_native_password BY 'daa';
GRANT ALL ON `eatsily`.* TO 'daa'@'localhost';

CREATE USER IF NOT EXISTS 'eatsily_user'@'localhost' IDENTIFIED WITH mysql_native_password BY 'eatsily_password';
GRANT ALL ON `eatsily`.* TO 'eatsily_user'@'localhost';