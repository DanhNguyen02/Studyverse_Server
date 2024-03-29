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

 Date: 29/03/2024 21:53:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for children_do_test
-- ----------------------------
DROP TABLE IF EXISTS `children_do_test`;
CREATE TABLE `children_do_test`  (
  `children_id` int(11) NOT NULL,
  `test_id` int(11) NOT NULL,
  PRIMARY KEY (`children_id`, `test_id`) USING BTREE,
  INDEX `test_id`(`test_id`) USING BTREE,
  CONSTRAINT `children_do_test_ibfk_1` FOREIGN KEY (`children_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `children_do_test_ibfk_2` FOREIGN KEY (`test_id`) REFERENCES `test` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of children_do_test
-- ----------------------------

-- ----------------------------
-- Table structure for choice
-- ----------------------------
DROP TABLE IF EXISTS `choice`;
CREATE TABLE `choice`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `image` blob NULL,
  `question_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `question_id`(`question_id`) USING BTREE,
  CONSTRAINT `choice_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 75 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of choice
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 124869 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

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
INSERT INTO `family` VALUES (1, 'Gia đình', NULL, 'b@gmail.com');
INSERT INTO `family` VALUES (2, 'Gia đình', NULL, 'a@gmail.com');

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
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `suggest` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `image` blob NULL,
  `answer_id` int(11) NULL DEFAULT NULL,
  `type` int(11) NULL DEFAULT NULL,
  `test_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `answer_id`(`answer_id`) USING BTREE,
  INDEX `test_id`(`test_id`) USING BTREE,
  CONSTRAINT `question_ibfk_2` FOREIGN KEY (`test_id`) REFERENCES `test` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------

-- ----------------------------
-- Table structure for question_have_tag
-- ----------------------------
DROP TABLE IF EXISTS `question_have_tag`;
CREATE TABLE `question_have_tag`  (
  `question_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL,
  PRIMARY KEY (`question_id`, `tag_id`) USING BTREE,
  INDEX `tag_id`(`tag_id`) USING BTREE,
  CONSTRAINT `question_have_tag_ibfk_1` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `question_have_tag_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_have_tag
-- ----------------------------

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
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, 'Toán');
INSERT INTO `tag` VALUES (2, 'Ngữ văn');
INSERT INTO `tag` VALUES (3, 'Tiếng anh');
INSERT INTO `tag` VALUES (4, 'Vật lý');
INSERT INTO `tag` VALUES (5, 'Hoá học');
INSERT INTO `tag` VALUES (6, 'Sinh học');
INSERT INTO `tag` VALUES (7, 'Lịch sử');
INSERT INTO `tag` VALUES (8, 'Địa lý');
INSERT INTO `tag` VALUES (9, 'Âm nhạc');
INSERT INTO `tag` VALUES (10, 'GDCD');
INSERT INTO `tag` VALUES (11, 'Thể dục');
INSERT INTO `tag` VALUES (12, 'GDQP');
INSERT INTO `tag` VALUES (13, 'Daily');
INSERT INTO `tag` VALUES (14, 'Exam');

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `time` int(11) NULL DEFAULT NULL,
  `question_count` int(11) NULL DEFAULT NULL,
  `question_count_to_pass` int(11) NULL DEFAULT NULL,
  `parent_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `parent_id`(`parent_id`) USING BTREE,
  CONSTRAINT `test_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test
-- ----------------------------

-- ----------------------------
-- Table structure for test_have_tag
-- ----------------------------
DROP TABLE IF EXISTS `test_have_tag`;
CREATE TABLE `test_have_tag`  (
  `test_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL,
  PRIMARY KEY (`tag_id`, `test_id`) USING BTREE,
  INDEX `test_id`(`test_id`) USING BTREE,
  CONSTRAINT `test_have_tag_ibfk_1` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `test_have_tag_ibfk_2` FOREIGN KEY (`test_id`) REFERENCES `test` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_have_tag
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
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT '1',
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
INSERT INTO `user` VALUES (1, 'a@gmail.com', 'a', 'Lionel', 'Messi', '1991-06-05', '0123456789', '1', 'T1 lại win', b'1', '2024-03-10 09:22:48', 2, 'BunnyOwO', b'1');
INSERT INTO `user` VALUES (2, 'b@gmail.com', 'siuu', 'Ri Cha', 'Kim', '2024-01-01', '0123456789', '3', 'Đang học anh văn', b'1', '2024-03-09 20:39:28', 1, NULL, b'1');
INSERT INTO `user` VALUES (3, 'c@gmail.com', 'b', 'Ri Cha', 'Kim', '2024-01-01', '0123456789', '4', 'Đọc báo', b'1', '2024-03-09 20:39:31', 2, 'Bố', b'1');
INSERT INTO `user` VALUES (4, 'd@gmail.com', 'Studyverse123', 'Cha', 'Ri', '2024-02-08', '0938469314', '3', 'Đang học anh văn', b'1', '2024-03-09 20:39:36', 2, 'Anh hai', b'0');
INSERT INTO `user` VALUES (5, 'abc@gmail.com', 'Studyverse123', 'Verse', 'Study', '2023-12-23', '0909294562', '2', 'Đang học anh văn', b'0', '2024-03-09 20:39:39', 2, 'Jenny', b'0');
INSERT INTO `user` VALUES (6, 'cc@gmail.com', 'b', 'Ri Cha', 'Kim', '2024-01-01', '0123456789', '1', 'Đang học anh văn', b'0', '2024-03-09 20:39:43', 0, NULL, b'0');
INSERT INTO `user` VALUES (7, 'huudanhnguyen02@gmail.com', 'Camonvidaden2002', 'Danh', 'Nguyen', '2002-08-15', '0938469314', '1', 'Đang học anh văn', b'0', '2024-03-09 20:40:05', 0, NULL, b'1');
INSERT INTO `user` VALUES (8, 'eeee@gmail.com', 'b', 'Ri Cha', 'Kim', '2024-01-01', NULL, '1', NULL, b'1', NULL, 0, 'Ri Cha Kim', b'0');

-- ----------------------------
-- Table structure for user_involve_event
-- ----------------------------
DROP TABLE IF EXISTS `user_involve_event`;
CREATE TABLE `user_involve_event`  (
  `event_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`event_id`, `user_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `user_involve_event_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_involve_event_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_involve_event
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
