CREATE TABLE `trade_order` (
                               `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                               `symbol` VARCHAR(255) NOT NULL,
                               `quantity` DECIMAL(19, 4) NOT NULL,
                               `price` DECIMAL(19, 4),
                               `order_type` ENUM('BUY', 'SELL') NOT NULL,
                               `status` ENUM('PENDING', 'COMPLETED', 'CANCELLED') NOT NULL,
                               `order_time` DATETIME NOT NULL,
                               `user_id` BIGINT NOT NULL,
                               CONSTRAINT `FK_trade_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);