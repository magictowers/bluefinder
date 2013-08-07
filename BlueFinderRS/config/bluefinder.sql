-- MySQL dump 10.13  Distrib 5.5.29, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: test_bluefinder
-- ------------------------------------------------------
-- Server version	5.5.29-0ubuntu0.12.04.2

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
-- Table structure for table `NFPC`
--

DROP TABLE IF EXISTS `NFPC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NFPC` (
  `id` int(3) NOT NULL AUTO_INCREMENT,
  `v_from` varchar(800) CHARACTER SET utf8 NOT NULL,
  `u_to` varchar(800) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `U_page`
--

DROP TABLE IF EXISTS `U_page`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `U_page` (
  `id` int(3) NOT NULL AUTO_INCREMENT,
  `page` BLOB NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `UxV`
--

DROP TABLE IF EXISTS `UxV`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UxV` (
  `id` int(3) NOT NULL AUTO_INCREMENT,
  `u_from` int(3) NOT NULL,
  `v_to` int(3) NOT NULL,
  `description` varchar(800) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `V_NormXStarNorm`
--

DROP TABLE IF EXISTS `V_NormXStarNorm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `V_NormXStarNorm` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `v_norm_id` int(11) NOT NULL,
  `star_norm_id` int(11) NOT NULL,
  `comment` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `V_Normalized`
--

DROP TABLE IF EXISTS `V_Normalized`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `V_Normalized` (
  `id` int(3) NOT NULL AUTO_INCREMENT,
  `path` longtext CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`),
  KEY `path` (`path`(100)) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `q_pairs`
--

DROP TABLE IF EXISTS `q_pairs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `q_pairs` (
  `from` varchar(300) DEFAULT NULL,
  `to` varchar(300) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;



--
-- Table structure for table `query_pairs`
--

DROP TABLE IF EXISTS `query_pairs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `query_pairs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from` varchar(200) DEFAULT NULL,
  `to` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-07-01 15:28:43

DROP TABLE IF EXISTS `dbtypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dbtypes` (
  `resource` blob NOT NULL,
  `type` blob NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `resource` (`resource`(15))
) ENGINE=MyISAM AUTO_INCREMENT=21395741 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dbtypes`
--
LOCK TABLES `dbtypes` WRITE;
/*!40000 ALTER TABLE `dbtypes` DISABLE KEYS */;
INSERT INTO `dbtypes` VALUES ('First_Jewish–Roman_War','<http://dbpedia.org/class/yago/1st-centuryConflicts>',21395748),('Autism','<http://www.w3.org/2002/07/owl#Thing>',21395747),('Autism','<http://dbpedia.org/ontology/Disease>',21395746),('Diego_Torres','<http://dbpedia.org/class/yago/ArgentinePopSingers>',21395741),('Diego_Torres','<http://dbpedia.org/class/yago/PeopleFromBuEnosAires>',21395742),('Diego_Torres','<http://dbpedia.org/class/yago/Actor109765278>',21395743),('Diego_Torres','<http://dbpedia.org/class/yago/LivingPeople>',21395744),('Diego_Torres','<http://dbpedia.org/class/yago/ArgentinePeopleOfItalianDescent>',21395745),('Rosario,_Santa_Fe','<http://dbpedia.org/class/yago/YagoGeoEntity>',21395749),('Rosario,_Santa_Fe','<http://dbpedia.org/class/yago/PopulatedPlacesInSantaFeProvince>',21395750);
/*!40000 ALTER TABLE `dbtypes` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

DROP TABLE IF EXISTS `U_pageEnhanced`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `U_pageEnhanced` (
  `id` int(11) NOT NULL,
  `page` blob NOT NULL,
  `subjectTypes` blob NOT NULL,
  `objectTypes` blob NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS `resultsTestKNN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resultsTestKNN` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resource` blob,
  `related_resources` blob,
  `1path` text,
  `2path` text,
  `3path` text,
  `4path` text,
  `5path` text,
  `6path` text,
  `7path` text,
  `8path` text,
  `9path` text,
  `10path` text,
  `time` bigint(20) DEFAULT NULL,
  `relevantPaths` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `resultsTestKNN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resultsTestKNN` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resource` blob,
  `related_resources` blob,
  `1path` text,
  `2path` text,
  `3path` text,
  `4path` text,
  `5path` text,
  `6path` text,
  `7path` text,
  `8path` text,
  `9path` text,
  `10path` text,
  `time` bigint(20) DEFAULT NULL,
  `relevantPaths` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resultsTestKNN`
--
-- WHERE:  1 limit 2

LOCK TABLES `resultsTestKNN` WRITE;
/*!40000 ALTER TABLE `resultsTestKNN` DISABLE KEYS */;
INSERT INTO `resultsTestKNN` VALUES (1,'Grand_Central_Records , Aim_(musician) 3088','(0.11538461538461536) ATIC_Records , Aim_(musician) (0.1923076923076923) Grand_Central_Records , Niko_(musician) (0.2) Grand_Central_Records , Kate_Rogers (0.20588235294117646) Grand_Central_Records , Riton_(musician) (0.2142857142857143) Grand_Central_Records , Broadway_Project (0.2142857142857143) Grand_Central_Records , Mark_Rae (0.23376623376623373) Planet_Mu , The_Gasman (0.25757575757575757) Planet_Mu , Jega_(musician) (0.2582417582417582) Skam_Records , Darrell_Fitton (0.26785714285714285) Warp_(record_label) , Bibio ','{#from / #to=1001}','{#from / #to=1002}','{#from / #to=1003}','{#from / #to=1004}','{#from / #to=1005}','{#from / #to=1006}','{#from / #to=1007}','{#from / #to=1008}','{#from / #to=1009, #from / * / Cat:Musicians_from_Manchester / #to=1}','{#from / #to=1010, #from / * / Cat:Musicians_from_Manchester / #to=1}',960,'#from / #to'),(2,'Grand_Hustle_Records , Iggy_Azalea 3446','(0.0) Grand_Hustle_Records , Killer_Mike (0.0) Grand_Hustle_Records , Ricco_Barrino (0.0) Grand_Hustle_Records , Trae_Tha_Truth (0.08333333333333331) Slip-n-Slide_Records , Qwote (0.125) Asylum_Records , Bob_Woodruff_(singer) (0.125) Asylum_Records , David_Blue_(musician) (0.125) Asylum_Records , Ed_Sheeran (0.125) Asylum_Records , Gucci_Mane (0.125) Asylum_Records , Ironik (0.125) Asylum_Records , Johnny_Lee_(singer) ','{#from / #to=1001, #from / * / Cat:Atlantic_Records_artists / #to=1001}','{#from / #to=1002, #from / * / Cat:Atlantic_Records_artists / #to=1}','{#from / #to=1003, #from / * / Cat:Atlantic_Records_artists / #to=1}','{#from / #to=1004, #from / * / Cat:Atlantic_Records_artists / #to=1}','{#from / #to=1005, #from / * / Cat:#from_artists / #to=1, #from / * / Cat:Atlantic_Records_artists / #to=1}','{#from / #to=5, #from / * / Cat:#from_artists / #to=1, #from / * / Cat:Atlantic_Records_artists / #to=1, #from / * / List_of_#from_artists / #to=1}','{#from / #to=6, #from / * / Cat:Atlantic_Records_artists / #to=2, #from / * / List_of_#from_artists / #to=2, #from / * / Cat:#from_artists / #to=1}','{#from / #to=6, #from / * / List_of_#from_artists / #to=3, #from / * / Cat:#from_artists / #to=2, #from / * / Cat:Atlantic_Records_artists / #to=2, #from / * / Cat:Tommy_Boy_Records_artists / #to=1}','{#from / #to=6, #from / * / List_of_#from_artists / #to=4, #from / * / Cat:#from_artists / #to=2, #from / * / Cat:Atlantic_Records_artists / #to=2, #from / * / Cat:Tommy_Boy_Records_artists / #to=1}','{#from / #to=6, #from / * / List_of_#from_artists / #to=4, #from / * / Cat:#from_artists / #to=3, #from / * / Cat:Atlantic_Records_artists / #to=2, #from / * / Cat:Tommy_Boy_Records_artists / #to=1}',427,'#from / #to , #from / Cat:Warner_W / #to');
/*!40000 ALTER TABLE `resultsTestKNN` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-08-05 14:03:20
