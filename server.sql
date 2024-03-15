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

 Date: 15/03/2024 16:49:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for event
-- ----------------------------
DROP TABLE IF EXISTS `event`;
CREATE TABLE `event`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `time_start` datetime NULL DEFAULT NULL,
  `time_end` datetime NULL DEFAULT NULL,
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `user_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `event_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 124826 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of event
-- ----------------------------

-- ----------------------------
-- Table structure for family
-- ----------------------------
DROP TABLE IF EXISTS `family`;
CREATE TABLE `family`  (
  `id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

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

-- ----------------------------
-- Table structure for loop_event
-- ----------------------------
DROP TABLE IF EXISTS `loop_event`;
CREATE TABLE `loop_event`  (
  `first_event_id` int(11) NOT NULL,
  `last_event_id` int(11) NOT NULL,
  PRIMARY KEY (`first_event_id`, `last_event_id`) USING BTREE,
  INDEX `last_event_id`(`last_event_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of loop_event
-- ----------------------------

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `is_read` bit(1) NULL DEFAULT NULL,
  `sender_id` int(11) NULL DEFAULT NULL,
  `receiver_id` int(11) NULL DEFAULT NULL,
  `time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sender_id`(`sender_id`) USING BTREE,
  INDEX `receiver_id`(`receiver_id`) USING BTREE,
  CONSTRAINT `message_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `message_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, 'Hello', b'1', 1, 2, '2024-03-13 09:38:21');
INSERT INTO `message` VALUES (2, 'Bạn khoẻ không', b'1', 1, 2, '2024-03-13 09:38:41');
INSERT INTO `message` VALUES (3, 'Tôi khoẻ lắm', b'1', 2, 1, '2024-03-13 10:10:08');
INSERT INTO `message` VALUES (4, 'Tôi khoẻ lắm', b'1', 2, 1, '2024-03-13 15:56:32');
INSERT INTO `message` VALUES (5, 'Hello', b'0', 3, 1, '2024-03-13 16:05:20');
INSERT INTO `message` VALUES (6, 'T1 thua rồi', b'0', 3, 1, '2024-03-13 16:05:27');
INSERT INTO `message` VALUES (7, 'Buồn k ông cháu ơi', b'0', 3, 1, '2024-03-13 16:05:36');

-- ----------------------------
-- Table structure for remind_event
-- ----------------------------
DROP TABLE IF EXISTS `remind_event`;
CREATE TABLE `remind_event`  (
  `id` int(11) NOT NULL,
  `time` int(11) NULL DEFAULT NULL,
  `is_success` int(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `remind_event_ibfk_1` FOREIGN KEY (`id`) REFERENCES `event` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of remind_event
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `firstname` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `lastname` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `dob` date NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `user_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `account_status` bit(1) NULL DEFAULT NULL,
  `last_login` datetime NULL DEFAULT NULL,
  `family_id` int(11) NULL DEFAULT 0,
  `nickname` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `role` bit(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_email`(`email`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'a@gmail.com', 'a', 'Lionel', 'Messi', '2024-01-01', '0123456789', NULL, 'T1 thua rồi', b'1', '2024-03-10 09:22:48', 2, 'Mẹ đom dóm', b'1');
INSERT INTO `user` VALUES (2, 'b@gmail.com', 'siuu', 'Ri Cha', 'Kim', '2024-01-01', '0123456789', NULL, 'Đang học anh văn', b'1', '2024-03-09 20:39:28', 1, NULL, b'1');
INSERT INTO `user` VALUES (3, 'c@gmail.com', 'b', 'Ri Cha', 'Kim', '2024-01-01', '0123456789', NULL, 'Đang học anh văn', b'1', '2024-03-09 20:39:31', 0, 'Bé phô mai', b'0');
INSERT INTO `user` VALUES (4, 'd@gmail.com', 'Studyverse123', 'Cha', 'Ri', '2024-02-08', '0938469314', NULL, 'Đang học anh văn', b'1', '2024-03-09 20:39:36', 0, NULL, b'1');
INSERT INTO `user` VALUES (5, 'abc@gmail.com', 'Studyverse123', 'Verse', 'Study', '2023-12-23', '0909294562', NULL, 'Đang học anh văn', b'0', '2024-03-09 20:39:39', 0, NULL, b'1');
INSERT INTO `user` VALUES (6, 'cc@gmail.com', 'b', 'Ri Cha', 'Kim', '2024-01-01', '0123456789', NULL, 'Đang học anh văn', b'0', '2024-03-09 20:39:43', 0, NULL, b'0');
INSERT INTO `user` VALUES (7, 'huudanhnguyen02@gmail.com', 'Camonvidaden2002', 'Danh', 'Nguyen', '2002-08-15', '0938469314', NULL, 'Đang học anh văn', b'0', '2024-03-09 20:40:05', 0, NULL, b'1');
INSERT INTO `user` VALUES (8, 'eeee@gmail.com', 'b', 'Ri Cha', 'Kim', '2024-01-01', NULL, NULL, NULL, b'1', NULL, 0, 'Ri Cha Kim', b'0');

SET FOREIGN_KEY_CHECKS = 1;
