DROP DATABASE IF EXISTS `eatsily`;
CREATE DATABASE `eatsily`;

-- ========== Tables structure for database `eatsily` ==========

CREATE TABLE `eatsily`.`people` (
    `id_person` BIGINT NOT NULL AUTO_INCREMENT,
    `name`      VARCHAR(50)  NOT NULL,
    `surname`   VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id_person`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`users` (
    `id_user`               BIGINT       NOT NULL AUTO_INCREMENT,
    `username`              VARCHAR(50)  UNIQUE NOT NULL,
    `password_hash`         VARCHAR(255) NOT NULL,
    `email`                 VARCHAR(100) NOT NULL,
    `role`                  VARCHAR(20)  NOT NULL,
    `active`                BOOLEAN      NOT NULL DEFAULT TRUE,
    `blocked`               BOOLEAN      NOT NULL DEFAULT FALSE,
    `failed_login_attempts` INT          NOT NULL DEFAULT 0,
    `creation_date`         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `last_login`            TIMESTAMP    NULL,
    `person_id`             BIGINT       NOT NULL,
    PRIMARY KEY (`id_user`),
    FOREIGN KEY (`person_id`) REFERENCES `people`(`id_person`) ON DELETE CASCADE,
    INDEX `idx_username` (`username`),
    INDEX `idx_email`    (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`tokens` (
    `id_token`   BIGINT       NOT NULL AUTO_INCREMENT,
    `token`      VARCHAR(255) NOT NULL,
    `token_type` VARCHAR(20)  NOT NULL,
    `expired`    BOOLEAN      NOT NULL DEFAULT FALSE,
    `revoked`    BOOLEAN      NOT NULL DEFAULT FALSE,
    `user_id`    BIGINT       NOT NULL,
    PRIMARY KEY (`id_token`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id_user`) ON DELETE CASCADE,
    INDEX `idx_token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========== Root Recipes ==========

CREATE TABLE `eatsily`.`root_recipes` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========== Recipes ==========

CREATE TABLE `eatsily`.`recipes` (
    `id_recipe`       BIGINT    NOT NULL AUTO_INCREMENT,
    `preparation_time` INT      NOT NULL DEFAULT 0,
    `cooking_time`    INT       NOT NULL DEFAULT 0,
    `difficulty`      ENUM('EASY', 'MEDIUM', 'HARD'),
    `servings`        INT       NOT NULL DEFAULT 1,
    `is_public`       BOOLEAN   NOT NULL DEFAULT FALSE,
    `is_lunchbox`     BOOLEAN   NOT NULL DEFAULT FALSE,
    `image_path`      VARCHAR(255),
    `user_id`         BIGINT    NOT NULL,
    `root_recipe_id`  BIGINT,
    `created_at`      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id_recipe`),
    FOREIGN KEY (`user_id`)        REFERENCES `users`(`id_user`)       ON DELETE CASCADE,
    FOREIGN KEY (`root_recipe_id`) REFERENCES `root_recipes`(`id`)     ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`recipe_translations` (
    `recipe_id`   BIGINT       NOT NULL,
    `locale`      VARCHAR(5)   NOT NULL,
    `title`       VARCHAR(100) NOT NULL,
    `description` TEXT,
    PRIMARY KEY (`recipe_id`, `locale`),
    FOREIGN KEY (`recipe_id`) REFERENCES `recipes`(`id_recipe`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========== Recipe Steps ==========

CREATE TABLE `eatsily`.`recipe_steps` (
    `id_recipe_step` BIGINT NOT NULL AUTO_INCREMENT,
    `step_number`    INT    NOT NULL,
    `image_path`     VARCHAR(255),
    `recipe_id`      BIGINT NOT NULL,
    PRIMARY KEY (`id_recipe_step`),
    FOREIGN KEY (`recipe_id`) REFERENCES `recipes`(`id_recipe`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`recipe_step_translations` (
    `step_id`     BIGINT     NOT NULL,
    `locale`      VARCHAR(5) NOT NULL,
    `title`       VARCHAR(100),
    `description` TEXT       NOT NULL,
    PRIMARY KEY (`step_id`, `locale`),
    FOREIGN KEY (`step_id`) REFERENCES `recipe_steps`(`id_recipe_step`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========== Catalog ==========

CREATE TABLE `eatsily`.`ingredient_categories` (
    `id_ingredient_category` BIGINT       NOT NULL AUTO_INCREMENT,
    `name`                   VARCHAR(100) NOT NULL,
    `description`            VARCHAR(255),
    PRIMARY KEY (`id_ingredient_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`ingredients` (
    `id_ingredient` BIGINT       NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(100) NOT NULL UNIQUE,
    `category_id`   BIGINT,
    PRIMARY KEY (`id_ingredient`),
    FOREIGN KEY (`category_id`) REFERENCES `ingredient_categories`(`id_ingredient_category`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`measurement_units` (
    `id_measurement_unit` BIGINT      NOT NULL AUTO_INCREMENT,
    `name`                VARCHAR(50) NOT NULL UNIQUE,
    `abbreviation`        VARCHAR(20) NOT NULL,
    `type`                ENUM('VOLUME', 'WEIGHT', 'UNIT', 'OTHER') NOT NULL,
    PRIMARY KEY (`id_measurement_unit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `eatsily`.`recipe_ingredients` (
    `id_recipe_ingredient` BIGINT         NOT NULL AUTO_INCREMENT,
    `quantity`             DECIMAL(10, 2) NOT NULL,
    `notes`                VARCHAR(255),
    `recipe_id`            BIGINT         NOT NULL,
    `ingredient_id`        BIGINT         NOT NULL,
    `measurement_unit_id`  BIGINT         NOT NULL,
    PRIMARY KEY (`id_recipe_ingredient`),
    FOREIGN KEY (`recipe_id`)           REFERENCES `recipes`(`id_recipe`)                     ON DELETE CASCADE,
    FOREIGN KEY (`ingredient_id`)       REFERENCES `ingredients`(`id_ingredient`)             ON DELETE CASCADE,
    FOREIGN KEY (`measurement_unit_id`) REFERENCES `measurement_units`(`id_measurement_unit`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========== Users and permissions ==========

CREATE USER IF NOT EXISTS 'eatsily_user'@'localhost' IDENTIFIED WITH mysql_native_password BY 'eatsily_password';
GRANT ALL ON `eatsily`.* TO 'eatsily_user'@'localhost';

-- ========== Set UTF-8 encoding ==========

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ========== Events ==========

USE `eatsily`;

SET GLOBAL event_scheduler = ON;

DELIMITER $$

CREATE EVENT IF NOT EXISTS `clean_expired_tokens_daily`
ON SCHEDULE EVERY 1 DAY
STARTS TIMESTAMP(CURRENT_DATE, '00:00:00')
DO
BEGIN
    DELETE FROM `eatsily`.`tokens` WHERE `expired` = TRUE OR `revoked` = TRUE;
END $$

CREATE EVENT IF NOT EXISTS `reset_failed_logins_hourly`
ON SCHEDULE EVERY 1 HOUR
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    UPDATE `eatsily`.`users` SET `failed_login_attempts` = 0 WHERE `failed_login_attempts` > 0;
END $$

DELIMITER ;

-- ========== Initial data ==========

USE `eatsily`;

-- People
INSERT INTO `people` (`name`, `surname`) VALUES
('Juan',      'Pérez García'),
('María',     'López Martínez'),
('Carlos',    'Rodríguez Fernández'),
('Ana',       'Gómez Sánchez'),
('Pedro',     'Martín Díaz'),
('Laura',     'Hernández Ruiz'),
('Miguel',    'Jiménez Castro'),
('Elena',     'Torres Ortega'),
('David',     'Navarro Ramos'),
('Sofía',     'Romero Molina');

-- Users (passwords are "password123" hashed with BCrypt)
INSERT INTO `users` (`username`, `password_hash`, `email`, `role`, `active`, `blocked`, `failed_login_attempts`, `person_id`) VALUES
('juanpg',   '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'juan.perez@example.com',    'ADMIN',     TRUE,  FALSE, 0, 1),
('marialm',  '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'maria.lopez@example.com',   'USER',      TRUE,  FALSE, 0, 2),
('carlosrf', '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'carlos.rodriguez@example.com', 'USER',   TRUE,  FALSE, 0, 3),
('anags',    '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'ana.gomez@example.com',     'USER',      TRUE,  FALSE, 0, 4),
('pedromd',  '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'pedro.martin@example.com',  'USER',      FALSE, TRUE,  5, 5),
('laurahr',  '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'laura.hernandez@example.com', 'MODERATOR', TRUE, FALSE, 2, 6),
('migueljc', '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'miguel.jimenez@example.com', 'USER',     TRUE,  FALSE, 0, 7),
('elenato',  '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'elena.torres@example.com',  'USER',      TRUE,  FALSE, 1, 8),
('davidnr',  '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'david.navarro@example.com', 'USER',      TRUE,  FALSE, 0, 9),
('sofiar',   '$2a$12$k8L9ZbYq5Q3m6S7dF8G9H0iJ1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7A8B9C0D', 'sofia.romero@example.com',  'USER',      TRUE,  FALSE, 0, 10);

-- ========== Catalog ==========

INSERT INTO `ingredient_categories` (`name`, `description`) VALUES
('Vegetables',          'Fresh and cooked vegetables'),
('Meat',                'Beef, pork, chicken and other meats'),
('Fish & Seafood',      'Fresh and cured fish and seafood'),
('Dairy',               'Milk, cheese, butter and other dairy products'),
('Grains & Pasta',      'Rice, pasta, bread and other grains'),
('Legumes',             'Lentils, chickpeas, beans and other legumes'),
('Spices & Condiments', 'Salt, pepper, olive oil and other condiments'),
('Eggs',                'Chicken and other eggs'),
('Fruit',               'Fresh and dried fruit');

INSERT INTO `measurement_units` (`name`, `abbreviation`, `type`) VALUES
('Gram',        'g',     'WEIGHT'),   -- 1
('Kilogram',    'kg',    'WEIGHT'),   -- 2
('Milliliter',  'ml',    'VOLUME'),   -- 3
('Liter',       'l',     'VOLUME'),   -- 4
('Teaspoon',    'tsp',   'VOLUME'),   -- 5
('Tablespoon',  'tbsp',  'VOLUME'),   -- 6
('Unit',        'u',     'UNIT'),     -- 7
('Slice',       'slice', 'UNIT'),     -- 8
('Pinch',       'pinch', 'OTHER'),    -- 9
('Cup',         'cup',   'VOLUME');   -- 10

INSERT INTO `ingredients` (`name`, `category_id`) VALUES
('spaghetti',       5),   -- 1
('egg',             8),   -- 2
('pancetta',        2),   -- 3
('parmesan cheese', 4),   -- 4
('black pepper',    7),   -- 5
('salt',            7),   -- 6
('olive oil',       7),   -- 7
('garlic',          1),   -- 8
('onion',           1),   -- 9
('tomato',          1),   -- 10
('chicken breast',  2),   -- 11
('rice',            5),   -- 12
('lemon',           9),   -- 13
('butter',          4),   -- 14
('flour',           5),   -- 15
('milk',            4),   -- 16
('potato',          1),   -- 17
('carrot',          1),   -- 18
('tuna',            3),   -- 19
('lettuce',         1);   -- 20

-- ========== Root recipes ==========

INSERT INTO `root_recipes` (`id`) VALUES (1), (2), (3), (4), (5);

-- ========== Recipes (without title/description) ==========

INSERT INTO `recipes` (`preparation_time`, `cooking_time`, `difficulty`, `servings`, `is_public`, `is_lunchbox`, `image_path`, `user_id`, `root_recipe_id`) VALUES
-- juanpg (user 1)
(10, 20, 'MEDIUM', 2, TRUE,  FALSE, NULL, 1, 1),   -- 1 Pasta Carbonara
(5,  15, 'EASY',   4, TRUE,  FALSE, NULL, 1, 2),   -- 2 Tomato Pasta
(10, 20, 'MEDIUM', 2, FALSE, FALSE, NULL, 1, 1),   -- 3 My Carbonara Twist
-- marialm (user 2)
(15, 25, 'EASY',   2, TRUE,  FALSE, NULL, 2, 3),   -- 4 Grilled Chicken
(20, 30, 'EASY',   1, TRUE,  TRUE,  NULL, 2, 3),   -- 5 Lunchbox Chicken Rice
(20, 15, 'MEDIUM', 4, TRUE,  FALSE, NULL, 2, 4),   -- 6 Potato Omelette
-- carlosrf (user 3)
(15, 0,  'EASY',   2, TRUE,  TRUE,  NULL, 3, 5),   -- 7 Caesar Salad
(10, 0,  'EASY',   1, TRUE,  TRUE,  NULL, 3, 5),   -- 8 Tuna Salad Lunchbox
(10, 20, 'HARD',   2, FALSE, FALSE, NULL, 3, 1),   -- 9 Creamy Carbonara
-- anags (user 4)
(5,  15, 'EASY',   2, FALSE, FALSE, NULL, 4, 2),   -- 10 My Tomato Pasta
(25, 15, 'MEDIUM', 4, FALSE, FALSE, NULL, 4, 4);   -- 11 Veggie Omelette

-- ========== Recipe translations ==========

INSERT INTO `recipe_translations` (`recipe_id`, `locale`, `title`, `description`) VALUES
(1,  'en', 'Pasta Carbonara',        'Classic Italian carbonara with eggs and pancetta'),
(2,  'en', 'Tomato and Basil Pasta', 'Simple and quick tomato pasta'),
(3,  'en', 'My Carbonara Twist',     'Carbonara with a touch of garlic'),
(4,  'en', 'Grilled Chicken',        'Juicy grilled chicken with lemon and herbs'),
(5,  'en', 'Lunchbox Chicken Rice',  'Grilled chicken with rice, perfect for lunchbox'),
(6,  'en', 'Potato Omelette',        'Traditional Spanish potato omelette'),
(7,  'en', 'Caesar Salad',           'Classic Caesar salad with croutons'),
(8,  'en', 'Tuna Salad Lunchbox',    'Light tuna and lettuce salad for lunchbox'),
(9,  'en', 'Creamy Carbonara',       'Extra creamy carbonara version'),
(10, 'en', 'My Tomato Pasta',        'My personal tomato pasta recipe'),
(11, 'en', 'Veggie Omelette',        'Potato omelette with extra vegetables'),
-- Spanish translations for public recipes
(1,  'es', 'Pasta Carbonara',        'Clásica carbonara italiana con huevos y panceta'),
(2,  'es', 'Pasta con Tomate',       'Pasta con tomate sencilla y rápida'),
(4,  'es', 'Pollo a la Plancha',     'Pollo jugoso a la plancha con limón y hierbas'),
(6,  'es', 'Tortilla de Patatas',    'Clásica tortilla española de patatas'),
(7,  'es', 'Ensalada César',         'Clásica ensalada César con picatostes');

-- ========== Recipe steps (without title/description) ==========

-- Recipe 1: Pasta Carbonara
INSERT INTO `recipe_steps` (`step_number`, `image_path`, `recipe_id`) VALUES
(1, NULL, 1),   -- step id 1
(2, NULL, 1),   -- step id 2
(3, NULL, 1),   -- step id 3
(4, NULL, 1);   -- step id 4

-- Recipe 2: Tomato and Basil Pasta
INSERT INTO `recipe_steps` (`step_number`, `image_path`, `recipe_id`) VALUES
(1, NULL, 2),   -- step id 5
(2, NULL, 2),   -- step id 6
(3, NULL, 2),   -- step id 7
(4, NULL, 2);   -- step id 8

-- Recipe 4: Grilled Chicken
INSERT INTO `recipe_steps` (`step_number`, `image_path`, `recipe_id`) VALUES
(1, NULL, 4),   -- step id 9
(2, NULL, 4),   -- step id 10
(3, NULL, 4);   -- step id 11

-- Recipe 5: Lunchbox Chicken Rice
INSERT INTO `recipe_steps` (`step_number`, `image_path`, `recipe_id`) VALUES
(1, NULL, 5),   -- step id 12
(2, NULL, 5),   -- step id 13
(3, NULL, 5);   -- step id 14

-- Recipe 6: Potato Omelette
INSERT INTO `recipe_steps` (`step_number`, `image_path`, `recipe_id`) VALUES
(1, NULL, 6),   -- step id 15
(2, NULL, 6),   -- step id 16
(3, NULL, 6);   -- step id 17

-- Recipe 7: Caesar Salad
INSERT INTO `recipe_steps` (`step_number`, `image_path`, `recipe_id`) VALUES
(1, NULL, 7),   -- step id 18
(2, NULL, 7),   -- step id 19
(3, NULL, 7);   -- step id 20

-- Recipe 8: Tuna Salad Lunchbox
INSERT INTO `recipe_steps` (`step_number`, `image_path`, `recipe_id`) VALUES
(1, NULL, 8),   -- step id 21
(2, NULL, 8),   -- step id 22
(3, NULL, 8);   -- step id 23

-- ========== Recipe step translations ==========

-- Recipe 1: Pasta Carbonara (en)
INSERT INTO `recipe_step_translations` (`step_id`, `locale`, `title`, `description`) VALUES
(1, 'en', 'Boil pasta',     'Boil spaghetti in salted water for 10 minutes until al dente'),
(2, 'en', 'Cook pancetta',  'Fry pancetta in a pan without oil until golden and crispy'),
(3, 'en', 'Prepare sauce',  'Beat eggs with grated parmesan and a generous amount of black pepper in a bowl'),
(4, 'en', 'Combine',        'Remove pan from heat, add drained pasta and egg mixture. Mix quickly to avoid scrambling the eggs');

-- Recipe 1: Pasta Carbonara (es)
INSERT INTO `recipe_step_translations` (`step_id`, `locale`, `title`, `description`) VALUES
(1, 'es', 'Cocer la pasta',     'Cocer los espaguetis en agua con sal durante 10 minutos hasta que estén al dente'),
(2, 'es', 'Cocinar la panceta', 'Freír la panceta en una sartén sin aceite hasta que esté dorada y crujiente'),
(3, 'es', 'Preparar la salsa',  'Batir los huevos con el parmesano rallado y una generosa cantidad de pimienta negra'),
(4, 'es', 'Mezclar',            'Retirar la sartén del fuego, añadir la pasta escurrida y la mezcla de huevos. Mezclar rápidamente para evitar que los huevos se cuajen');

-- Recipe 2: Tomato Pasta (en)
INSERT INTO `recipe_step_translations` (`step_id`, `locale`, `title`, `description`) VALUES
(5, 'en', 'Sauté garlic',   'Heat olive oil in a pan and sauté minced garlic for 1 minute'),
(6, 'en', 'Add tomato',     'Add chopped tomatoes and cook for 10 minutes over medium heat'),
(7, 'en', 'Cook pasta',     'Boil pasta in salted water and drain'),
(8, 'en', 'Combine',        'Mix pasta with tomato sauce and serve immediately');

-- Recipe 2: Tomato Pasta (es)
INSERT INTO `recipe_step_translations` (`step_id`, `locale`, `title`, `description`) VALUES
(5, 'es', 'Sofreír el ajo',     'Calentar el aceite en una sartén y sofreír el ajo picado durante 1 minuto'),
(6, 'es', 'Añadir el tomate',   'Añadir los tomates troceados y cocinar 10 minutos a fuego medio'),
(7, 'es', 'Cocer la pasta',     'Cocer la pasta en agua con sal y escurrir'),
(8, 'es', 'Mezclar',            'Mezclar la pasta con la salsa de tomate y servir inmediatamente');

-- Recipe 4: Grilled Chicken (en)
INSERT INTO `recipe_step_translations` (`step_id`, `locale`, `title`, `description`) VALUES
(9,  'en', 'Marinate',  'Marinate chicken with lemon juice, olive oil, salt and pepper for 10 minutes'),
(10, 'en', 'Grill',     'Grill chicken on high heat for 6-7 minutes per side until cooked through'),
(11, 'en', 'Rest',      'Let the chicken rest for 5 minutes before slicing');

-- Recipe 4: Grilled Chicken (es)
INSERT INTO `recipe_step_translations` (`step_id`, `locale`, `title`, `description`) VALUES
(9,  'es', 'Marinar',   'Marinar el pollo con zumo de limón, aceite de oliva, sal y pimienta durante 10 minutos'),
(10, 'es', 'Plancha',   'Cocinar el pollo a fuego alto 6-7 minutos por cada lado hasta que esté hecho'),
(11, 'es', 'Reposar',   'Dejar reposar el pollo 5 minutos antes de cortar');

-- Recipe 5: Lunchbox Chicken Rice (en)
INSERT INTO `recipe_step_translations` (`step_id`, `locale`, `title`, `description`) VALUES
(12, 'en', 'Cook rice',     'Cook rice in salted water following package instructions'),
(13, 'en', 'Grill chicken', 'Season and grill chicken breast until fully cooked'),
(14, 'en', 'Assemble',      'Place rice and sliced chicken in your lunchbox container');

-- Recipe 6: Potato Omelette (en)
INSERT INTO `recipe_step_translations` (`step_id`, `locale`, `title`, `description`) VALUES
(15, 'en', 'Fry potatoes',  'Peel and slice potatoes, fry in olive oil over medium heat until tender'),
(16, 'en', 'Beat eggs',     'Beat eggs with salt in a large bowl, add the fried potatoes'),
(17, 'en', 'Cook omelette', 'Pour mixture into a pan and cook on both sides until golden');

-- Recipe 6: Potato Omelette (es)
INSERT INTO `recipe_step_translations` (`step_id`, `locale`, `title`, `description`) VALUES
(15, 'es', 'Freír las patatas', 'Pelar y cortar las patatas, freírlas en aceite de oliva a fuego medio hasta que estén tiernas'),
(16, 'es', 'Batir los huevos', 'Batir los huevos con sal en un bol grande, añadir las patatas fritas'),
(17, 'es', 'Cocinar la tortilla', 'Verter la mezcla en la sartén y cocinar por ambos lados hasta que esté dorada');

-- Recipe 7: Caesar Salad (en)
INSERT INTO `recipe_step_translations` (`step_id`, `locale`, `title`, `description`) VALUES
(18, 'en', 'Prepare lettuce',  'Wash and tear lettuce into bite-sized pieces'),
(19, 'en', 'Grill chicken',    'Season and grill chicken breast, then slice thinly'),
(20, 'en', 'Assemble',         'Combine lettuce, chicken, parmesan and dressing. Toss well before serving');

-- Recipe 7: Caesar Salad (es)
INSERT INTO `recipe_step_translations` (`step_id`, `locale`, `title`, `description`) VALUES
(18, 'es', 'Preparar la lechuga',  'Lavar y trocear la lechuga en trozos pequeños'),
(19, 'es', 'Cocinar el pollo',     'Sazonar y cocinar el pollo a la plancha, luego cortar en láminas finas'),
(20, 'es', 'Montar la ensalada',   'Combinar lechuga, pollo, parmesano y aliño. Mezclar bien antes de servir');

-- Recipe 8: Tuna Salad Lunchbox (en)
INSERT INTO `recipe_step_translations` (`step_id`, `locale`, `title`, `description`) VALUES
(21, 'en', 'Prepare base',  'Wash and chop lettuce into pieces'),
(22, 'en', 'Add tuna',      'Drain canned tuna and add to the lettuce'),
(23, 'en', 'Season',        'Drizzle with olive oil, salt and a squeeze of lemon. Pack in lunchbox');

-- ========== Recipe ingredients ==========

-- Recipe 1: Pasta Carbonara
INSERT INTO `recipe_ingredients` (`quantity`, `notes`, `recipe_id`, `ingredient_id`, `measurement_unit_id`) VALUES
(200.00, NULL,                  1, 1,  1),   -- 200g spaghetti
(3.00,   'at room temperature', 1, 2,  7),   -- 3 eggs
(100.00, 'diced',               1, 3,  1),   -- 100g pancetta
(50.00,  'freshly grated',      1, 4,  1),   -- 50g parmesan
(1.00,   'to taste',            1, 5,  9),   -- 1 pinch black pepper
(1.00,   'to taste',            1, 6,  9);   -- 1 pinch salt

-- Recipe 2: Tomato and Basil Pasta
INSERT INTO `recipe_ingredients` (`quantity`, `notes`, `recipe_id`, `ingredient_id`, `measurement_unit_id`) VALUES
(320.00, NULL,       2, 1,  1),   -- 320g pasta
(400.00, 'chopped',  2, 10, 1),   -- 400g tomato
(2.00,   'minced',   2, 8,  7),   -- 2 cloves garlic
(2.00,   NULL,       2, 7,  6),   -- 2 tbsp olive oil
(1.00,   'to taste', 2, 6,  9);   -- salt

-- Recipe 4: Grilled Chicken
INSERT INTO `recipe_ingredients` (`quantity`, `notes`, `recipe_id`, `ingredient_id`, `measurement_unit_id`) VALUES
(300.00, NULL,       4, 11, 1),   -- 300g chicken breast
(1.00,   'juiced',   4, 13, 7),   -- 1 lemon
(2.00,   NULL,       4, 7,  6),   -- 2 tbsp olive oil
(1.00,   'to taste', 4, 6,  9),   -- salt
(1.00,   'to taste', 4, 5,  9);   -- black pepper

-- Recipe 5: Lunchbox Chicken Rice
INSERT INTO `recipe_ingredients` (`quantity`, `notes`, `recipe_id`, `ingredient_id`, `measurement_unit_id`) VALUES
(150.00, NULL,       5, 12, 1),   -- 150g rice
(200.00, NULL,       5, 11, 1),   -- 200g chicken breast
(1.00,   'to taste', 5, 6,  9),   -- salt
(1.00,   NULL,       5, 7,  6);   -- 1 tbsp olive oil

-- Recipe 6: Potato Omelette
INSERT INTO `recipe_ingredients` (`quantity`, `notes`, `recipe_id`, `ingredient_id`, `measurement_unit_id`) VALUES
(400.00, 'peeled and sliced', 6, 17, 1),   -- 400g potato
(4.00,   NULL,                6, 2,  7),   -- 4 eggs
(1.00,   'to taste',          6, 6,  9),   -- salt
(4.00,   NULL,                6, 7,  6);   -- 4 tbsp olive oil

-- Recipe 7: Caesar Salad
INSERT INTO `recipe_ingredients` (`quantity`, `notes`, `recipe_id`, `ingredient_id`, `measurement_unit_id`) VALUES
(200.00, NULL,             7, 20, 1),   -- 200g lettuce
(150.00, NULL,             7, 11, 1),   -- 150g chicken breast
(30.00,  'freshly grated', 7, 4,  1),   -- 30g parmesan
(1.00,   NULL,             7, 7,  6);   -- 1 tbsp olive oil

-- Recipe 8: Tuna Salad Lunchbox
INSERT INTO `recipe_ingredients` (`quantity`, `notes`, `recipe_id`, `ingredient_id`, `measurement_unit_id`) VALUES
(150.00, NULL,       8, 20, 1),   -- 150g lettuce
(160.00, 'drained',  8, 19, 1),   -- 160g tuna
(1.00,   'juiced',   8, 13, 7),   -- 1 lemon
(1.00,   'to taste', 8, 6,  9),   -- salt
(1.00,   NULL,       8, 7,  6);   -- 1 tbsp olive oil