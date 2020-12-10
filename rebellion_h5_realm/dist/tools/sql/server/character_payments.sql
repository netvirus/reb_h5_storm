DROP TABLE IF EXISTS `character_payments`;
CREATE TABLE IF NOT EXISTS `character_payments` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `char_name` varchar(16) NOT NULL,
    `amount` int(5) NOT NULL,
    `pay_data` int(11) NOT NULL DEFAULT '0',
    `status` int(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;