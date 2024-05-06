/*
 Navicat Premium Data Transfer

 Source Server         : mysql-5-7
 Source Server Type    : MySQL
 Source Server Version : 50741
 Source Host           : localhost:13306
 Source Schema         : dpe_flowable

 Target Server Type    : MySQL
 Target Server Version : 50741
 File Encoding         : 65001

 Date: 10/05/2023 17:01:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user_flow_key
-- ----------------------------
DROP TABLE IF EXISTS `user_flow_key`;
CREATE TABLE `user_flow_key`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `flow_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流程任务key',
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程任务节点key预置' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
