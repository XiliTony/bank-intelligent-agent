/*
 Navicat Premium Data Transfer

 Source Server         : conn-localhost
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : localhost:3306
 Source Schema         : bank_agent

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 21/12/2025 22:29:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for persistent_chat
-- ----------------------------
DROP TABLE IF EXISTS `persistent_chat`;
CREATE TABLE `persistent_chat`  (
  `id` int NOT NULL COMMENT '会话id，主键',
  `jsonContent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '聊天记忆',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
