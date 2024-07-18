-- Create the asset_history table with a foreign key reference to the asset table
CREATE TABLE IF NOT EXISTS `asset_history` (
                                               `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                                               `asset_id` BIGINT NOT NULL,
                                               `time` TIMESTAMP NOT NULL,
                                               `price` DECIMAL(19, 2) NOT NULL,
                                               CONSTRAINT `fk_asset_history_asset`
                                                   FOREIGN KEY (`asset_id`)
                                                       REFERENCES `asset` (`id`)
                                                       ON DELETE CASCADE
                                                       ON UPDATE CASCADE
);
