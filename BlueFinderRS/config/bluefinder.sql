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
