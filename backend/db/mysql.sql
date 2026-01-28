DROP DATABASE IF EXISTS `daaexample`;
CREATE DATABASE `daaexample`;

CREATE TABLE `daaexample`.`people` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(50) NOT NULL,
	`surname` varchar(100) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `daaexample`.`users` (
	`login` varchar(100) NOT NULL,
	`password` varchar(64) NOT NULL,
	`role` varchar(10) NOT NULL,
	PRIMARY KEY (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `daaexample`.`pets` (
	`pet_id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(50) NOT NULL,
	`specie` enum('DOG','CAT','BIRD','RABBIT','OTHER') NOT NULL,
	`breed` varchar(50),
	`owner_id` int,
	PRIMARY KEY (`pet_id`),
	FOREIGN KEY (`owner_id`) REFERENCES `people`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE USER IF NOT EXISTS 'daa'@'localhost' IDENTIFIED WITH mysql_native_password BY 'daa';
GRANT ALL ON `daaexample`.* TO 'daa'@'localhost';
