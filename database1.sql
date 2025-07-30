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
-- Table structure for table `body_measurements`
--

DROP TABLE IF EXISTS `body_measurements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `body_measurements` (
  `measurement_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `weight` decimal(5,2) DEFAULT NULL,
  `body_fat_percentage` decimal(5,2) DEFAULT NULL,
  `muscle_mass` decimal(5,2) DEFAULT NULL,
  `chest` decimal(5,2) DEFAULT NULL,
  `waist` decimal(5,2) DEFAULT NULL,
  `bicep` decimal(5,2) DEFAULT NULL,
  `thigh` decimal(5,2) DEFAULT NULL,
  `notes` text,
  `measured_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`measurement_id`),
  KEY `idx_user_date` (`user_id`,`measured_at`),
  CONSTRAINT `body_measurements_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `body_measurements`
--

LOCK TABLES `body_measurements` WRITE;
/*!40000 ALTER TABLE `body_measurements` DISABLE KEYS */;
INSERT INTO `body_measurements` VALUES (1,8,70.00,18.00,35.00,100.00,80.00,35.00,55.00,'Đo đầu tháng 7','2025-07-01 01:00:00',0),(2,9,55.00,22.00,25.00,85.00,70.00,30.00,50.00,'Đo đầu tháng 6','2025-06-01 01:00:00',1);
/*!40000 ALTER TABLE `body_measurements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_messages`
--

DROP TABLE IF EXISTS `chat_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_messages` (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `receiver_id` int NOT NULL,
  `message` text,
  `message_type` enum('TEXT','IMAGE','FILE') DEFAULT 'TEXT',
  `read_status` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`message_id`),
  KEY `receiver_id` (`receiver_id`),
  KEY `idx_chat_time` (`sender_id`,`receiver_id`,`created_at`),
  CONSTRAINT `chat_messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT,
  CONSTRAINT `chat_messages_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_messages`
--

LOCK TABLES `chat_messages` WRITE;
/*!40000 ALTER TABLE `chat_messages` DISABLE KEYS */;
INSERT INTO `chat_messages` VALUES (3,8,6,'Hôm nay em tập Bench Press hơi đau vai, anh xem giúp em nhé!','TEXT',1,'2025-07-20 03:30:00',0),(4,6,8,'OK, anh sẽ xem log và nhận xét. Cố gắng giữ tư thế đúng nhé!','TEXT',0,'2025-07-20 05:00:00',0);
/*!40000 ALTER TABLE `chat_messages` ENABLE KEYS */;
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
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `attendance` tinyint(1) DEFAULT '0',
  `check_in_time` timestamp NULL DEFAULT NULL,
  `status` enum('ENROLLED','COMPLETED','CANCELLED') DEFAULT 'ENROLLED',
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`enrollment_id`),
  KEY `class_id` (`class_id`),
  KEY `idx_user_class` (`user_id`,`class_id`),
  CONSTRAINT `class_enrollments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT,
  CONSTRAINT `class_enrollments_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `training_classes` (`class_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_enrollments`
--

LOCK TABLES `class_enrollments` WRITE;
/*!40000 ALTER TABLE `class_enrollments` DISABLE KEYS */;
INSERT INTO `class_enrollments` VALUES (3,8,3,'2025-07-01 01:00:00',1,'2025-07-03 11:00:00','ENROLLED',0),(4,9,4,'2025-06-01 01:00:00',0,NULL,'CANCELLED',1);
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
  `name` varchar(100) NOT NULL,
  `type` varchar(100) DEFAULT NULL,
  `status` enum('AVAILABLE','IN_USE','BROKEN','MAINTENANCE') DEFAULT 'AVAILABLE',
  `location` varchar(100) DEFAULT NULL,
  `maintenance_date` date DEFAULT NULL,
  `last_service_date` date DEFAULT NULL,
  `notes` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`device_id`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `devices`
--

LOCK TABLES `devices` WRITE;
/*!40000 ALTER TABLE `devices` DISABLE KEYS */;
INSERT INTO `devices` VALUES (3,'Máy chạy bộ #1','Cardio','AVAILABLE','Tầng 1 - Khu Cardio','2025-12-01','2025-06-01','Hoạt động tốt','2025-07-01 01:00:00','2025-07-01 01:00:00',0),(4,'Tạ đòn #1','Strength','AVAILABLE','Tầng 2 - Khu tạ',NULL,'2025-05-01','Mới bảo trì','2025-07-01 01:00:00','2025-07-01 01:00:00',0),(5,'Thảm yoga #1','Accessory','BROKEN','Tầng 3 - Phòng lớp tập',NULL,NULL,'Hỏng, cần thay mới','2025-07-01 01:00:00','2025-07-01 01:00:00',1);
/*!40000 ALTER TABLE `devices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `diet_items`
--

DROP TABLE IF EXISTS `diet_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diet_items` (
  `diet_item_id` int NOT NULL AUTO_INCREMENT,
  `diet_plan_id` int DEFAULT NULL,
  `meal_name` varchar(100) DEFAULT NULL,
  `meal_type` enum('BREAKFAST','LUNCH','DINNER','SNACK') DEFAULT 'BREAKFAST',
  `calories` int DEFAULT NULL,
  `protein` int DEFAULT NULL,
  `carb` int DEFAULT NULL,
  `fat` int DEFAULT NULL,
  `serving_size` varchar(50) DEFAULT NULL,
  `preparation_notes` text,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`diet_item_id`),
  KEY `diet_plan_id` (`diet_plan_id`),
  KEY `idx_meal_type` (`meal_type`),
  CONSTRAINT `diet_items_ibfk_1` FOREIGN KEY (`diet_plan_id`) REFERENCES `diet_plans` (`diet_plan_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diet_items`
--

LOCK TABLES `diet_items` WRITE;
/*!40000 ALTER TABLE `diet_items` DISABLE KEYS */;
INSERT INTO `diet_items` VALUES (4,3,'Gà nướng và cơm gạo lứt','LUNCH',600,40,60,15,'200g gà, 150g cơm','Nướng gà với ít muối',0),(5,3,'Sữa whey protein','SNACK',150,25,5,3,'1 muỗng whey','Pha với 200ml nước',0),(6,4,'Salad rau xanh','DINNER',200,10,20,8,'200g rau','Thêm dầu ô liu',1);
/*!40000 ALTER TABLE `diet_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `diet_plans`
--

DROP TABLE IF EXISTS `diet_plans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diet_plans` (
  `diet_plan_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `plan_name` varchar(100) DEFAULT NULL,
  `calories_target` int DEFAULT NULL,
  `protein_target` int DEFAULT NULL,
  `carb_target` int DEFAULT NULL,
  `fat_target` int DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `status` enum('ACTIVE','COMPLETED','PAUSED') DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`diet_plan_id`),
  KEY `created_by` (`created_by`),
  KEY `idx_user_date` (`user_id`,`created_at`),
  CONSTRAINT `diet_plans_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT,
  CONSTRAINT `diet_plans_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diet_plans`
--

LOCK TABLES `diet_plans` WRITE;
/*!40000 ALTER TABLE `diet_plans` DISABLE KEYS */;
INSERT INTO `diet_plans` VALUES (3,8,'Chế độ tăng cơ',2500,180,300,80,6,'2025-07-01','2025-09-30','ACTIVE','2025-07-01 01:00:00','2025-07-01 01:00:00',0),(4,9,'Chế độ giảm mỡ',1800,120,150,60,7,'2025-06-01','2025-08-31','PAUSED','2025-06-01 01:00:00','2025-06-01 01:00:00',1);
/*!40000 ALTER TABLE `diet_plans` ENABLE KEYS */;
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
  `name` varchar(100) NOT NULL,
  `duration_months` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `description` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`package_id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_packages`
--

LOCK TABLES `membership_packages` WRITE;
/*!40000 ALTER TABLE `membership_packages` DISABLE KEYS */;
INSERT INTO `membership_packages` VALUES (4,'Gói Cơ Bản',1,500000.00,'Gói tập cơ bản 1 tháng, sử dụng tất cả thiết bị','2025-07-01 01:00:00','2025-07-01 01:00:00',0),(5,'Gói Tiêu Chuẩn',3,1350000.00,'Gói tập 3 tháng, tiết kiệm 10%','2025-07-01 01:00:00','2025-07-01 01:00:00',0),(6,'Gói VIP',12,4800000.00,'Gói VIP 1 năm, bao gồm PT và lớp tập nhóm','2025-07-01 01:00:00','2025-07-01 01:00:00',0),(7,'Gói Cũ',1,450000.00,'Gói cơ bản cũ, đã ngừng cung cấp','2025-06-01 01:00:00','2025-07-01 01:00:00',1);
/*!40000 ALTER TABLE `membership_packages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `notification_id` int NOT NULL AUTO_INCREMENT,
  `receiver_id` int NOT NULL,
  `sender_id` int DEFAULT NULL,
  `title` varchar(200) DEFAULT NULL,
  `content` text,
  `type` enum('REMINDER','CHAT','SYSTEM','MEMBERSHIP','CLASS') DEFAULT NULL,
  `status` enum('READ','UNREAD') DEFAULT 'UNREAD',
  `scheduled_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`notification_id`),
  KEY `sender_id` (`sender_id`),
  KEY `idx_receiver_status` (`receiver_id`,`status`),
  KEY `idx_scheduled` (`scheduled_at`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT,
  CONSTRAINT `notifications_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (3,8,6,'Nhắc nhở tập luyện','Hôm nay là ngày tập ngực, đừng quên!','REMINDER','UNREAD','2025-07-20 00:00:00','2025-07-19 23:00:00',0),(4,9,NULL,'Hết hạn gói tập','Gói tập của bạn đã hết hạn, vui lòng gia hạn!','MEMBERSHIP','READ',NULL,'2025-06-01 01:00:00',1);
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
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
  `amount` decimal(10,2) NOT NULL,
  `payment_method` enum('CASH','CARD','TRANSFER') DEFAULT 'CASH',
  `payment_status` enum('PENDING','COMPLETED','FAILED') DEFAULT 'PENDING',
  `payment_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `notes` text,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`payment_id`),
  KEY `idx_membership_date` (`membership_id`,`payment_date`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`membership_id`) REFERENCES `user_memberships` (`membership_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,3,500000.00,'CASH','COMPLETED','2025-07-01 02:00:00','Thanh toán gói cơ bản',0),(2,4,1350000.00,'CARD','COMPLETED','2025-06-01 03:00:00','Thanh toán gói tiêu chuẩn',1);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `plan_details`
--

DROP TABLE IF EXISTS `plan_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plan_details` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `plan_id` int NOT NULL,
  `day_of_week` int DEFAULT NULL COMMENT '1=Monday, 7=Sunday',
  `exercise_name` varchar(100) DEFAULT NULL,
  `sets` int DEFAULT NULL,
  `reps` int DEFAULT NULL,
  `weight` decimal(6,2) DEFAULT NULL,
  `rest_seconds` int DEFAULT '60',
  `notes` text,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`detail_id`),
  KEY `idx_plan_day` (`plan_id`,`day_of_week`),
  CONSTRAINT `plan_details_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `training_plans` (`plan_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plan_details`
--

LOCK TABLES `plan_details` WRITE;
/*!40000 ALTER TABLE `plan_details` DISABLE KEYS */;
INSERT INTO `plan_details` VALUES (4,3,1,'Bench Press',3,10,50.00,90,'Tập trung vào ngực',0),(5,3,1,'Squat',4,12,60.00,120,'Tập chân',0),(6,4,2,'Treadmill',0,0,NULL,60,'Chạy 20 phút',1);
/*!40000 ALTER TABLE `plan_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `review_id` int NOT NULL AUTO_INCREMENT,
  `reviewer_id` int NOT NULL,
  `reviewee_id` int DEFAULT NULL,
  `class_id` int DEFAULT NULL,
  `rating` int DEFAULT NULL,
  `comment` text,
  `review_type` enum('PT_TO_USER','USER_TO_PT','USER_TO_CLASS','USER_TO_GYM') NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`review_id`),
  KEY `idx_reviewer` (`reviewer_id`),
  KEY `idx_reviewee` (`reviewee_id`),
  KEY `idx_class` (`class_id`),
  CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`reviewer_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT,
  CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`reviewee_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `reviews_ibfk_3` FOREIGN KEY (`class_id`) REFERENCES `training_classes` (`class_id`) ON DELETE RESTRICT,
  CONSTRAINT `reviews_chk_1` CHECK (((`rating` >= 1) and (`rating` <= 5)))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,8,6,NULL,5,'PT Hùng rất nhiệt tình, hướng dẫn chi tiết!','USER_TO_PT','2025-07-20 05:30:00',0),(2,8,NULL,3,4,'Lớp yoga rất thư giãn, nhưng hơi đông','USER_TO_CLASS','2025-07-03 12:30:00',0);
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trainers`
--

DROP TABLE IF EXISTS `trainers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trainers` (
  `trainer_id` int NOT NULL,
  `specialization` varchar(100) DEFAULT NULL,
  `schedule` text,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`trainer_id`),
  CONSTRAINT `trainers_ibfk_1` FOREIGN KEY (`trainer_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trainers`
--

LOCK TABLES `trainers` WRITE;
/*!40000 ALTER TABLE `trainers` DISABLE KEYS */;
INSERT INTO `trainers` VALUES (6,'Tập sức mạnh','Thứ 2,4,6: 08:00-12:00, 17:00-20:00',0),(7,'Yoga và Cardio','Thứ 3,5,7: 07:00-11:00, 18:00-21:00',0);
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
  `name` varchar(100) NOT NULL,
  `description` text,
  `trainer_id` int DEFAULT NULL,
  `schedule` text,
  `max_participants` int DEFAULT NULL,
  `price` decimal(8,2) DEFAULT '0.00',
  `duration_minutes` int DEFAULT '60',
  `start_time` time DEFAULT NULL,
  `days_of_week` varchar(20) DEFAULT NULL COMMENT 'e.g., 1,3,5 for Mon,Wed,Fri',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`class_id`),
  KEY `trainer_id` (`trainer_id`),
  KEY `idx_name` (`name`),
  CONSTRAINT `training_classes_ibfk_1` FOREIGN KEY (`trainer_id`) REFERENCES `trainers` (`trainer_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_classes`
--

LOCK TABLES `training_classes` WRITE;
/*!40000 ALTER TABLE `training_classes` DISABLE KEYS */;
INSERT INTO `training_classes` VALUES (3,'Lớp Yoga Cơ Bản','Lớp yoga cho người mới bắt đầu',7,'Thứ 3,5: 18:00-19:00',20,200000.00,60,'18:00:00','3,5','2025-07-01 01:00:00','2025-07-01 01:00:00',0),(4,'Lớp Cardio Nâng Cao','Lớp cardio cường độ cao',NULL,'Thứ 2,4: 19:00-20:00',15,250000.00,60,'19:00:00','2,4','2025-07-01 01:00:00','2025-07-01 01:00:00',0);
/*!40000 ALTER TABLE `training_classes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_plans`
--

DROP TABLE IF EXISTS `training_plans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_plans` (
  `plan_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `created_by` int DEFAULT NULL,
  `plan_name` varchar(100) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `status` enum('ACTIVE','COMPLETED','PAUSED') DEFAULT 'ACTIVE',
  `notes` text,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`plan_id`),
  KEY `created_by` (`created_by`),
  KEY `idx_user_date` (`user_id`,`start_date`),
  CONSTRAINT `training_plans_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT,
  CONSTRAINT `training_plans_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_plans`
--

LOCK TABLES `training_plans` WRITE;
/*!40000 ALTER TABLE `training_plans` DISABLE KEYS */;
INSERT INTO `training_plans` VALUES (3,8,6,'Kế hoạch tăng cơ','2025-07-01','2025-09-30','ACTIVE','Tập trung vào bài tập sức mạnh',0),(4,9,7,'Kế hoạch giảm mỡ','2025-06-01','2025-08-31','PAUSED','Tạm dừng do khách hủy gói',1);
/*!40000 ALTER TABLE `training_plans` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_goals`
--

DROP TABLE IF EXISTS `user_goals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_goals` (
  `goal_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `goal_type` enum('WEIGHT_LOSS','WEIGHT_GAIN','MUSCLE_GAIN','ENDURANCE','STRENGTH','BODY_FAT_REDUCTION') NOT NULL,
  `target_value` decimal(8,2) DEFAULT NULL,
  `current_value` decimal(8,2) DEFAULT '0.00',
  `target_date` date DEFAULT NULL,
  `status` enum('ACTIVE','COMPLETED','PAUSED') DEFAULT 'ACTIVE',
  `notes` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`goal_id`),
  KEY `idx_user_status` (`user_id`,`status`),
  CONSTRAINT `user_goals_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_goals`
--

LOCK TABLES `user_goals` WRITE;
/*!40000 ALTER TABLE `user_goals` DISABLE KEYS */;
INSERT INTO `user_goals` VALUES (1,8,'MUSCLE_GAIN',75.00,70.00,'2025-12-31','ACTIVE','Tăng 5kg cơ bắp','2025-07-01 01:00:00','2025-07-01 01:00:00',0),(2,9,'WEIGHT_LOSS',50.00,55.00,'2025-08-31','PAUSED','Giảm 5kg','2025-06-01 01:00:00','2025-06-01 01:00:00',1);
/*!40000 ALTER TABLE `user_goals` ENABLE KEYS */;
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
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `status` enum('ACTIVE','EXPIRED','CANCELLED') DEFAULT 'ACTIVE',
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`membership_id`),
  KEY `package_id` (`package_id`),
  KEY `idx_user_status` (`user_id`,`status`),
  CONSTRAINT `user_memberships_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT,
  CONSTRAINT `user_memberships_ibfk_2` FOREIGN KEY (`package_id`) REFERENCES `membership_packages` (`package_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_memberships`
--

LOCK TABLES `user_memberships` WRITE;
/*!40000 ALTER TABLE `user_memberships` DISABLE KEYS */;
INSERT INTO `user_memberships` VALUES (3,8,4,'2025-07-01','2025-07-31','ACTIVE',0),(4,9,5,'2025-06-01','2025-08-31','EXPIRED',1);
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
  `phone_number` varchar(15) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('ADMIN','PT','USER') NOT NULL,
  `name` varchar(100) NOT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `height` decimal(5,2) DEFAULT NULL,
  `weight` decimal(5,2) DEFAULT NULL,
  `fitness_goal` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone_number` (`phone_number`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone_number`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (5,'admin@gym.com','0123456789','$2a$10$example.hash.here','ADMIN','Nguyễn Văn Quản Trị','Male','1985-01-01',NULL,NULL,NULL,'2025-07-01 01:00:00','2025-07-01 01:00:00',0),(6,'pt1@gym.com','0912345678','$2a$10$example.hash.here','PT','Trần Văn Hùng','Male','1990-03-15',NULL,NULL,NULL,'2025-07-01 01:00:00','2025-07-01 01:00:00',0),(7,'pt2@gym.com','0918765432','$2a$10$example.hash.here','PT','Lê Thị Mai','Female','1992-07-20',NULL,NULL,NULL,'2025-07-01 01:00:00','2025-07-01 01:00:00',0),(8,'user1@gym.com','0987654321','$2a$10$example.hash.here','USER','Nguyễn Văn An','Male','1995-05-10',170.00,70.00,'Tăng cơ bắp','2025-07-01 01:00:00','2025-07-01 01:00:00',0),(9,'user2@gym.com','0971234567','$2a$10$example.hash.here','USER','Phạm Thị Bình','Female','1998-11-25',160.00,55.00,'Giảm mỡ','2025-07-01 01:00:00','2025-07-01 01:00:00',1);
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
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`comment_id`),
  KEY `idx_log_id` (`log_id`),
  KEY `idx_pt_id` (`pt_id`),
  CONSTRAINT `workout_log_comments_ibfk_1` FOREIGN KEY (`log_id`) REFERENCES `workout_logs` (`log_id`) ON DELETE RESTRICT,
  CONSTRAINT `workout_log_comments_ibfk_2` FOREIGN KEY (`pt_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workout_log_comments`
--

LOCK TABLES `workout_log_comments` WRITE;
/*!40000 ALTER TABLE `workout_log_comments` DISABLE KEYS */;
INSERT INTO `workout_log_comments` VALUES (1,3,6,'Điều chỉnh tư thế vai để tránh đau. Tốt lắm!','2025-07-20 05:00:00',0),(2,4,6,'Tăng trọng lượng lên 65kg tuần tới.','2025-07-19 04:00:00',0);
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
  `exercise_name` varchar(100) DEFAULT NULL,
  `sets` int DEFAULT NULL,
  `reps` int DEFAULT NULL,
  `weight` decimal(6,2) DEFAULT NULL,
  `duration_min` int DEFAULT NULL,
  `calories_burned` decimal(6,2) DEFAULT NULL,
  `notes` text COMMENT 'User notes after workout',
  `logged_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`log_id`),
  KEY `idx_user_date` (`user_id`,`logged_at`),
  CONSTRAINT `workout_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workout_logs`
--

LOCK TABLES `workout_logs` WRITE;
/*!40000 ALTER TABLE `workout_logs` DISABLE KEYS */;
INSERT INTO `workout_logs` VALUES (3,8,'Bench Press',3,10,50.00,30,150.00,'Cảm thấy tốt, hơi đau vai','2025-07-20 03:00:00',0),(4,8,'Squat',4,12,60.00,35,NULL,'Tập ổn, cần tăng trọng lượng','2025-07-19 02:00:00',0),(5,9,'Treadmill',0,0,NULL,20,200.00,'Chạy bộ nhẹ','2025-06-15 01:00:00',1);
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

-- Dump completed on 2025-07-22  9:19:31
