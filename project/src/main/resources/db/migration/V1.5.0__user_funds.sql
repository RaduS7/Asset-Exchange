CREATE TABLE `user_funds` (
                              `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                              `user_id` BIGINT NOT NULL,
                              `currency` VARCHAR(10) NOT NULL,
                              `total_amount` DECIMAL(19, 4) NOT NULL,
                              `available_amount` DECIMAL(19, 4) NOT NULL,
                              `pending_amount` DECIMAL(19, 4) NOT NULL,
                              CONSTRAINT `fk_user_funds_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);