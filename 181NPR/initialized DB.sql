-- MySQL dump 10.13  Distrib 5.6.17, for Win64 (x86_64)
--
-- Host: localhost    Database: 181nprdb
-- ------------------------------------------------------
-- Server version	5.6.17

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `181nprdb`
--

/*!40000 DROP DATABASE IF EXISTS `181nprdb`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `181nprdb` /*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_general_ci */;

USE `181nprdb`;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account` (
  `emailAccount` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `emailPassword` varchar(50) COLLATE latin1_general_ci NOT NULL,
  `emailPort` varchar(10) COLLATE latin1_general_ci NOT NULL,
  `fowizUsername` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `fowizPasscode` varchar(50) COLLATE latin1_general_ci NOT NULL,
  `reservationDefault` varchar(200) COLLATE latin1_general_ci NOT NULL,
  `registrationDefault` varchar(200) COLLATE latin1_general_ci NOT NULL,
  `furnitureDefault` varchar(200) COLLATE latin1_general_ci NOT NULL,
  `gadgetDefault` varchar(200) COLLATE latin1_general_ci NOT NULL,
  `transientDefault` varchar(200) COLLATE latin1_general_ci NOT NULL,
  `biometric` enum('Yes','No') COLLATE latin1_general_ci NOT NULL,
  `transientBillingDefault` varchar(200) COLLATE latin1_general_ci NOT NULL,
  `statementOfAccountDefault` varchar(200) COLLATE latin1_general_ci NOT NULL DEFAULT 'D:\\forms\\residentStatementOfAccount',
  `paymentRemittanceDefault` varchar(200) COLLATE latin1_general_ci NOT NULL DEFAULT 'D:\\forms\\residentBilling',
  `curfew` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES ('181npr@gmail.com','automatedmonitoringsystem','587','181NPR','morefunat181','D:forms\reservation','D:forms\registration','D:formsfurniture','D:formsgadget','D:forms	ransient','Yes','D:\\forms\\transientBilling','D:\\forms\\residentStatementOfAccount','D:\\forms\\residentBilling','22:00:00');
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin` (
  `adminIdnum` int(8) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `adminFname` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `adminMname` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `adminLname` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `adminGender` enum('Male','Female','','') COLLATE latin1_general_ci NOT NULL,
  `adminBirthdate` date NOT NULL,
  `adminEmail` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `adminPicture` mediumblob,
  PRIMARY KEY (`adminIdnum`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (00000001,'Administrator','','','Male','1900-01-01','','ˇÿˇ‡\0JFIF\0\0`\0`\0\0ˇ€\0C\0		\Z#,%!*!\Z&4\'*./121%6:60:,010ˇ€\0C		\n0  00000000000000000000000000000000000000000000000000ˇ¿\0\0n\0y\"\0ˇƒ\0\0\0\0\0\0\0\0\0\0\0	\nˇƒ\0µ\0\0\0}\0!1AQa\"q2Åë°#B±¡R—$3brÇ	\n\Z%&\'()*456789:CDEFGHIJSTUVWXYZcdefghijstuvwxyzÉÑÖÜáàâäíìîïñóòôö¢£§•¶ß®©™≤≥¥µ∂∑∏π∫¬√ƒ≈∆«»… “”‘’÷◊ÿŸ⁄·‚„‰ÂÊÁËÈÍÒÚÛÙıˆ˜¯˘˙ˇƒ\0\0\0\0\0\0\0\0	\nˇƒ\0µ\0\0w\0!1AQaq\"2ÅBë°±¡	#3Rbr—\n$4·%Ò\Z&\'()*56789:CDEFGHIJSTUVWXYZcdefghijstuvwxyzÇÉÑÖÜáàâäíìîïñóòôö¢£§•¶ß®©™≤≥¥µ∂∑∏π∫¬√ƒ≈∆«»… “”‘’÷◊ÿŸ⁄‚„‰ÂÊÁËÈÍÚÛÙıˆ˜¯˘˙ˇ⁄\0\0\0?\0Ù¡IE÷bQE\0QRAì»%,∆Ä#†dÙ˝+v”Eâp”±vÙ\n“äﬁ(Ü#çWË+\'Qt)GπÀ•ùÀ¨G“°`Uà=A¡Æ∂Í#4-\ZπB√´òºµñ÷B≤3¡\r8ŒÏ\Z±QZQE\0QE\0¶íä\0(¢ä\0+®”-⁄ŸF>vcÔ\\Ã|»øQ¸Î±N}+*è°qä(¨K\nÜÍ›.\"1»Ù©®4»\\¬÷ÛºM¸\'^◊?‰ ﬂA¸™çuE›0¢ä)à(¢ä\0(¢î\Z\0J)I§†≈˛µ~£˘◊`:W)¶ƒ\'ºé6$s«∑5’ä¬¶ÂƒZ(¢≥,)\r-!†o[ˇ\0êÉ˝Ú™©ØE≤Èd›ù„ß¶+0ıÆòÏd˜äu4’(•å–QE(†¢ä(∆ú‚+ÿú`˛5’éï∆WU¶KÊÿƒ«Æ‹\Z∆¢Í\\KTQEdXQE5ÿ*ñ=\0Õ\0`xÖ˜]\"évØ5õO∏êÀ3πÁsL”dd˜ê—äJ°¢ä(¥QK@	J®\0VÆÉtÀ\'Ÿœ*‹èj 5•†ƒˇ\0jÛ6°O8‚¢{;ù\rQ\\Ê°Y∫Ì√Ej:»pOµiVGàQö»RBíI™£Ò!3îRR◊IêÜä\r%\0(¢ä((¢ä\0){S£Gë∂FÖò˙\n“µ—dl€`Ùj\\íôñb\0ˆÆØOá»¥é3‘i∂÷ˆ¯Ÿ›˝„…´Uå•r“∞RR“w®(Zä·<ÿ]?º©i(èu(Ïç’N\r4\ZÍÆ,‡ü˝daèÆ9¨ÀùÜMªÁ˝ñ≠‘”‹œïòÙ∏ßœ∞6ŸP©˜¿kM…äSI@\näŒ¡QK1ËkYh•Äkñ#˝ë˝jm’&r2ƒ‡{V∞¨g>à∏Æ§p[≈Ìâèjñä+\"¬ä(†ä);–—IE\0-îPdç$RÆ°îˆ\"≤ÓÙXÿ∑;˚ßëZÙÜöml!qñÚlïv∞¶WK´[,ˆåOÉp5ÃÁÿVÒíí‘Õ£ˇŸ');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `billing`
--

DROP TABLE IF EXISTS `billing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `billing` (
  `billingIdnum` int(8) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `billingDatePaid` date DEFAULT NULL,
  `billingDatein` date DEFAULT NULL,
  `billingDateout` date DEFAULT NULL,
  `billingRateType` enum('Monthly','Daily') DEFAULT NULL,
  `billingNoofdays` int(20) DEFAULT NULL,
  `billingRoomrate` decimal(10,2) DEFAULT NULL,
  `billingShuttleRatetype` enum('Monthly','Daily') DEFAULT NULL,
  `billingShuttleRate` decimal(10,2) DEFAULT NULL,
  `billingGadgetRate` decimal(10,2) DEFAULT NULL,
  `billingRemarks` varchar(500) DEFAULT NULL,
  `billingTotalRoomrate` decimal(10,2) DEFAULT NULL,
  `billingTotalShuttlerate` decimal(10,2) DEFAULT NULL,
  `billingTotalGadgetrate` decimal(10,2) DEFAULT NULL,
  `billingAdditionalfee` decimal(10,2) DEFAULT NULL,
  `billingTotalAmount` decimal(10,2) DEFAULT NULL,
  `billingAmountPaid` decimal(10,2) DEFAULT NULL,
  `billingBalance` decimal(10,2) DEFAULT NULL,
  `billingStatus` enum('Paid','Unpaid','Replaced') NOT NULL DEFAULT 'Unpaid',
  `residentIdnum` int(8) unsigned zerofill NOT NULL,
  `roomIdnum` int(8) unsigned zerofill NOT NULL,
  `adminIdnum` int(8) unsigned zerofill DEFAULT NULL,
  `billingModeOfPayment` enum('cash','cheque','bd') DEFAULT NULL,
  PRIMARY KEY (`billingIdnum`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `billing`
--

LOCK TABLES `billing` WRITE;
/*!40000 ALTER TABLE `billing` DISABLE KEYS */;
/*!40000 ALTER TABLE `billing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `billinggadget`
--

DROP TABLE IF EXISTS `billinggadget`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `billinggadget` (
  `billingIdnum` int(8) unsigned zerofill NOT NULL,
  `gadgetIdnum` int(8) unsigned zerofill NOT NULL,
  `isChecked` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `billinggadget`
--

LOCK TABLES `billinggadget` WRITE;
/*!40000 ALTER TABLE `billinggadget` DISABLE KEYS */;
/*!40000 ALTER TABLE `billinggadget` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biometrics`
--

DROP TABLE IF EXISTS `biometrics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `biometrics` (
  `biometricsIdnum` int(10) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `fingerprintData1` longtext COLLATE latin1_general_ci,
  `fingerprintData2` longtext COLLATE latin1_general_ci,
  `status` enum('active','not active') COLLATE latin1_general_ci DEFAULT NULL,
  `residentIdnum` int(8) unsigned zerofill NOT NULL,
  PRIMARY KEY (`biometricsIdnum`),
  UNIQUE KEY `residentIdnum` (`residentIdnum`),
  CONSTRAINT `biometrics_ibfk_1` FOREIGN KEY (`residentIdnum`) REFERENCES `resident` (`residentIdnum`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biometrics`
--

LOCK TABLES `biometrics` WRITE;
/*!40000 ALTER TABLE `biometrics` DISABLE KEYS */;
/*!40000 ALTER TABLE `biometrics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `controlnumber`
--

DROP TABLE IF EXISTS `controlnumber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `controlnumber` (
  `controlNumber` int(6) NOT NULL,
  `type` enum('resident','transient') COLLATE latin1_general_ci NOT NULL,
  `id` int(8) unsigned zerofill NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `controlnumber`
--

LOCK TABLES `controlnumber` WRITE;
/*!40000 ALTER TABLE `controlnumber` DISABLE KEYS */;
/*!40000 ALTER TABLE `controlnumber` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `credentials`
--

DROP TABLE IF EXISTS `credentials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `credentials` (
  `adminIdnum` int(8) unsigned zerofill NOT NULL,
  `adminUsername` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `adminPassword` longtext COLLATE latin1_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credentials`
--

LOCK TABLES `credentials` WRITE;
/*!40000 ALTER TABLE `credentials` DISABLE KEYS */;
INSERT INTO `credentials` VALUES (00000001,'admin','72edc028befbd604d8697ad4ae1bbe42');
/*!40000 ALTER TABLE `credentials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `fingerprintowner`
--

DROP TABLE IF EXISTS `fingerprintowner`;
/*!50001 DROP VIEW IF EXISTS `fingerprintowner`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `fingerprintowner` (
  `residentIdnum` tinyint NOT NULL,
  `resident_fullname` tinyint NOT NULL,
  `fingerprintData1` tinyint NOT NULL,
  `fingerprintData2` tinyint NOT NULL,
  `status` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `furniture`
--

DROP TABLE IF EXISTS `furniture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `furniture` (
  `furnitureIdnum` int(8) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `furnitureControlNo` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `furnitureColor` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `furnitureBrand` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `furniturePurchasedate` date DEFAULT NULL,
  `furnitureItemName` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `residentIdnum` int(8) unsigned zerofill DEFAULT NULL,
  `transientIdnum` int(8) unsigned zerofill DEFAULT NULL,
  `roomIdnum` int(8) unsigned zerofill DEFAULT NULL,
  `furnitureStatus` enum('Available','Taken','Defective') COLLATE latin1_general_ci NOT NULL,
  PRIMARY KEY (`furnitureIdnum`),
  KEY `residentIdnum` (`residentIdnum`),
  KEY `roomIdnum` (`roomIdnum`),
  CONSTRAINT `furniture_ibfk_2` FOREIGN KEY (`roomIdnum`) REFERENCES `room` (`roomIdnum`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `furniture`
--

LOCK TABLES `furniture` WRITE;
/*!40000 ALTER TABLE `furniture` DISABLE KEYS */;
/*!40000 ALTER TABLE `furniture` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gadget`
--

DROP TABLE IF EXISTS `gadget`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gadget` (
  `gadgetIdnum` int(8) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `gadgetItemName` varchar(50) COLLATE latin1_general_ci NOT NULL,
  `gadgetDescription` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `gadgetSerialNo` varchar(45) COLLATE latin1_general_ci DEFAULT NULL,
  `gadgetVoltage` varchar(50) COLLATE latin1_general_ci DEFAULT NULL,
  `gadgetWattage` varchar(50) COLLATE latin1_general_ci DEFAULT NULL,
  `gadgetRate` decimal(10,2) DEFAULT NULL,
  `residentIdnum` int(8) unsigned zerofill NOT NULL,
  PRIMARY KEY (`gadgetIdnum`),
  KEY `residentIdnum` (`residentIdnum`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gadget`
--

LOCK TABLES `gadget` WRITE;
/*!40000 ALTER TABLE `gadget` DISABLE KEYS */;
/*!40000 ALTER TABLE `gadget` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `groupvisitcount`
--

DROP TABLE IF EXISTS `groupvisitcount`;
/*!50001 DROP VIEW IF EXISTS `groupvisitcount`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `groupvisitcount` (
  `residentIdnum` tinyint NOT NULL,
  `visitorsStartDate` tinyint NOT NULL,
  `visitorsTimein` tinyint NOT NULL,
  `visitorsTimeout` tinyint NOT NULL,
  `visitorsArea` tinyint NOT NULL,
  `visitorCount` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `logs`
--

DROP TABLE IF EXISTS `logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logs` (
  `logsIdnum` int(8) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `logsDate` date NOT NULL,
  `logsTime` time NOT NULL,
  `logsStatus` varchar(5) COLLATE latin1_general_ci NOT NULL,
  `residentIdnum` int(8) unsigned zerofill NOT NULL,
  PRIMARY KEY (`logsIdnum`),
  KEY `residentIdnum` (`residentIdnum`),
  CONSTRAINT `logs_ibfk_1` FOREIGN KEY (`residentIdnum`) REFERENCES `resident` (`residentIdnum`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logs`
--

LOCK TABLES `logs` WRITE;
/*!40000 ALTER TABLE `logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messagetemplate`
--

DROP TABLE IF EXISTS `messagetemplate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `messagetemplate` (
  `reservation7days` varchar(160) COLLATE latin1_general_ci NOT NULL,
  `reservation3days` varchar(160) COLLATE latin1_general_ci NOT NULL,
  `successfulRegistration` varchar(160) COLLATE latin1_general_ci NOT NULL,
  `visitorInform` varchar(160) COLLATE latin1_general_ci NOT NULL,
  `rentDueDate` varchar(160) COLLATE latin1_general_ci NOT NULL,
  `balanceAmount` varchar(160) COLLATE latin1_general_ci NOT NULL,
  `newRoommate` varchar(160) COLLATE latin1_general_ci NOT NULL,
  `curfewNotice` varchar(160) COLLATE latin1_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messagetemplate`
--

LOCK TABLES `messagetemplate` WRITE;
/*!40000 ALTER TABLE `messagetemplate` DISABLE KEYS */;
INSERT INTO `messagetemplate` VALUES ('UPDATE: Your reservation is due 7 days from now. Kindly, drop by at our office to confirm your reservation. Thank you!','UPDATE: Your reservation is due 3 days from now. Kindly, drop by at our office to confirm your reservation. Thank you!','You are now successfully registered and already part of the residence. Please proceed to the admin office for other concerns. Thank you!','We would like to inform you that you have a visitor at the lobby whose name is [insertnamehere].','DUE: We would like to inform you that the due date of the rent is 5 days from now. Kindly, pay on or before the due date. Thank you!','Good day! Your balance as of <datetoday> is P <balance> and is due on <duedate>. Thank you!','Good day! We would like to inform you that your new roommate will be moving in not later than <date>.','We would like to inform you that your child/ward <residentName> was not able to come in the dorm premises before the 10:00PM curfew last <date> ');
/*!40000 ALTER TABLE `messagetemplate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification` (
  `notificationIdnum` int(8) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `notificationTime` time NOT NULL,
  `notificationDate` date NOT NULL,
  `notificationMessage` varchar(150) COLLATE latin1_general_ci NOT NULL,
  `notificationType` enum('Violations','Balance','Reservation Update','Successful Registration','Visitor','Due Date','New Roommate','Others') COLLATE latin1_general_ci NOT NULL,
  `notificationContact` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `notificationMethod` enum('email','text') CHARACTER SET utf16 NOT NULL,
  `residentIdnum` int(8) unsigned zerofill NOT NULL,
  `notificationTable` enum('resident','reservation','transient') COLLATE latin1_general_ci NOT NULL,
  PRIMARY KEY (`notificationIdnum`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `personperroom`
--

DROP TABLE IF EXISTS `personperroom`;
/*!50001 DROP VIEW IF EXISTS `personperroom`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `personperroom` (
  `roomIdnum` tinyint NOT NULL,
  `roomNumber` tinyint NOT NULL,
  `numberOfResident` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `rate`
--

DROP TABLE IF EXISTS `rate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rate` (
  `rentRoomSingleSharing` decimal(10,2) NOT NULL DEFAULT '9000.00',
  `rentRoomDoubleSharing` decimal(10,2) NOT NULL DEFAULT '6500.00',
  `rentRoomTripleSharing` decimal(10,2) NOT NULL DEFAULT '4500.00',
  `rentRoomMasterSuite` decimal(10,2) NOT NULL,
  `shuttleRateMonthly` decimal(10,2) NOT NULL,
  `shuttleRateDaily` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rate`
--

LOCK TABLES `rate` WRITE;
/*!40000 ALTER TABLE `rate` DISABLE KEYS */;
INSERT INTO `rate` VALUES (9000.00,5500.00,4500.00,9000.00,300.00,20.00);
/*!40000 ALTER TABLE `rate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registration`
--

DROP TABLE IF EXISTS `registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registration` (
  `registrationIdnum` int(8) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `registrationResidentCollege` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationResidentCourse` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationResidentYear` varchar(4) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationResidentDept` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationResidentBirthdate` date DEFAULT NULL,
  `registrationResidentGender` enum('Male','Female','','') COLLATE latin1_general_ci DEFAULT NULL,
  `registrationResidentMobileNo` varchar(50) COLLATE latin1_general_ci NOT NULL,
  `registrationResidentMobileNo2` varchar(50) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationFatherName` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationFatherLandline` varchar(50) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationFatherMobileNo` varchar(50) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationFatherEmail` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationMotherName` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationMotherLandline` varchar(50) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationMotherMobileNo` varchar(50) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationMotherEmail` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationGuardianName` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationGuardianAddress` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationGuardianMobileNo` varchar(50) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationGuardianRelation` varchar(30) COLLATE latin1_general_ci DEFAULT NULL,
  `registrationPicture` mediumblob,
  `registrationStatus` enum('Not Active','Active','','') COLLATE latin1_general_ci NOT NULL DEFAULT 'Not Active',
  `roomIdnum` int(8) unsigned zerofill NOT NULL,
  `reservationIdnum` int(8) unsigned zerofill NOT NULL,
  `registrationDate` date NOT NULL,
  PRIMARY KEY (`registrationIdnum`),
  KEY `roomIdnum` (`roomIdnum`),
  KEY `reservationIdnum` (`reservationIdnum`),
  CONSTRAINT `registration_ibfk_1` FOREIGN KEY (`roomIdnum`) REFERENCES `room` (`roomIdnum`) ON UPDATE CASCADE,
  CONSTRAINT `registration_ibfk_2` FOREIGN KEY (`reservationIdnum`) REFERENCES `reservation` (`reservationIdnum`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registration`
--

LOCK TABLES `registration` WRITE;
/*!40000 ALTER TABLE `registration` DISABLE KEYS */;
/*!40000 ALTER TABLE `registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rentedroom`
--

DROP TABLE IF EXISTS `rentedroom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rentedroom` (
  `rentId` int(11) NOT NULL AUTO_INCREMENT,
  `rentStartDate` date DEFAULT NULL,
  `rentEndDate` date DEFAULT NULL,
  `rentRateType` enum('Monthly','Daily') COLLATE latin1_general_ci NOT NULL,
  `rentRoomRate` decimal(10,2) NOT NULL,
  `roomIdnum` int(8) unsigned zerofill NOT NULL,
  PRIMARY KEY (`rentId`),
  KEY `roomIdnum` (`roomIdnum`),
  CONSTRAINT `rentedroom_ibfk_1` FOREIGN KEY (`roomIdnum`) REFERENCES `room` (`roomIdnum`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rentedroom`
--

LOCK TABLES `rentedroom` WRITE;
/*!40000 ALTER TABLE `rentedroom` DISABLE KEYS */;
/*!40000 ALTER TABLE `rentedroom` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reservation` (
  `reservationIdnum` int(8) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `reservationLname` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `reservationFname` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `reservationMname` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `reservationHomeaddress` varchar(255) COLLATE latin1_general_ci DEFAULT NULL,
  `reservationZipCode` varchar(10) COLLATE latin1_general_ci DEFAULT NULL,
  `reservationMobileNo` varchar(11) COLLATE latin1_general_ci DEFAULT NULL,
  `reservationSchoolTerm` enum('First Semester','Second Semester','Summer Term','') COLLATE latin1_general_ci DEFAULT NULL,
  `reservationEmail` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `reservationAyFrom` year(4) DEFAULT NULL,
  `reservationAyTo` year(4) DEFAULT NULL,
  `reservationOthers` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `reservationDatePaid` date NOT NULL,
  `reservationStatus` enum('Cancel','Registered','Pending') COLLATE latin1_general_ci NOT NULL,
  `roomIdnum` int(8) unsigned zerofill NOT NULL,
  PRIMARY KEY (`reservationIdnum`),
  KEY `roomIdnum` (`roomIdnum`),
  CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`roomIdnum`) REFERENCES `room` (`roomIdnum`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resident`
--

DROP TABLE IF EXISTS `resident`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resident` (
  `residentIdnum` int(8) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `residentFname` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `residentMname` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `residentLname` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `residentGender` enum('Male','Female','','') COLLATE latin1_general_ci DEFAULT NULL,
  `residentZipCode` varchar(10) COLLATE latin1_general_ci DEFAULT NULL,
  `residentMobileNo` varchar(11) COLLATE latin1_general_ci NOT NULL,
  `residentEmail` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `residentBirthdate` date DEFAULT NULL,
  `residentHomeAddress` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `picture` mediumblob,
  `status` enum('Not Active','Active','Leave') COLLATE latin1_general_ci DEFAULT 'Not Active',
  `registrationIdnum` int(8) unsigned zerofill NOT NULL,
  `roomIdnum` int(8) unsigned zerofill NOT NULL,
  PRIMARY KEY (`residentIdnum`),
  KEY `roomIdnum` (`roomIdnum`),
  KEY `registrationIdnum` (`registrationIdnum`),
  CONSTRAINT `resident_ibfk_2` FOREIGN KEY (`roomIdnum`) REFERENCES `room` (`roomIdnum`) ON UPDATE CASCADE,
  CONSTRAINT `resident_ibfk_3` FOREIGN KEY (`registrationIdnum`) REFERENCES `registration` (`registrationIdnum`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resident`
--

LOCK TABLES `resident` WRITE;
/*!40000 ALTER TABLE `resident` DISABLE KEYS */;
/*!40000 ALTER TABLE `resident` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `residentnames`
--

DROP TABLE IF EXISTS `residentnames`;
/*!50001 DROP VIEW IF EXISTS `residentnames`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `residentnames` (
  `residentNames` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `room` (
  `roomIdnum` int(8) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `roomNumber` varchar(3) COLLATE latin1_general_ci NOT NULL,
  `roomStatus` enum('not available','fully occupied','partially occupied','unoccupied') COLLATE latin1_general_ci NOT NULL,
  `roomType` enum('S','D','T','SU') COLLATE latin1_general_ci NOT NULL,
  `roomDorm` varchar(4) COLLATE latin1_general_ci NOT NULL,
  PRIMARY KEY (`roomIdnum`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (00000001,'200','unoccupied','SU','1'),(00000002,'201','unoccupied','D','1'),(00000003,'202','unoccupied','S','1'),(00000004,'203','unoccupied','T','1'),(00000005,'204','unoccupied','T','1'),(00000006,'205','unoccupied','D','1'),(00000007,'206','unoccupied','T','1'),(00000008,'207','unoccupied','D','1'),(00000009,'208','unoccupied','S','1'),(00000010,'209','unoccupied','D','1'),(00000011,'210','unoccupied','T','1'),(00000012,'211','unoccupied','D','1'),(00000013,'212','unoccupied','D','1'),(00000014,'214','unoccupied','T','1'),(00000015,'215','unoccupied','S','1'),(00000016,'300','unoccupied','SU','2'),(00000017,'301','unoccupied','D','2'),(00000018,'302','unoccupied','D','2'),(00000019,'303','unoccupied','T','2'),(00000020,'304','unoccupied','T','2'),(00000021,'305','unoccupied','D','2'),(00000022,'306','unoccupied','D','2'),(00000023,'307','unoccupied','S','2'),(00000024,'308','unoccupied','D','2'),(00000025,'309','unoccupied','T','2'),(00000026,'310','unoccupied','T','2'),(00000027,'311','unoccupied','D','2'),(00000028,'312','unoccupied','T','2'),(00000029,'314','unoccupied','T','2'),(00000030,'315','unoccupied','S','2'),(00000031,'400','unoccupied','SU','3'),(00000032,'401','unoccupied','S','3'),(00000033,'402','unoccupied','D','3'),(00000034,'403','unoccupied','T','3'),(00000035,'404','unoccupied','T','3'),(00000036,'405','unoccupied','D','3'),(00000037,'406','unoccupied','D','3'),(00000038,'407','unoccupied','S','3'),(00000039,'408','unoccupied','D','3'),(00000040,'409','unoccupied','T','3'),(00000041,'410','unoccupied','T','3'),(00000042,'411','unoccupied','D','3'),(00000043,'412','unoccupied','T','3'),(00000044,'414','unoccupied','T','3'),(00000045,'415','unoccupied','S','3');
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shuttle`
--

DROP TABLE IF EXISTS `shuttle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shuttle` (
  `shuttleId` int(8) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `shuttleDate` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `shuttleNoRides` int(2) DEFAULT NULL,
  `shuttleRate` decimal(10,2) DEFAULT NULL,
  `shuttleRateType` enum('Monthly','Rides') COLLATE latin1_general_ci NOT NULL,
  `shuttleStatus` enum('unpaid','paid') COLLATE latin1_general_ci NOT NULL,
  `residentIdnum` int(8) unsigned zerofill NOT NULL,
  PRIMARY KEY (`shuttleId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shuttle`
--

LOCK TABLES `shuttle` WRITE;
/*!40000 ALTER TABLE `shuttle` DISABLE KEYS */;
/*!40000 ALTER TABLE `shuttle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transient`
--

DROP TABLE IF EXISTS `transient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transient` (
  `transientIdnum` int(8) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `transientLname` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `transientFname` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `transientMobileno` varchar(45) COLLATE latin1_general_ci NOT NULL,
  `transientAddress` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `transientEmail` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `transientDateEntered` date NOT NULL,
  `transientRelation` enum('Mother','Father','Brother','Sister','Grandmother','Grandfather','Aunt','Uncle','Other') COLLATE latin1_general_ci DEFAULT NULL,
  `transientTotalAmount` decimal(10,2) NOT NULL,
  `transientAmountpaid` decimal(10,2) NOT NULL,
  `transientBalance` decimal(10,2) NOT NULL,
  `transientNoreservedRoom` int(10) NOT NULL,
  `transientReservedRoomNo` varchar(50) COLLATE latin1_general_ci NOT NULL,
  `transientArrival` date NOT NULL,
  `transientDeparture` date NOT NULL,
  `transientTotalNoDays` int(10) NOT NULL,
  `transientNoAdditionalGuest` int(10) DEFAULT NULL,
  `transientNamesAdditionalGuest` varchar(500) COLLATE latin1_general_ci DEFAULT NULL,
  `transientNoExtraBed` int(10) DEFAULT NULL,
  `transientBedcharge` decimal(10,2) DEFAULT NULL,
  `transientChargePerPerson` decimal(10,2) DEFAULT NULL,
  `transientStatus` enum('Checkout','Extend','Active') COLLATE latin1_general_ci DEFAULT NULL,
  `residentIdnum` int(8) unsigned zerofill DEFAULT NULL,
  `transientControlNumber` int(6) DEFAULT NULL,
  `transientRemarks` varchar(200) COLLATE latin1_general_ci DEFAULT NULL,
  `transientModeOfPayment` enum('cash','cheque','bd') COLLATE latin1_general_ci DEFAULT NULL,
  `adminIdnum` int(8) unsigned zerofill DEFAULT NULL,
  `transientDiscount` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`transientIdnum`),
  KEY `residentIdnum` (`residentIdnum`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transient`
--

LOCK TABLES `transient` WRITE;
/*!40000 ALTER TABLE `transient` DISABLE KEYS */;
/*!40000 ALTER TABLE `transient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transientrate`
--

DROP TABLE IF EXISTS `transientrate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transientrate` (
  `transientRegularRate` decimal(10,2) NOT NULL,
  `transientFamilyRate` decimal(10,2) NOT NULL,
  `transientRExtraBed` decimal(10,2) NOT NULL,
  `transientFExtraBedRate` decimal(10,2) NOT NULL,
  `transientPrivateSuite` decimal(10,2) NOT NULL,
  `transientPrivateExtraRate` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transientrate`
--

LOCK TABLES `transientrate` WRITE;
/*!40000 ALTER TABLE `transientrate` DISABLE KEYS */;
INSERT INTO `transientrate` VALUES (500.00,350.00,300.00,200.00,1750.00,1500.00);
/*!40000 ALTER TABLE `transientrate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `visitors`
--

DROP TABLE IF EXISTS `visitors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visitors` (
  `visitorsIdnum` int(8) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `visitorsName` varchar(100) COLLATE latin1_general_ci NOT NULL,
  `visitorsDate` date NOT NULL,
  `visitorsTimein` time NOT NULL,
  `visitorsTimeout` time DEFAULT NULL,
  `visitorsPurpose` enum('Inquire','Visit','School works','Personal Business','Others') COLLATE latin1_general_ci DEFAULT NULL,
  `residentIdnum` int(8) unsigned zerofill NOT NULL,
  `visitorTable` enum('resident','administrator','transient') COLLATE latin1_general_ci NOT NULL,
  `visitorsValidId` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `visitorsArea` varchar(100) COLLATE latin1_general_ci DEFAULT NULL,
  `visitorsStartDate` date DEFAULT NULL,
  PRIMARY KEY (`visitorsIdnum`),
  KEY `residentIdnum` (`residentIdnum`),
  KEY `residentIdnum_2` (`residentIdnum`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `visitors`
--

LOCK TABLES `visitors` WRITE;
/*!40000 ALTER TABLE `visitors` DISABLE KEYS */;
/*!40000 ALTER TABLE `visitors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `181nprdb`
--

USE `181nprdb`;

--
-- Final view structure for view `fingerprintowner`
--

/*!50001 DROP TABLE IF EXISTS `fingerprintowner`*/;
/*!50001 DROP VIEW IF EXISTS `fingerprintowner`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `fingerprintowner` AS select `resident`.`residentIdnum` AS `residentIdnum`,concat(`resident`.`residentFname`,' ',`resident`.`residentLname`) AS `resident_fullname`,`biometrics`.`fingerprintData1` AS `fingerprintData1`,`biometrics`.`fingerprintData2` AS `fingerprintData2`,`resident`.`status` AS `status` from (`resident` left join `biometrics` on((`resident`.`residentIdnum` = `biometrics`.`residentIdnum`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `groupvisitcount`
--

/*!50001 DROP TABLE IF EXISTS `groupvisitcount`*/;
/*!50001 DROP VIEW IF EXISTS `groupvisitcount`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `groupvisitcount` AS select `visitors`.`residentIdnum` AS `residentIdnum`,`visitors`.`visitorsStartDate` AS `visitorsStartDate`,`visitors`.`visitorsTimein` AS `visitorsTimein`,`visitors`.`visitorsTimeout` AS `visitorsTimeout`,`visitors`.`visitorsArea` AS `visitorsArea`,count(1) AS `visitorCount` from `visitors` group by `visitors`.`visitorsArea`,`visitors`.`residentIdnum` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `personperroom`
--

/*!50001 DROP TABLE IF EXISTS `personperroom`*/;
/*!50001 DROP VIEW IF EXISTS `personperroom`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `personperroom` AS select `resident`.`roomIdnum` AS `roomIdnum`,`room`.`roomNumber` AS `roomNumber`,count(1) AS `numberOfResident` from (`room` join `resident` on((`room`.`roomIdnum` = `resident`.`roomIdnum`))) where (not((`resident`.`status` like 'Leave'))) group by `resident`.`roomIdnum` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `residentnames`
--

/*!50001 DROP TABLE IF EXISTS `residentnames`*/;
/*!50001 DROP VIEW IF EXISTS `residentnames`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `residentnames` AS select concat(`resident`.`residentFname`,' ',`resident`.`residentLname`) AS `residentNames` from `resident` order by concat(`resident`.`residentFname`,' ',`resident`.`residentLname`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-05-31  0:32:09
