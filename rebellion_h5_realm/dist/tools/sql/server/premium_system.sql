DROP TABLE IF EXISTS `premium_system`;
CREATE TABLE `premium_system` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `char_id` varchar(45) NOT NULL DEFAULT '',
 `bonus_id` int(2) NOT NULL DEFAULT '0',
 `bonus_expire` bigint(20) NOT NULL DEFAULT '0',
 `active` char(1) NOT NULL DEFAULT '1',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;