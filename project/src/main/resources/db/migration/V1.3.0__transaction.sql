CREATE TABLE `transaction` (
                               `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                               `trade_order_id` BIGINT NOT NULL,
                               `executed_price` DECIMAL(19, 4) NOT NULL,
                               `quantity` DECIMAL(19, 4) NOT NULL,
                               `transaction_time` DATETIME NOT NULL,
                               CONSTRAINT `fk_trade_order` FOREIGN KEY (`trade_order_id`) REFERENCES `trade_order` (`id`) ON DELETE CASCADE
);