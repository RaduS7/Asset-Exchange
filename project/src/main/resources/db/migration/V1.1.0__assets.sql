CREATE TABLE IF NOT EXISTS `asset` (
                                       `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       `asset_type` VARCHAR(255) NOT NULL,
                                       `current_price` DECIMAL(19, 2) NOT NULL,
                                       `name` VARCHAR(255) NOT NULL,
                                       `symbol` VARCHAR(255) NOT NULL UNIQUE
);