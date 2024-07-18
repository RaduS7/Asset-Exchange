CREATE TABLE `role` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `name` ENUM('TRADER', 'ADMIN') NULL
);

CREATE TABLE `users` (
                         `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                         `username` VARCHAR(20) NOT NULL,
                         `email` VARCHAR(50) NOT NULL,
                         `password` VARCHAR(120) NOT NULL,
                         CONSTRAINT `UKob8kqyqqgmefl0aco34akdtpe` UNIQUE (`email`),
                         CONSTRAINT `UKsb8bbouer5wak8vyiiy4pf2bx` UNIQUE (`username`)
);

CREATE TABLE `user_roles` (
                              `role_id` INT NOT NULL,
                              `user_id` BIGINT NOT NULL,
                              PRIMARY KEY (`role_id`, `user_id`),
                              CONSTRAINT `FK55itppkw3i07do3h7qoclqd4k` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
                              CONSTRAINT `FKrhfovtciq1l558cw6udg0h0d3` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
);