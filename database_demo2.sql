-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: localhost    Database: gym_management
-- ------------------------------------------------------
-- Server version	8.0.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (7,'Quản lý tài khoản','Quản lý thông tin người dùng, phân quyền và tài khoản hệ thống'),(8,'Quản lý huấn luyện viên','Quản lý thông tin và lịch làm việc của huấn luyện viên'),(9,'Quản lý lớp tập','Quản lý lịch học, đăng ký và thông tin lớp tập'),(10,'Quản lý gói tập','Quản lý các gói tập và dịch vụ của phòng gym'),(11,'Quản lý thanh toán','Theo dõi và quản lý các khoản thanh toán'),(12,'Báo cáo thống kê','Xem báo cáo và thống kê hoạt động phòng gym'),(13,'Quản lý thiết bị','Quản lý trang thiết bị phòng gym');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category2`
--

DROP TABLE IF EXISTS `category2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category2` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text,
  `path` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category2`
--

LOCK TABLES `category2` WRITE;
/*!40000 ALTER TABLE `category2` DISABLE KEYS */;
INSERT INTO `category2` VALUES (1,'Trang chủ','Trang chủ và dashboard chính','/home','2025-08-14 18:24:05.000000','2025-08-16 23:39:13.000000'),(2,'Hồ sơ cá nhân','Quản lý thông tin cá nhân và tiến trình','/profile','2025-08-14 18:24:05.000000','2025-08-16 23:39:13.000000'),(3,'Bài tập','Ghi log bài tập và lịch sử tập luyện','/workout','2025-08-14 18:24:05.000000','2025-08-16 23:39:13.000000'),(4,'Dinh dưỡng','Chế độ ăn và gợi ý thực đơn','/diet','2025-08-14 18:24:05.000000','2025-08-16 23:39:13.000000'),(5,'Lớp tập','Đăng ký và quản lý các lớp tập','/classes','2025-08-14 18:24:05.000000','2025-08-16 23:39:13.000000'),(6,'Chat PT','Tương tác với Personal Trainer','/chat-pt','2025-08-14 18:24:05.000000','2025-08-16 23:39:13.000000'),(7,'Gói tập','Các gói đăng kí với phòng','/package',NULL,NULL);
/*!40000 ALTER TABLE `category2` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class_enrollments`
--

DROP TABLE IF EXISTS `class_enrollments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_enrollments` (
  `enrollment_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `class_id` int NOT NULL,
  `joined_at` datetime(6) DEFAULT NULL,
  `attendance` tinyint(1) DEFAULT '0',
  `check_in_time` datetime(6) DEFAULT NULL,
  `status` enum('CANCELLED','COMPLETED','ENROLLED') NOT NULL,
  `is_deleted` int DEFAULT NULL,
  PRIMARY KEY (`enrollment_id`),
  KEY `class_id` (`class_id`),
  KEY `idx_user_class` (`user_id`,`class_id`),
  CONSTRAINT `class_enrollments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT,
  CONSTRAINT `class_enrollments_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `training_classes` (`class_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_enrollments`
--

LOCK TABLES `class_enrollments` WRITE;
/*!40000 ALTER TABLE `class_enrollments` DISABLE KEYS */;
INSERT INTO `class_enrollments` VALUES (9,73,3,'2025-08-24 03:42:13.634000',0,NULL,'ENROLLED',0),(10,73,4,'2025-08-29 08:00:35.924000',0,NULL,'ENROLLED',0),(11,70,3,'2025-08-29 08:05:37.928000',0,NULL,'ENROLLED',0),(12,70,4,'2025-08-29 08:09:45.888000',0,NULL,'ENROLLED',0),(13,73,5,'2025-08-29 08:18:52.353000',0,NULL,'ENROLLED',0),(14,70,8,'2025-08-29 08:30:46.558000',0,NULL,'ENROLLED',0),(15,73,8,'2025-08-30 15:46:40.289000',0,NULL,'ENROLLED',0);
/*!40000 ALTER TABLE `class_enrollments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `devices`
--

DROP TABLE IF EXISTS `devices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `devices` (
  `device_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `status` enum('AVAILABLE','BROKEN','IN_USE','MAINTENANCE') NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `maintenance_date` date DEFAULT NULL,
  `last_service_date` date DEFAULT NULL,
  `notes` text,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_deleted` int DEFAULT NULL,
  `image` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`device_id`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `devices`
--

LOCK TABLES `devices` WRITE;
/*!40000 ALTER TABLE `devices` DISABLE KEYS */;
INSERT INTO `devices` VALUES (3,'Máy chạy bộ #1f','Cardio','IN_USE','Tầng 1 - Khu Cardio','2025-12-01','2025-06-01','Hoạt động tốt nhak','2025-07-01 08:00:00.000000','2025-08-17 08:11:07.000000',0,'https://res.cloudinary.com/dd6b0cj7l/image/upload/v1754584934/gym_devices/nqzaaia2obql1gfoifje.jpg'),(4,'Tạ đòn #1','Strength','AVAILABLE','Tầng 2 - Khu tạ',NULL,'2025-05-01','Mới bảo trì','2025-07-01 08:00:00.000000','2025-07-01 08:00:00.000000',0,NULL),(5,'Thảm yoga #1','Accessory','AVAILABLE','Tầng 3 - Phòng lớp tập',NULL,NULL,'Hỏng, cần thay mới','2025-07-01 08:00:00.000000','2025-07-28 18:35:58.970514',1,NULL),(6,'máy chạy bộ ABC','Cardio','AVAILABLE','Tầng 3 - Phòng lớp tập',NULL,NULL,'','2025-08-01 18:51:33.782600','2025-08-11 16:47:59.995602',1,NULL),(7,'rử','thongke','IN_USE','rwrwrrwe','2025-08-20',NULL,'','2025-08-11 17:03:15.000000','2025-08-11 17:03:34.000000',0,'https://res.cloudinary.com/dd6b0cj7l/image/upload/v1754931812/gym_devices/vbgyyrhxdsev1xrsi7cn.jpg'),(8,'ghế','ghế d','AVAILABLE','tâng b',NULL,NULL,'','2025-08-29 08:31:33.000000','2025-08-29 08:33:29.000000',1,'https://res.cloudinary.com/dd6b0cj7l/image/upload/v1756456305/gym_devices/cjzsl7lrksg3dbkb7ybe.png');
/*!40000 ALTER TABLE `devices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gym_checkins`
--

DROP TABLE IF EXISTS `gym_checkins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gym_checkins` (
  `checkin_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `checkin_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `checkout_time` timestamp NULL DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`checkin_id`),
  KEY `idx_user_checkin` (`user_id`,`checkin_time`),
  CONSTRAINT `gym_checkins_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gym_checkins`
--

LOCK TABLES `gym_checkins` WRITE;
/*!40000 ALTER TABLE `gym_checkins` DISABLE KEYS */;
INSERT INTO `gym_checkins` VALUES (1,8,'2025-07-20 02:00:00','2025-07-20 03:30:00',0),(2,9,'2025-06-15 01:00:00','2025-06-15 02:00:00',0);
/*!40000 ALTER TABLE `gym_checkins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_packages`
--

DROP TABLE IF EXISTS `membership_packages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_packages` (
  `package_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `duration_months` int NOT NULL,
  `price` double NOT NULL,
  `description` text,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`package_id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_packages`
--

LOCK TABLES `membership_packages` WRITE;
/*!40000 ALTER TABLE `membership_packages` DISABLE KEYS */;
INSERT INTO `membership_packages` VALUES (4,'Gói Cơ Bản',1,500000,'Gói tập cơ bản 1 tháng, sử dụng tất cả thiết bị nhafff','2025-07-01 08:00:00.000000','2025-08-29 08:33:54.000000',0),(5,'Gói Tiêu Chuẩn',3,1350000,'Gói tập 3 tháng, tiết kiệm 10%','2025-07-01 08:00:00.000000','2025-07-01 08:00:00.000000',0),(6,'Gói VIP',12,4800000,'Gói VIP 1 năm, bao gồm PT và lớp tập nhóm','2025-07-01 08:00:00.000000','2025-07-01 08:00:00.000000',0),(7,'Gói Cũ',1,450000,'Gói cơ bản cũ, đã ngừng cung cấp','2025-06-01 08:00:00.000000','2025-07-01 08:00:00.000000',1),(8,'fwef',3,1440000,'frfw','2025-08-11 16:52:27.052356','2025-08-11 16:52:42.778034',1),(9,'fwfw',6,10000,'','2025-08-11 16:52:52.726548','2025-08-11 23:54:51.799110',1),(10,'fwf',6,10000,'fwf','2025-08-11 23:55:09.025471','2025-08-11 23:55:09.025471',0),(11,'hrhtrhhrthtrhrh',3,50000000,'','2025-08-11 16:57:04.026252','2025-08-11 17:02:57.000000',1),(12,'ffffqaqfqaw',24,1000000,'rử','2025-08-11 17:02:44.000000','2025-08-29 08:35:33.000000',1);
/*!40000 ALTER TABLE `membership_packages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `momo_payments`
--

DROP TABLE IF EXISTS `momo_payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `momo_payments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `amount` double NOT NULL,
  `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  `is_deleted` tinyint(1) DEFAULT '0',
  `momo_message` varchar(255) DEFAULT NULL,
  `momo_request_id` varchar(255) DEFAULT NULL,
  `momo_response_time` varchar(255) DEFAULT NULL,
  `momo_result_code` varchar(255) DEFAULT NULL,
  `momo_transaction_id` varchar(255) DEFAULT NULL,
  `order_id` varchar(255) NOT NULL,
  `package_id` int NOT NULL,
  `status` enum('COMPLETED','EXPIRED','FAILED','PENDING') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_nuo7y87hxx5wdy67f6vmdf8uv` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `momo_payments`
--

LOCK TABLES `momo_payments` WRITE;
/*!40000 ALTER TABLE `momo_payments` DISABLE KEYS */;
INSERT INTO `momo_payments` VALUES (11,10000,'2025-09-03 14:47:32.117000',0,'Test thanh toán thành công','REQ_1756910900155','1756910900155','0','DIRECT_TEST_1756910900154','MOMO_1756910852117',10,'COMPLETED','2025-09-03 14:48:20.157000',73);
/*!40000 ALTER TABLE `momo_payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_tokens` (
  `id` int NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `used` bit(1) NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_71lqwbwtklmljk3qlsugr1mig` (`token`),
  UNIQUE KEY `UK_la2ts67g4oh2sreayswhox1i6` (`user_id`),
  UNIQUE KEY `UKla2ts67g4oh2sreayswhox1i6` (`user_id`),
  CONSTRAINT `FKk3ndxg5xp6v7wd4gjyusp15gq` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_tokens`
--

LOCK TABLES `password_reset_tokens` WRITE;
/*!40000 ALTER TABLE `password_reset_tokens` DISABLE KEYS */;
INSERT INTO `password_reset_tokens` VALUES (41,'2025-08-11 09:40:41.847856','be2856a4-d420-4add-ae50-a78daaba20ce',_binary '',70),(42,'2025-08-30 01:50:31.925597','42cf9764-e3d0-4529-9a4e-2ef1bdc23f6b',_binary '\0',5);
/*!40000 ALTER TABLE `password_reset_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `membership_id` int NOT NULL,
  `amount` double NOT NULL,
  `payment_method` enum('CARD','CASH','TRANSFER') DEFAULT NULL,
  `payment_status` enum('COMPLETED','FAILED','PENDING') DEFAULT NULL,
  `payment_date` datetime(6) DEFAULT NULL,
  `notes` text,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`payment_id`),
  KEY `idx_membership_date` (`membership_id`,`payment_date`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`membership_id`) REFERENCES `user_memberships` (`membership_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,3,500000,'CASH','COMPLETED','2025-07-01 09:00:00.000000','Thanh toán gói cơ bản',0),(2,4,1350000,'CARD','COMPLETED','2025-06-01 10:00:00.000000','Thanh toán gói tiêu chuẩn',1),(7,9,10000,'CARD','COMPLETED','2025-09-03 14:48:20.193000','Test thanh toán trực tiếp - Order ID: MOMO_1756910852117',0),(8,10,500000,'CASH','COMPLETED','2025-09-03 15:05:51.997000','',0),(9,11,4800000,'CASH','COMPLETED','2025-09-03 15:17:19.908000','',0);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pt_assignments`
--

DROP TABLE IF EXISTS `pt_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pt_assignments` (
  `assignment_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `trainer_id` int DEFAULT NULL,
  `status` enum('ACTIVE','ASSIGNED','CANCELLED','COMPLETED','PENDING') NOT NULL,
  `request_notes` text,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_deleted` int DEFAULT '0',
  PRIMARY KEY (`assignment_id`),
  KEY `idx_user_status` (`user_id`,`status`),
  KEY `idx_trainer_status` (`trainer_id`,`status`),
  CONSTRAINT `pt_assignments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `pt_assignments_ibfk_2` FOREIGN KEY (`trainer_id`) REFERENCES `trainers` (`trainer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pt_assignments`
--

LOCK TABLES `pt_assignments` WRITE;
/*!40000 ALTER TABLE `pt_assignments` DISABLE KEYS */;
INSERT INTO `pt_assignments` VALUES (12,73,71,'ACTIVE','fsdfs','2025-08-29 09:05:04.410000','2025-08-29 09:06:15.679000',0);
/*!40000 ALTER TABLE `pt_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trainers`
--

DROP TABLE IF EXISTS `trainers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trainers` (
  `trainer_id` int NOT NULL,
  `specialization` varchar(255) DEFAULT NULL,
  `schedule` text,
  `is_deleted` int DEFAULT NULL,
  `experience` varchar(255) DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`trainer_id`),
  UNIQUE KEY `UK_se8qmhomor3guutjui5wfmrk3` (`user_id`),
  CONSTRAINT `FKmkxcvfr0uu3pwv772aurye5w7` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `trainers_ibfk_1` FOREIGN KEY (`trainer_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trainers`
--

LOCK TABLES `trainers` WRITE;
/*!40000 ALTER TABLE `trainers` DISABLE KEYS */;
INSERT INTO `trainers` VALUES (6,'Tập sức mạnhh','Thứ 2,4,6: 08:00-12:00, 17:00-20:00',1,NULL,NULL),(7,'Yoga và Cardio','Thứ 3,5,7: 07:00-11:00, 18:00-21:00',0,NULL,NULL),(71,'ssf','',0,NULL,NULL),(74,'cardio','',0,NULL,NULL),(76,'strength','',0,NULL,NULL),(80,'calis','',NULL,NULL,NULL),(85,'fff','',0,NULL,NULL),(86,'s','',0,NULL,NULL),(87,NULL,NULL,1,NULL,NULL),(88,'fwfw','fwf',0,NULL,NULL),(90,NULL,NULL,NULL,NULL,NULL),(92,'alo','',0,NULL,NULL),(93,'','',1,NULL,NULL);
/*!40000 ALTER TABLE `trainers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_classes`
--

DROP TABLE IF EXISTS `training_classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_classes` (
  `class_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text,
  `trainer_id` int DEFAULT NULL,
  `schedule` text,
  `max_participants` int DEFAULT NULL,
  `price` double DEFAULT NULL,
  `duration_minutes` int DEFAULT '60',
  `start_time` varchar(255) DEFAULT NULL,
  `days_of_week` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_deleted` int DEFAULT NULL,
  PRIMARY KEY (`class_id`),
  KEY `trainer_id` (`trainer_id`),
  KEY `idx_name` (`name`),
  CONSTRAINT `training_classes_ibfk_1` FOREIGN KEY (`trainer_id`) REFERENCES `trainers` (`trainer_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_classes`
--

LOCK TABLES `training_classes` WRITE;
/*!40000 ALTER TABLE `training_classes` DISABLE KEYS */;
INSERT INTO `training_classes` VALUES (3,'Lớp Yoga Cơ Bản','Lớp yoga cho người mới bắt đầu',7,'Thứ 3,5: 18:00-19:00',50,200000,60,'18:00:00.000000','3,5','2025-07-01 08:00:00.000000','2025-07-01 08:00:00.000000',0),(4,'Lớp Cardio Nâng Cao','Lớp cardio cường độ cao',86,'Thứ 2,4: 19:00-20:00',15,250000,60,'19:00:00.000000','2,4','2025-07-01 08:00:00.000000','2025-08-09 03:47:20.256000',0),(5,'cfsdfsdf','fsdf',85,'3',10,0,60,'00:00','3','2025-08-18 13:30:33.301000','2025-08-18 13:30:33.301000',0),(6,'dsfsa','',88,'',NULL,0,60,'00:00',NULL,'2025-08-24 02:59:04.398000','2025-08-24 03:42:04.016000',1),(7,'123132','',71,'',NULL,0,60,'00:00',NULL,'2025-08-24 03:06:16.165000','2025-08-24 03:42:01.931000',1),(8,'demo nhe','tập luyueenj',90,'t2,4 ,6',10,0,60,'00:00',NULL,'2025-08-29 08:28:06.845000','2025-08-29 08:30:20.684000',0);
/*!40000 ALTER TABLE `training_classes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_sessions`
--

DROP TABLE IF EXISTS `training_sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_sessions` (
  `session_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `trainer_id` int NOT NULL,
  `session_date` date NOT NULL,
  `start_time` time(6) NOT NULL,
  `end_time` time(6) NOT NULL,
  `status` enum('CANCELLED','COMPLETED','IN_PROGRESS','NO_SHOW','SCHEDULED') NOT NULL,
  `notes` text,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_deleted` int DEFAULT '0',
  PRIMARY KEY (`session_id`),
  KEY `idx_user_date` (`user_id`,`session_date`),
  KEY `idx_trainer_date` (`trainer_id`,`session_date`),
  CONSTRAINT `training_sessions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `training_sessions_ibfk_2` FOREIGN KEY (`trainer_id`) REFERENCES `trainers` (`trainer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_sessions`
--

LOCK TABLES `training_sessions` WRITE;
/*!40000 ALTER TABLE `training_sessions` DISABLE KEYS */;
INSERT INTO `training_sessions` VALUES (11,73,71,'2025-08-30','19:06:00.000000','20:06:00.000000','SCHEDULED','',NULL,NULL,0);
/*!40000 ALTER TABLE `training_sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_availabilities`
--

DROP TABLE IF EXISTS `user_availabilities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_availabilities` (
  `availability_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `day_of_week` enum('FRIDAY','MONDAY','SATURDAY','SUNDAY','THURSDAY','TUESDAY','WEDNESDAY') NOT NULL,
  `start_time` time(6) NOT NULL,
  `end_time` time(6) NOT NULL,
  `is_available` bit(1) NOT NULL DEFAULT b'1',
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_deleted` int DEFAULT '0',
  PRIMARY KEY (`availability_id`),
  KEY `idx_user_day` (`user_id`,`day_of_week`),
  CONSTRAINT `user_availabilities_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_availabilities`
--

LOCK TABLES `user_availabilities` WRITE;
/*!40000 ALTER TABLE `user_availabilities` DISABLE KEYS */;
INSERT INTO `user_availabilities` VALUES (15,73,'MONDAY','18:00:00.000000','19:00:00.000000',_binary '',NULL,NULL,0);
/*!40000 ALTER TABLE `user_availabilities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_memberships`
--

DROP TABLE IF EXISTS `user_memberships`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_memberships` (
  `membership_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `package_id` int DEFAULT NULL,
  `start_date` datetime(6) NOT NULL,
  `end_date` datetime(6) NOT NULL,
  `status` enum('ACTIVE','CANCELLED','EXPIRED') DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`membership_id`),
  KEY `package_id` (`package_id`),
  KEY `idx_user_status` (`user_id`,`status`),
  CONSTRAINT `user_memberships_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT,
  CONSTRAINT `user_memberships_ibfk_2` FOREIGN KEY (`package_id`) REFERENCES `membership_packages` (`package_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_memberships`
--

LOCK TABLES `user_memberships` WRITE;
/*!40000 ALTER TABLE `user_memberships` DISABLE KEYS */;
INSERT INTO `user_memberships` VALUES (3,8,4,'2025-07-01 00:00:00.000000','2025-09-30 00:00:00.000000','ACTIVE',0,NULL,'2025-09-03 17:04:48.028000'),(4,9,5,'2025-06-01 00:00:00.000000','2025-08-31 00:00:00.000000','EXPIRED',1,NULL,NULL),(9,73,10,'2025-09-03 14:48:20.177000','2026-03-03 14:48:20.177000','ACTIVE',0,'2025-09-03 14:48:20.184000','2025-09-03 14:48:20.184000'),(10,94,4,'2025-09-03 15:05:51.933000','2025-10-03 15:05:51.933000','ACTIVE',0,'2025-09-03 15:05:51.933000','2025-09-03 15:05:51.933000'),(11,9,6,'2025-09-03 15:17:19.901000','2026-09-03 15:17:19.901000','ACTIVE',0,'2025-09-03 15:17:19.901000','2025-09-03 15:17:19.901000');
/*!40000 ALTER TABLE `user_memberships` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `phone_number` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('ADMIN','PT','USER') NOT NULL,
  `name` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `height` double DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `fitness_goal` text,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_deleted` int NOT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone_number` (`phone_number`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone_number`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (5,'d68101@gmail.com','0123456789','$2y$10$DZHGB4q.8PAKd88urv5ZVu64Qlh1WjIUqo3IBYKOZY2J4sKNmn4ce','ADMIN','Nguyễn Văn Quản Trịf','Nam','1985-01-01',NULL,NULL,'','2025-07-01 08:00:00.000000','2025-08-29 12:39:07.931000',0,NULL),(6,'pt1@gym.com','0912345678','$2y$10$md6Xw.Gx4ZL4COlUjPMLr.Ro/b5fbFaTZPZL0jPD0CLc8KP.8lqnG','PT','Trần Văn Hùng','','1990-03-15',NULL,NULL,'','2025-07-01 08:00:00.000000','2025-08-17 07:10:16.593000',1,NULL),(7,'pt2@gym.com','0918765432','$2a$10$example.hash.here','PT','Lê Thị Maii','','1992-07-20',NULL,NULL,'','2025-07-01 08:00:00.000000','2025-08-01 17:41:30.337000',0,NULL),(8,'user1@gym.com','0987654321','$2a$10$example.hash.here','USER','Nguyễn Văn Anh','','1995-05-10',170,70,'Tăng cơ bắp','2025-07-01 08:00:00.000000','2025-07-28 10:25:26.815000',0,NULL),(9,'user2@gym.com','0971234567','$2a$10$example.hash.here','USER','Phạm Thị Bìnhh','','1998-11-25',160,55,'Giảm mỡ','2025-07-01 08:00:00.000000','2025-07-27 06:20:13.654000',0,NULL),(70,'danhvip83@gmail.com','0909817104','$2y$10$dYBFnVZMVo0rMJRJ21ZwsOp25aEfEbBZ8L.TlLSyCPJyxmL.KirDW','ADMIN','Đặng Hoàng Danh','Nam','0004-10-01',175,55.1,NULL,'2025-07-27 06:00:02.690000','2025-08-29 17:24:43.648000',0,'https://res.cloudinary.com/dd6b0cj7l/image/upload/v1756488282/user_avatars/u5ciz0gb8xmi3wewmc3p.png'),(71,'123@gmail.com','01232131231','$2y$10$rjzdWqbP9yHH9Bpe4u0ZH.a36f.ei5xlXMBCnWD66Jrgg2ELaOgly','PT','Bob','Nam','2000-07-15',NULL,NULL,'','2025-08-17 07:49:03.592000','2025-08-17 07:49:03.592000',0,NULL),(73,'111@gmail.com','0111111111','$2a$10$MEzJgS5FtsKFzKooV.fHW.uoK9S8JDYE8JFW3oRSeHGZ7hRmORYwW','USER','111','Nữ','2000-07-30',176,70,'tăng cơ giảm mỡ','2025-08-01 18:46:35.887000','2025-09-04 11:24:29.627000',0,'https://res.cloudinary.com/dd6b0cj7l/image/upload/v1756488335/user_avatars/tjt3lbaeh3nuumpv2wqo.png'),(74,'222@gmail.com','0222222222','$2y$10$tbvf9lI/IGbnljwATGtLVeK738sLcBRcHUCPfCG7EtfEiOjOGDAnO','PT','222','Nam','2000-11-05',178,77,'','2025-08-01 18:50:27.675000','2025-08-01 18:50:45.525000',0,NULL),(76,'333@gmail.com','0333333333','$2y$10$mis9OYAs2N00QddtwyLO3e/3SIIWoeQQ5WyDEykK6NdzvuyH4zkq2','PT','333','Nữ','2000-08-07',180,80,'','2025-08-03 02:35:07.608000','2025-08-03 02:35:07.608000',0,NULL),(77,'4444@email.com','0444444444','$2a$10$ajZEJfx13ItskQ2MjnR4lOC1VWL7bSrqeZKqBkSwt0i7yvPTA62tS','USER','4444','Nam','2000-01-20',175,55,'','2025-08-06 14:26:29.203000','2025-08-06 14:26:29.204000',1,NULL),(78,'555@email.com','0555555555','$2a$10$.1BUWyiM91bs/EdHs/yGmOxsRXwkhkmBW/00ZhAd5gV/qb5dimZYm','ADMIN','555','Nam','2000-02-02',150,NULL,'','2025-08-06 14:51:04.653000','2025-08-06 14:51:04.653000',0,NULL),(80,'666@email.com','0666666666','$2a$10$Ws3HWyXZ9qSGveJhzAVTWO6jB5i8uhkxdpqP62PyM7MnT5G.vIUFC','PT','666','Nữ','2000-01-01',NULL,NULL,'','2025-08-18 05:27:12.980000','2025-08-18 05:27:12.980000',0,NULL),(85,'fsdfsdf@email.com','03827263478','$2a$10$1U2Uwz8g0uSXt6rRKpCbl.l40Sl6RAocAY2E6sD/UQsqFJdkGxQqy','PT','fifai','',NULL,NULL,NULL,'','2025-08-17 06:51:52.544000','2025-08-17 06:51:52.544000',0,NULL),(86,'ttt@email.com','03426546323','$2a$10$LHGxfJ9mPju9rMuzd03yF.XgrRzr5jH1NOqzhLQc.19QNiSi/uRQC','PT','tt','',NULL,NULL,NULL,'suc mạh','2025-08-29 08:25:04.732000','2025-08-29 08:25:04.732000',0,NULL),(87,'fwff23@email.com','04623734839','$2a$10$nfzpGcccEU0ClN2N6DKFVuenuB9GGrRI8ZZ.P.t5qscMDDZPiMsUi','PT','fwfw','',NULL,NULL,NULL,'','2025-08-07 15:24:04.368000','2025-08-07 15:24:44.789000',1,NULL),(88,'v83@gmail.com','0909817103','$2a$10$6AQZlUA/dwXB9ENVDz7nve.o2tzaJvg2oKnWbIeuMP2xFOICUgHIO','PT','vvv','',NULL,NULL,NULL,'','2025-08-07 15:32:23.786000','2025-08-07 15:32:23.786000',0,NULL),(89,'hihi@email.com','0984766635','$2y$10$OUihAVKb.mQ/eXcsI7taCuOtUUnDN5mdaKbjqtvU18wB/HuHiJYZC','USER','hihi','MALE','2000-01-20',175,50,'MUSCLE_GAIN','2025-08-16 14:46:19.620000','2025-08-16 14:46:19.620000',0,NULL),(90,'bbbb@email.com','0738888989','$2a$10$3x6HdD/HpClDcx9/H.kNMeG4dKnVY4VteXqg8LgTi8ZsS78NtGSCm','PT','bbbb','',NULL,NULL,NULL,'','2025-08-17 07:10:44.511000','2025-08-17 07:10:44.511000',0,NULL),(91,'bbb@example.com','077773837','$2a$10$k0lkhpIm0Ci4tEAACnzWRuBjmmAZv8aDE7cTJlwj3mazr2GFBzVaq','PT','bbb','',NULL,NULL,NULL,'','2025-08-17 07:14:04.988000','2025-08-17 07:14:04.988000',0,NULL),(92,'cccc@email.com','088888787','$2a$10$UB.50JUFYl..nvLOuujNY.rCepMxxpO3bpeYPpixyH5GatFXY/EXy','PT','ccccc','',NULL,NULL,NULL,'','2025-08-17 07:19:35.707000','2025-08-17 07:19:35.707000',1,NULL),(93,'ffff@example.com','0777767676','$2a$10$6w6..xuXd6mqExKCBJNVmesvy2YeTUNyHU5eajJhNcXbcZoxjCIJe','PT','fff','',NULL,NULL,NULL,'','2025-08-17 07:21:52.579000','2025-08-17 07:21:52.579000',0,NULL),(94,'qqq@email.com','0888888887','$2a$10$Fh29gEuV1PaNJF1GpDGLA.7FvhmCOroXj0XHvEiU0zqgw7Mm9MGku','USER','ques','Nam','2000-01-01',170,45,'tang can tang co','2025-08-18 05:26:05.031000','2025-08-18 05:26:55.565000',0,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workout_log_comments`
--

DROP TABLE IF EXISTS `workout_log_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workout_log_comments` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `log_id` int NOT NULL,
  `pt_id` int NOT NULL,
  `comment` text NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`comment_id`),
  KEY `idx_log_id` (`log_id`),
  KEY `idx_pt_id` (`pt_id`),
  CONSTRAINT `workout_log_comments_ibfk_1` FOREIGN KEY (`log_id`) REFERENCES `workout_logs` (`log_id`) ON DELETE RESTRICT,
  CONSTRAINT `workout_log_comments_ibfk_2` FOREIGN KEY (`pt_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workout_log_comments`
--

LOCK TABLES `workout_log_comments` WRITE;
/*!40000 ALTER TABLE `workout_log_comments` DISABLE KEYS */;
INSERT INTO `workout_log_comments` VALUES (8,25,71,'good','2025-08-31 02:56:10.952981',0);
/*!40000 ALTER TABLE `workout_log_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workout_logs`
--

DROP TABLE IF EXISTS `workout_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workout_logs` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `session_name` varchar(255) DEFAULT NULL,
  `session_date` date NOT NULL,
  `workout_type` enum('cardio','strength') NOT NULL,
  `exercise_name` varchar(255) NOT NULL,
  `exercise_order` int NOT NULL,
  `set_number` int DEFAULT NULL,
  `weight_kg` double DEFAULT NULL,
  `reps` int DEFAULT NULL,
  `rest_seconds` int DEFAULT '60',
  `duration_minutes` int DEFAULT NULL,
  `calories_burned` double DEFAULT NULL,
  `intensity` enum('high','low','medium') DEFAULT NULL,
  `notes` text,
  `created_at` datetime(6) DEFAULT NULL,
  `is_deleted` int DEFAULT NULL,
  `total_sets` int DEFAULT '1',
  `completed` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`log_id`),
  KEY `idx_user_date` (`user_id`,`session_date`),
  KEY `idx_exercise_order` (`user_id`,`session_date`,`exercise_order`,`set_number`),
  CONSTRAINT `workout_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workout_logs`
--

LOCK TABLES `workout_logs` WRITE;
/*!40000 ALTER TABLE `workout_logs` DISABLE KEYS */;
INSERT INTO `workout_logs` VALUES (25,73,NULL,'2025-08-24','strength','bb',1,1,5,5,60,NULL,NULL,'medium','a','2025-08-24 16:37:15.484000',0,1,0),(26,73,NULL,'2025-08-24','cardio','dsfsd',1,NULL,NULL,NULL,60,55,NULL,'medium','','2025-08-24 17:39:07.822000',0,1,0),(27,73,NULL,'2025-08-24','strength','fsdf',1,1,34,3,60,NULL,NULL,'medium','vsf','2025-08-24 17:39:23.957000',0,1,0),(28,73,NULL,'2025-08-30','strength','sdf',1,1,2,2,60,NULL,NULL,'medium','sfsf','2025-08-30 15:48:07.733000',0,1,0);
/*!40000 ALTER TABLE `workout_logs` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-06 17:55:55
