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

 Date: 10/05/2023 17:01:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户密码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称',
  `org_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属公司',
  `dept_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属部门',
  `position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '职位',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1D91701CDF5E11ED94EA00E04C360EE7', 'yewucheng', '123', '程职员', '信息技术有限公司', '业务', '职员');
INSERT INTO `user` VALUES ('21010838BC2911EDB56900FF8C62F6AG', 'fir', '123', '杉', '总公司', '业务', '超级管理员');
INSERT INTO `user` VALUES ('22C1A349DF5E11ED94EA00E04C360EE7', 'yewuji', '123', '冀经理', '信息技术有限公司', '业务', '经理');
INSERT INTO `user` VALUES ('23AA83F2DF5E11ED94EA00E04C360EE7', 'ywuwang', '123', '汪主管', '信息技术有限公司', '业务', '主管');
INSERT INTO `user` VALUES ('31306E88DF6111ED94EA00E04C360EE7', 'houqinqian', '123', '钱职员', '信息技术有限公司', '后勤', '职员');
INSERT INTO `user` VALUES ('5586E944DF5F11ED94EA00E04C360EE7', 'fawuxue', '123', '薛职员', '信息技术有限公司', '法务', '职员');
INSERT INTO `user` VALUES ('598B7EEDDF5B11ED94EA00E04C360EE7', 'jishuwang', '123', '王主管', '信息技术有限公司', '技术', '主管');
INSERT INTO `user` VALUES ('5996E7FAC49511EDBAF36C4B90076640', 'jishuzhai', '123', '翟经理', '信息技术有限公司', '技术', '经理');
INSERT INTO `user` VALUES ('5A12B37BDF5F11ED94EA00E04C360EE7', 'fawumeng', '123', '孟主管', '信息技术有限公司', '法务', '主管');
INSERT INTO `user` VALUES ('5C314C4EDF5F11ED94EA00E04C360EE7', 'fawusun', '123', '孙经理', '信息技术有限公司', '法务', '经理');
INSERT INTO `user` VALUES ('84AE9495DF5B11ED94EA00E04C360EE7', 'jishuliu', '123', '刘工', '信息技术有限公司', '技术', '职员');
INSERT INTO `user` VALUES ('8C804C71C49511EDBAF36C4B90076640', 'zhuguanye', '123', '叶主管', '信息技术有限公司', '行政', '主管');
INSERT INTO `user` VALUES ('CD854109DF5D11ED94EA00E04C360EE7', 'xingzhengzhang', '123', '张经理', '信息技术有限公司', '行政', '经理');
INSERT INTO `user` VALUES ('E490807FDF5D11ED94EA00E04C360EE7', 'xingzhengsong', '123', '宋职员', '信息技术有限公司', '行政', '职员');
INSERT INTO `user` VALUES ('FA5BDE0DDF6011ED94EA00E04C360EE7', 'danganzhou', '123', '周主任', '信息技术有限公司', '业务', '档案');

-- ----------------------------
-- Triggers structure for table user
-- ----------------------------
DROP TRIGGER IF EXISTS `id_user`;
delimiter ;;
CREATE TRIGGER `id_user` BEFORE INSERT ON `user` FOR EACH ROW BEGIN
		SET new.user_id=UPPER(REPLACE(UUID(), '-', '')); -- 触发器执行的逻辑
    END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
