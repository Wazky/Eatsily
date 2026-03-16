DROP DATABASE IF EXISTS `eatsily`;
CREATE DATABASE `eatsily`;

-- ========== Tables structure for database `eatsily` ==========

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

-- ========== Root Recipes ==========
-- Table to store original recipes ( adjust on future if needed )
CREATE TABLE `eatsily`.`root_recipes` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`name`varchar(100) NOT NULL,
	PRIMARY KEY (`id`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`recipes` (
	`id_recipe` BIGINT NOT NULL AUTO_INCREMENT,
	`title`varchar(100) NOT NULL,
	`description` text,
	`preparation_time` int NOT NULL,	-- In minutes
	`cooking_time` int,					-- In minutes, can be null if not applicable
	`difficulty` ENUM('EASY', 'MEDIUM', 'HARD'),
	`servings` int NOT NULL,
	`is_public` BOOLEAN NOT NULL DEFAULT FALSE,
	`is_lunchbox` BOOLEAN NOT NULL DEFAULT FALSE,
	`image_path` VARCHAR(255),
	`user_id` BIGINT NOT NULL,
	`root_recipe_id` BIGINT,			-- Nullable (not null in future, if we want to link to a root recipe)
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`id_recipe`),
	FOREIGN KEY (`user_id`) REFERENCES `users`(`id_user`) ON DELETE CASCADE,
	FOREIGN KEY (`root_recipe_id`) REFERENCES `root_recipes`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`recipe_steps` (
	`id_recipe_step` BIGINT NOT NULL AUTO_INCREMENT,
	`step_number` INT NOT NULL,
	`title` VARCHAR(100),
	`description` TEXT NOT NULL,
	`image_path` VARCHAR(255),
	`recipe_id` BIGINT NOT NULL,
	PRIMARY KEY (`id_recipe_step`),
	FOREIGN KEY (`recipe_id`) REFERENCES `recipes`(`id_recipe`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`ingredient_categories` (
	`id_ingredient_category` BIGINT NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) NOT NULL,
	`description`VARCHAR(255),
	PRIMARY KEY (`id_ingredient_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`ingredients` (
	`id_ingredient` BIGINT NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) NOT NULL UNIQUE,
	`category_id` BIGINT,
	PRIMARY KEY (`id_ingredient`),
	FOREIGN KEY (`category_id`) REFERENCES `ingredient_categories`(`id_ingredient_category`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`measurement_units` (
	`id_measurement_unit` BIGINT NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(50) NOT NULL UNIQUE,
	`abbreviation` VARCHAR(20) NOT NULL,
	`type` ENUM('VOLUME', 'WEIGHT', 'UNIT', 'OTHER') NOT NULL,
	PRIMARY KEY (`id_measurement_unit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`recipe_ingredients` (
	`id_recipe_ingredient` BIGINT NOT NULL AUTO_INCREMENT,
	`quantity` DECIMAL(10, 2) NOT NULL,
	`notes` VARCHAR(255),
	`recipe_id` BIGINT NOT NULL,
	`ingredient_id` BIGINT NOT NULL,
	`measurement_unit_id` BIGINT NOT NULL,
	PRIMARY KEY (`id_recipe_ingredient`),
	FOREIGN KEY (`recipe_id`) REFERENCES `recipes`(`id_recipe`) ON DELETE CASCADE,
	FOREIGN KEY (`ingredient_id`) REFERENCES `ingredients`(`id_ingredient`) ON DELETE CASCADE,
	FOREIGN KEY (`measurement_unit_id`) REFERENCES `measurement_units`(`id_measurement_unit`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE USER IF NOT EXISTS 'eatsily_user'@'localhost' IDENTIFIED WITH mysql_native_password BY 'eatsily_password';
GRANT ALL ON `eatsily`.* TO 'eatsily_user'@'localhost';


-- ========== Initial data for database `eatsily` ==========

USE `eatsily`;

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


-- ========== Events for database `eatsily` ==========

-- sActivate event scheduler
SET GLOBAL event_scheduler = ON;

-- Create a event to clean up expired tokens every day at midnight
DELIMITER $$

CREATE EVENT IF NOT EXISTS `clean_expired_tokens_daily`
ON SCHEDULE EVERY 1 DAY
STARTS TIMESTAMP(CURRENT_DATE, '00:00:00')
DO
BEGIN
	DELETE FROM `eatsily`.`tokens`
	WHERE `expired` = TRUE OR `revoked` = TRUE;
END $$

-- Create an event to reset failed login attempts every hour
CREATE EVENT IF NOT EXISTS `reset_failed_logins_hourly`
ON SCHEDULE EVERY 1 HOUR
STARTS CURRENT_TIMESTAMP
DO
BEGIN
	UPDATE `eatsily`.`users`
	SET `failed_login_attempts` = 0
	WHERE `failed_login_attempts` > 0;
END $$


DELIMITER ;


-- ========== Catalog data ==========
 
-- Ingredient categories
INSERT INTO `ingredient_categories` (`name`, `description`) VALUES
('Vegetables', 'Fresh and cooked vegetables'),
('Meat', 'Beef, pork, chicken and other meats'),
('Fish & Seafood', 'Fresh and cured fish and seafood'),
('Dairy', 'Milk, cheese, butter and other dairy products'),
('Grains & Pasta', 'Rice, pasta, bread and other grains'),
('Legumes', 'Lentils, chickpeas, beans and other legumes'),
('Spices & Condiments', 'Salt, pepper, olive oil and other condiments'),
('Eggs', 'Chicken and other eggs'),
('Fruit', 'Fresh and dried fruit');
 
-- Measurement units
INSERT INTO `measurement_units` (`name`, `abbreviation`, `type`) VALUES
('Gram', 'g', 'WEIGHT'),
('Kilogram', 'kg', 'WEIGHT'),
('Milliliter', 'ml', 'VOLUME'),
('Liter', 'l', 'VOLUME'),
('Teaspoon', 'tsp', 'VOLUME'),
('Tablespoon', 'tbsp', 'VOLUME'),
('Unit', 'u', 'UNIT'),
('Slice', 'slice', 'UNIT'),
('Pinch', 'pinch', 'UNIT'),
('Cup', 'cup', 'VOLUME');
 
-- Ingredients
INSERT INTO `ingredients` (`name`, `category_id`) VALUES
('spaghetti', 5),           -- 1
('egg', 8),                 -- 2
('pancetta', 2),            -- 3
('parmesan cheese', 4),     -- 4
('black pepper', 7),        -- 5
('salt', 7),                -- 6
('olive oil', 7),           -- 7
('garlic', 1),              -- 8
('onion', 1),               -- 9
('tomato', 1),              -- 10
('chicken breast', 2),      -- 11
('rice', 5),                -- 12
('lemon', 9),               -- 13
('butter', 4),              -- 14
('flour', 5),               -- 15
('milk', 4),                -- 16
('potato', 1),              -- 17
('carrot', 1),              -- 18
('tuna', 3),                -- 19
('lettuce', 1);             -- 20
 
-- ========== Root recipes ==========
 
INSERT INTO `root_recipes` (`name`) VALUES
('Pasta Carbonara'),        -- 1
('Tomato Pasta'),           -- 2
('Grilled Chicken'),        -- 3
('Potato Omelette'),        -- 4
('Caesar Salad');           -- 5
 
-- ========== Recipes ==========
-- user_id 1 = juanpg (ADMIN), 2 = marialm, 3 = carlosrf, 4 = anags
 
INSERT INTO `recipes` (
    `title`, `description`, `preparation_time`, `cooking_time`,
    `difficulty`, `servings`, `is_public`, `is_lunchbox`,
    `image_path`, `user_id`, `root_recipe_id`
) VALUES
-- juanpg recipes
('Pasta Carbonara',         'Classic Italian carbonara with eggs and pancetta', 10, 20, 'MEDIUM', 2, TRUE,  FALSE, NULL, 1, 1),
('Tomato and Basil Pasta',  'Simple and quick tomato pasta',                    5,  15, 'EASY',   4, TRUE,  FALSE, NULL, 1, 2),
('My Carbonara Twist',      'Carbonara with a touch of garlic',                 10, 20, 'MEDIUM', 2, FALSE, FALSE, NULL, 1, 1),
 
-- marialm recipes
('Grilled Chicken',         'Juicy grilled chicken with lemon and herbs',       15, 25, 'EASY',   2, TRUE,  FALSE, NULL, 2, 3),
('Lunchbox Chicken Rice',   'Grilled chicken with rice, perfect for lunchbox',  20, 30, 'EASY',   1, TRUE,  TRUE,  NULL, 2, 3),
('Potato Omelette',         'Traditional Spanish potato omelette',              20, 15, 'MEDIUM', 4, TRUE,  FALSE, NULL, 2, 4),
 
-- carlosrf recipes
('Caesar Salad',            'Classic Caesar salad with croutons',               15, 0,  'EASY',   2, TRUE,  TRUE,  NULL, 3, 5),
('Tuna Salad Lunchbox',     'Light tuna and lettuce salad for lunchbox',        10, 0,  'EASY',   1, TRUE,  TRUE,  NULL, 3, 5),
('Creamy Carbonara',        'Extra creamy carbonara version',                   10, 20, 'HARD',   2, FALSE, FALSE, NULL, 3, 1),
 
-- anags recipes (all private)
('My Tomato Pasta',         'My personal tomato pasta recipe',                  5,  15, 'EASY',   2, FALSE, FALSE, NULL, 4, 2),
('Veggie Omelette',         'Potato omelette with extra vegetables',            25, 15, 'MEDIUM', 4, FALSE, FALSE, NULL, 4, 4);
 
-- ========== Recipe steps ==========
 
-- Recipe 1: Pasta Carbonara (juanpg)
INSERT INTO `recipe_steps` (`step_number`, `title`, `description`, `recipe_id`) VALUES
(1, 'Boil pasta',       'Boil spaghetti in salted water for 10 minutes until al dente',                                     1),
(2, 'Cook pancetta',    'Fry pancetta in a pan without oil until golden and crispy',                                         1),
(3, 'Prepare sauce',    'Beat eggs with grated parmesan and a generous amount of black pepper in a bowl',                   1),
(4, 'Combine',          'Remove pan from heat, add drained pasta and egg mixture. Mix quickly to avoid scrambling the eggs', 1);
 
-- Recipe 2: Tomato and Basil Pasta (juanpg)
INSERT INTO `recipe_steps` (`step_number`, `title`, `description`, `recipe_id`) VALUES
(1, 'Sauté garlic',     'Heat olive oil in a pan and sauté minced garlic for 1 minute',        2),
(2, 'Add tomato',       'Add chopped tomatoes and cook for 10 minutes over medium heat',       2),
(3, 'Cook pasta',       'Boil pasta in salted water and drain',                                2),
(4, 'Combine',          'Mix pasta with tomato sauce and serve immediately',                   2);
 
-- Recipe 4: Grilled Chicken (marialm)
INSERT INTO `recipe_steps` (`step_number`, `title`, `description`, `recipe_id`) VALUES
(1, 'Marinate',         'Marinate chicken with lemon juice, olive oil, salt and pepper for 10 minutes', 4),
(2, 'Grill',            'Grill chicken on high heat for 6-7 minutes per side until cooked through',     4),
(3, 'Rest',             'Let the chicken rest for 5 minutes before slicing',                            4);
 
-- Recipe 5: Lunchbox Chicken Rice (marialm)
INSERT INTO `recipe_steps` (`step_number`, `title`, `description`, `recipe_id`) VALUES
(1, 'Cook rice',        'Cook rice in salted water following package instructions',                      5),
(2, 'Grill chicken',    'Season and grill chicken breast until fully cooked',                           5),
(3, 'Assemble',         'Place rice and sliced chicken in your lunchbox container',                     5);
 
-- Recipe 6: Potato Omelette (marialm)
INSERT INTO `recipe_steps` (`step_number`, `title`, `description`, `recipe_id`) VALUES
(1, 'Fry potatoes',     'Peel and slice potatoes, fry in olive oil over medium heat until tender',      6),
(2, 'Beat eggs',        'Beat eggs with salt in a large bowl, add the fried potatoes',                  6),
(3, 'Cook omelette',    'Pour mixture into a pan and cook on both sides until golden',                  6);
 
-- Recipe 7: Caesar Salad (carlosrf)
INSERT INTO `recipe_steps` (`step_number`, `title`, `description`, `recipe_id`) VALUES
(1, 'Prepare lettuce',  'Wash and tear lettuce into bite-sized pieces',                                 7),
(2, 'Grill chicken',    'Season and grill chicken breast, then slice thinly',                           7),
(3, 'Assemble',         'Combine lettuce, chicken, parmesan and dressing. Toss well before serving',   7);
 
-- Recipe 8: Tuna Salad Lunchbox (carlosrf)
INSERT INTO `recipe_steps` (`step_number`, `title`, `description`, `recipe_id`) VALUES
(1, 'Prepare base',     'Wash and chop lettuce into pieces',                                            8),
(2, 'Add tuna',         'Drain canned tuna and add to the lettuce',                                     8),
(3, 'Season',           'Drizzle with olive oil, salt and a squeeze of lemon. Pack in lunchbox',        8);
 
-- ========== Recipe ingredients ==========
 
-- Recipe 1: Pasta Carbonara
-- unit ids: 1=g, 2=kg, 7=u, 9=pinch
INSERT INTO `recipe_ingredients` (`quantity`, `notes`, `recipe_id`, `ingredient_id`, `measurement_unit_id`) VALUES
(200.00, NULL,                  1, 1,  1),   -- 200g spaghetti
(3.00,   'at room temperature', 1, 2,  7),   -- 3 eggs
(100.00, 'diced',               1, 3,  1),   -- 100g pancetta
(50.00,  'freshly grated',      1, 4,  1),   -- 50g parmesan
(1.00,   'to taste',            1, 5,  9),   -- 1 pinch black pepper
(1.00,   'to taste',            1, 6,  9);   -- 1 pinch salt
 
-- Recipe 2: Tomato and Basil Pasta
INSERT INTO `recipe_ingredients` (`quantity`, `notes`, `recipe_id`, `ingredient_id`, `measurement_unit_id`) VALUES
(320.00, NULL,          2, 1,  1),   -- 320g pasta
(400.00, 'chopped',     2, 10, 1),   -- 400g tomato
(2.00,   'minced',      2, 8,  7),   -- 2 cloves garlic
(2.00,   NULL,          2, 7,  6),   -- 2 tbsp olive oil
(1.00,   'to taste',    2, 6,  9);   -- salt
 
-- Recipe 4: Grilled Chicken
INSERT INTO `recipe_ingredients` (`quantity`, `notes`, `recipe_id`, `ingredient_id`, `measurement_unit_id`) VALUES
(300.00, NULL,          4, 11, 1),   -- 300g chicken breast
(1.00,   'juiced',      4, 13, 7),   -- 1 lemon
(2.00,   NULL,          4, 7,  6),   -- 2 tbsp olive oil
(1.00,   'to taste',    4, 6,  9),   -- salt
(1.00,   'to taste',    4, 5,  9);   -- black pepper
 
-- Recipe 5: Lunchbox Chicken Rice
INSERT INTO `recipe_ingredients` (`quantity`, `notes`, `recipe_id`, `ingredient_id`, `measurement_unit_id`) VALUES
(150.00, NULL,          5, 12, 1),   -- 150g rice
(200.00, NULL,          5, 11, 1),   -- 200g chicken breast
(1.00,   'to taste',    5, 6,  9),   -- salt
(1.00,   NULL,          5, 7,  6);   -- 1 tbsp olive oil
 
-- Recipe 6: Potato Omelette
INSERT INTO `recipe_ingredients` (`quantity`, `notes`, `recipe_id`, `ingredient_id`, `measurement_unit_id`) VALUES
(400.00, 'peeled and sliced',   6, 17, 1),   -- 400g potato
(4.00,   NULL,                  6, 2,  7),   -- 4 eggs
(1.00,   'to taste',            6, 6,  9),   -- salt
(4.00,   NULL,                  6, 7,  6);   -- 4 tbsp olive oil
 
-- Recipe 7: Caesar Salad
INSERT INTO `recipe_ingredients` (`quantity`, `notes`, `recipe_id`, `ingredient_id`, `measurement_unit_id`) VALUES
(200.00, NULL,                  7, 20, 1),   -- 200g lettuce
(150.00, NULL,                  7, 11, 1),   -- 150g chicken breast
(30.00,  'freshly grated',      7, 4,  1),   -- 30g parmesan
(1.00,   NULL,                  7, 7,  6);   -- 1 tbsp olive oil
 
-- Recipe 8: Tuna Salad Lunchbox
INSERT INTO `recipe_ingredients` (`quantity`, `notes`, `recipe_id`, `ingredient_id`, `measurement_unit_id`) VALUES
(150.00, NULL,          8, 20, 1),   -- 150g lettuce
(160.00, 'drained',     8, 19, 1),   -- 160g tuna
(1.00,   'juiced',      8, 13, 7),   -- 1 lemon
(1.00,   'to taste',    8, 6,  9),   -- salt
(1.00,   NULL,          8, 7,  6);   -- 1 tbsp olive oil