CREATE TABLE `user_assets` (
                               `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                               `user_id` BIGINT NOT NULL,
                               `asset_id` BIGINT NOT NULL,
                               `quantity` DECIMAL(19, 4) NOT NULL,
                               CONSTRAINT `fk_user_assets_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                               CONSTRAINT `fk_user_assets_asset` FOREIGN KEY (`asset_id`) REFERENCES `asset` (`id`) ON DELETE CASCADE
);