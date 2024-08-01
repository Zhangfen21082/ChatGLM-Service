# ************************************************************
# Sequel Ace SQL dump
# 版本号： 20050
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# 主机: 127.0.0.1 (MySQL 5.6.39)
# 数据库: openai
# 生成时间: 2023-10-05 23:19:49 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE database if NOT EXISTS `openai` default character set utf8mb4 collate utf8mb4_0900_ai_ci;
use `openai`;

# 转储表 openai_order
# ------------------------------------------------------------

DROP TABLE IF EXISTS `openai_order`;

CREATE TABLE `openai_order` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
                                `openid` varchar(128) NOT NULL COMMENT '用户ID；微信分配的唯一ID编码',
                                `product_id` int(4) NOT NULL COMMENT '商品ID',
                                `product_name` varchar(32) NOT NULL COMMENT '商品名称',
                                `product_quota` int(8) NOT NULL COMMENT '商品额度',
                                `product_model_types` varchar(128) DEFAULT NULL COMMENT '可用模型；glm-3.5-turbo,glm-4v,gpt-4,gpt-4-32k',
                                `order_id` varchar(12) NOT NULL COMMENT '订单编号',
                                `order_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
                                `order_status` tinyint(1) NOT NULL COMMENT '订单状态；0-创建完成、1-等待发货、2-发货完成、3-系统关单',
                                `total_amount` decimal(10,2) NOT NULL COMMENT '订单金额',
                                `pay_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '支付方式；0-微信支付',
                                `pay_url` varchar(128) DEFAULT NULL COMMENT '支付地址；创建支付后，获得的URL地址',
                                `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额；支付成功后，以回调信息更新金额',
                                `transaction_id` varchar(32) DEFAULT NULL COMMENT '交易单号；支付成功后，回调信息的交易单号',
                                `pay_status` tinyint(1) DEFAULT NULL COMMENT '支付状态；0-等待支付、1-支付完成、2-支付失败、3-放弃支付',
                                `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `openai_order` WRITE;
/*!40000 ALTER TABLE `openai_order` DISABLE KEYS */;

INSERT INTO `openai_order` (`id`, `openid`, `product_id`, `product_name`, `product_quota`, `product_model_types`, `order_id`, `order_time`, `order_status`, `total_amount`, `pay_type`, `pay_url`, `pay_amount`, `transaction_id`, `pay_status`, `pay_time`, `create_time`, `update_time`)
VALUES
    (1,'oqf316WfYVJjD0K_Klvq8fCqm7Hg',1001,'测试商品',100,NULL,'118845061424','2023-10-05 17:02:42',0,0.11,0,'weixin://wxpay/bizpayurl?pr=NFByxsizz',NULL,NULL,1,NULL,'2023-10-05 17:02:41','2023-10-05 20:08:29'),
    (2,'oqf316WfYVJjD0K_Klvq8fCqm7Hg',1001,'测试商品',100,NULL,'730807176035','2023-10-05 20:35:46',2,0.11,0,'weixin://wxpay/bizpayurl?pr=0ckFdM2zz',0.11,'4200001993202310054200967494',1,'2023-10-05 20:09:10','2023-10-05 20:08:46','2023-10-05 20:42:16'),
    (3,'oqf316WfYVJjD0K_Klvq8fCqm7Hg',1001,'测试商品',100,NULL,'957726718857','2023-10-05 21:10:37',0,0.11,0,'weixin://wxpay/bizpayurl?pr=5BWAAAezz',NULL,NULL,0,NULL,'2023-10-05 21:10:37','2023-10-05 21:10:37');

/*!40000 ALTER TABLE `openai_order` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 openai_product
# ------------------------------------------------------------

DROP TABLE IF EXISTS `openai_product`;

CREATE TABLE `openai_product` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
                                  `product_id` int(4) NOT NULL COMMENT '商品ID',
                                  `product_name` varchar(32) NOT NULL COMMENT '商品名称',
                                  `product_desc` varchar(128) NOT NULL COMMENT '商品描述',
                                  `product_model_types` varchar(128) DEFAULT NULL COMMENT '可用模型；glm-3.5-turbo,glm-4v,gpt-4,gpt-4-32k',
                                  `quota` int(8) NOT NULL COMMENT '额度次数',
                                  `price` decimal(10,2) NOT NULL COMMENT '商品价格',
                                  `sort` int(4) NOT NULL COMMENT '商品排序',
                                  `is_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效；0无效、1有效',
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_product_id_enabled` (`product_id`,`is_enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `openai_product` WRITE;
/*!40000 ALTER TABLE `openai_product` DISABLE KEYS */;

INSERT INTO `openai_product` (`id`, `product_id`, `product_name`, `product_desc`, `product_model_types`, `quota`, `price`, `sort`, `is_enabled`, `create_time`, `update_time`)
VALUES
    (1,1001,'OpenAi 测试商品(3.5)','测试商品请勿下单','glm-3-turbo',100,0.01,1,1,'2023-10-07 18:45:36','2023-10-07 18:45:36'),
	(2,1002,'OpenAi 测试商品(3.5)','测试商品请勿下单','glm-3-turbo',200,0.02,2,1,'2023-10-07 18:45:42','2023-10-07 18:45:42'),
	(3,1003,'OpenAi 测试商品(3.5&4.0)','测试商品请勿下单','glm-4v',50,10.00,3,1,'2023-10-07 18:46:41','2023-10-07 18:46:41'),
	(4,1004,'OpenAi 测试商品(3.5&4.0)','测试商品请勿下单','glm-4v',100,18.88,3,0,'2023-10-08 09:28:47','2023-10-08 09:28:47');

/*!40000 ALTER TABLE `openai_product` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 user_account
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_account`;

CREATE TABLE `user_account` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
                                `openid` varchar(64) NOT NULL COMMENT '用户ID；这里用的是微信ID作为唯一ID，你也可以给用户创建唯一ID，之后绑定微信ID',
                                `total_quota` int(11) NOT NULL DEFAULT '0' COMMENT '总量额度；分配的总使用次数',
                                `surplus_quota` int(11) NOT NULL DEFAULT '0' COMMENT '剩余额度；剩余的可使用次数',
                                `model_types` varchar(128) NOT NULL COMMENT '可用模型；glm-3.5-turbo,glm-4v,gpt-4,gpt-4-32k',
                                `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '账户状态；0-可用、1-冻结',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uq_openid` (`openid`),
                                KEY `idx_surplus_quota_status` (`surplus_quota`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `user_account` WRITE;
/*!40000 ALTER TABLE `user_account` DISABLE KEYS */;

INSERT INTO `user_account` (`id`, `openid`, `total_quota`, `surplus_quota`, `model_types`, `status`, `create_time`, `update_time`)
VALUES
    (1,'oqf316WfYVJjD0K_Klvq8fCqm7Hg',10,0,'glm-3-turbo,glm-4v',0,'2024-07-22 18:56:13','2024-7-22 19:50:23');

/*!40000 ALTER TABLE `user_account` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

