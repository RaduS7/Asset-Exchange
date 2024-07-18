CREATE TABLE `currency` (
                            `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                            `currency_code` VARCHAR(10) NOT NULL UNIQUE,
                            `rate_to_usd` DECIMAL(10, 6) NOT NULL
);