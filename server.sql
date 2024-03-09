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

 Date: 09/03/2024 20:41:11
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
-- Table structure for event
-- ----------------------------
DROP TABLE IF EXISTS `event`;
CREATE TABLE `event`  (
  `id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `time_start` datetime NULL DEFAULT NULL,
  `time_end` datetime NULL DEFAULT NULL,
  `note` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of event
-- ----------------------------
INSERT INTO `event` VALUES (1, 'LCK', '2024-03-09 15:00:00', '2024-03-09 17:00:08', 'T1 vo dich');

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
INSERT INTO `family` VALUES (1, 'Ri cha', 'siuu', 'b@gmail.com');
INSERT INTO `family` VALUES (2, NULL, NULL, 'a@gmail.com');

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
INSERT INTO `linking_family` VALUES (2, 4);
INSERT INTO `linking_family` VALUES (2, 6);

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
  `user_status` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `account_status` binary(255) NULL DEFAULT NULL,
  `last_login` datetime NULL DEFAULT NULL,
  `family_id` int(11) UNSIGNED NULL DEFAULT 0,
  `nickname` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_email`(`email`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'a@gmail.com', 'a', 'Lionel', 'Messi', '2024-01-01', '0123456789', NULL, NULL, NULL, '2024-03-09 20:39:23', 2, 'GOAT');
INSERT INTO `user` VALUES (2, 'b@gmail.com', 'siuu', 'Ri Cha', 'Kim', '2024-01-01', '0123456789', NULL, NULL, NULL, '2024-03-09 20:39:28', 1, NULL);
INSERT INTO `user` VALUES (3, 'c@gmail.com', 'b', 'Ri Cha', 'Kim', '2024-01-01', '0123456789', NULL, NULL, NULL, '2024-03-09 20:39:31', 2, NULL);
INSERT INTO `user` VALUES (4, 'd@gmail.com', 'Studyverse123', 'Cha', 'Ri', '2024-02-08', '0938469314', NULL, NULL, NULL, '2024-03-09 20:39:36', 0, NULL);
INSERT INTO `user` VALUES (5, 'abc@gmail.com', 'Studyverse123', 'Verse', 'Study', '2023-12-23', '0909294562', NULL, NULL, NULL, '2024-03-09 20:39:39', 0, NULL);
INSERT INTO `user` VALUES (6, 'cc@gmail.com', 'b', 'Ri Cha', 'Kim', '2024-01-01', '0123456789', NULL, NULL, NULL, '2024-03-09 20:39:43', 0, NULL);
INSERT INTO `user` VALUES (7, 'huudanhnguyen02@gmail.com', 'Camonvidaden2002', 'Danh', 'Nguyen', '2002-08-15', '0938469314', NULL, NULL, NULL, '2024-03-09 20:40:05', 0, NULL);

SET FOREIGN_KEY_CHECKS = 1;
