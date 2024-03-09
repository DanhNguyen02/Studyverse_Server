/*
 Navicat Premium Data Transfer

 Source Server         : Studyverse
 Source Server Type    : MySQL
 Source Server Version : 50742
 Source Host           : localhost:3306
 Source Schema         : server

 Target Server Type    : MySQL
 Target Server Version : 50742
 File Encoding         : 65001

 Date: 08/03/2024 15:37:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for children
-- ----------------------------
DROP TABLE IF EXISTS `children`;
CREATE TABLE `children`  (
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of children
-- ----------------------------
INSERT INTO `children` VALUES (3);
INSERT INTO `children` VALUES (6);

-- ----------------------------
-- Table structure for family
-- ----------------------------
DROP TABLE IF EXISTS `family`;
CREATE TABLE `family`  (
  `id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of family
-- ----------------------------
INSERT INTO `family` VALUES (1, NULL, NULL, 'b@gmail.com');

-- ----------------------------
-- Table structure for linking_family
-- ----------------------------
DROP TABLE IF EXISTS `linking_family`;
CREATE TABLE `linking_family`  (
  `family_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`family_id`, `user_id`) USING BTREE,
  INDEX `user`(`user_id`) USING BTREE,
  CONSTRAINT `family` FOREIGN KEY (`family_id`) REFERENCES `family` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of linking_family
-- ----------------------------

-- ----------------------------
-- Table structure for parent
-- ----------------------------
DROP TABLE IF EXISTS `parent`;
CREATE TABLE `parent`  (
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of parent
-- ----------------------------
INSERT INTO `parent` VALUES (1);
INSERT INTO `parent` VALUES (2);
INSERT INTO `parent` VALUES (4);
INSERT INTO `parent` VALUES (5);
INSERT INTO `parent` VALUES (7);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL,
  `email` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `firstname` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `lastname` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `dob` date NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `user_status` binary(255) NULL DEFAULT NULL,
  `account_status` binary(255) NULL DEFAULT NULL,
  `last_login` datetime NULL DEFAULT NULL,
  `family_id` int(11) UNSIGNED NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_email`(`email`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'a@gmail.com', 'a', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0);
INSERT INTO `user` VALUES (2, 'b@gmail.com', 'siuu', 'Ri Cha', 'Kim', '2024-01-01', '0123456789', NULL, NULL, NULL, NULL, 1);
INSERT INTO `user` VALUES (3, 'c@gmail.com', 'b', 'Ri Cha', 'Kim', '2024-01-01', '0123456789', NULL, NULL, NULL, NULL, 0);
INSERT INTO `user` VALUES (4, 'd@gmail.com', 'Studyverse123', 'Cha', 'Ri', '2024-02-08', '0938469314', NULL, NULL, NULL, NULL, 0);
INSERT INTO `user` VALUES (5, 'abc@gmail.com', 'Studyverse123', 'Verse', 'Study', '2023-12-23', '0909294562', NULL, NULL, NULL, NULL, 0);
INSERT INTO `user` VALUES (6, 'cc@gmail.com', 'b', 'Ri Cha', 'Kim', '2024-01-01', '0123456789', NULL, NULL, NULL, NULL, 0);
INSERT INTO `user` VALUES (7, 'huudanhnguyen02@gmail.com', 'Camonvidaden2002', 'Danh', 'Nguyen', '2002-08-15', '0938469314', NULL, NULL, NULL, NULL, 0);

SET FOREIGN_KEY_CHECKS = 1;
