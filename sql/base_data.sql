/*
Navicat MySQL Data Transfer

Source Server         : 腾讯云
Source Server Version : 50729
Source Host           : 123.206.19.217:3306
Source Database       : base_data

Target Server Type    : MYSQL
Target Server Version : 50729
File Encoding         : 65001

Date: 2020-05-15 14:19:21
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `item_key` varchar(32) NOT NULL DEFAULT '',
  `item_value` varchar(32) NOT NULL DEFAULT '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_name` varchar(16) NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_name` varchar(16) NOT NULL DEFAULT '' COMMENT '修改人姓名',
  `del_state` tinyint(2) NOT NULL DEFAULT '0' COMMENT '删除状态 0 正常 1删除',
  `remarks` varchar(256) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;
