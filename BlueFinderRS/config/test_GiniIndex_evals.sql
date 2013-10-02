-- MySQL dump 10.13  Distrib 5.5.25a, for Linux (i686)
--
-- Host: 192.168.10.243    Database: dbresearch
-- ------------------------------------------------------
-- Server version	5.5.32-0ubuntu0.12.04.1

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
-- Table structure for table `test_sc1Evaluation`
--

DROP TABLE IF EXISTS `test_sc1Evaluation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test_sc1Evaluation` (
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
-- Dumping data for table `test_sc1Evaluation`
--

LOCK TABLES `test_sc1Evaluation` WRITE;
/*!40000 ALTER TABLE `test_sc1Evaluation` DISABLE KEYS */;
INSERT INTO `test_sc1Evaluation` VALUES (1,'Lex_Records , DJ_Signify 7795','(0.04545454545454547) Lex_Records , Boom_Bip (0.125) Lex_Records , Prince_Po (0.15384615384615385) Lex_Records , Jneiro_Jarel (0.17857142857142855) Lex_Records , Doseone (0.22402597402597402) Babygrande_Records , Dame_Grease (0.22857142857142854) Fat_Cat_Records , Hauschka (0.23214285714285715) Burning_Shed , Jan_Linton (0.23376623376623373) Cooking_Vinyl , Dawn_Landes (0.24230769230769228) Paw_Tracks , Eric_Copeland (0.24285714285714283) ATIC_Records , Niko_(musician) ','{#from / #to=1001}','{#from / #to=1002}','{#from / #to=1003}','{#from / #to=1004}','{#from / #to=1005}','{#from / #to=1006}','{#from / #to=1007}','{#from / #to=1008}','{#from / #to=1009}','{#from / #to=1010}',10548,'#from / #to'),(2,'Ultra_Records , Inna 7513','(0.11538461538461536) Ultra_Records , Kim_Sozzi (0.16666666666666669) Ultra_Records , Adrian_Lux (0.16666666666666669) Ultra_Records , Alexandra_Stan (0.16666666666666669) Ultra_Records , Lucas_Prata (0.16666666666666669) Ultra_Records , Wolfgang_Gartner (0.17857142857142855) Ultra_Records , Markus_Schulz (0.17857142857142855) Ultra_Records , Vassy_(singer) (0.1875) Ultra_Records , Gabry_Ponte (0.1875) Ultra_Records , Sharam (0.20588235294117646) Ultra_Records , Chicane_(recording_artist) ','{#from / * / Cat:#from_artists / #to=1001}','{#from / #to=1, #from / * / Cat:#from_artists / #to=1}','{#from / #to=2, #from / * / Cat:#from_artists / #to=2}','{#from / * / Cat:#from_artists / #to=3, #from / #to=2}','{#from / #to=3, #from / * / Cat:#from_artists / #to=3}','{#from / #to=4, #from / * / Cat:#from_artists / #to=3}','{#from / #to=4, #from / * / Cat:#from_artists / #to=4}','{#from / #to=5, #from / * / Cat:#from_artists / #to=4}','{#from / #to=6, #from / * / Cat:#from_artists / #to=4}','{#from / #to=7, #from / * / Cat:#from_artists / #to=4}',540,'#from / #to , #from / Cat:#from / Cat:#from_artists / #to'),(3,'Atlantic_Records , T-Bone_Walker 5744','(0.17857142857142855) Brunswick_Records , T-Bone_Walker (0.2142857142857143) Imperial_Records , T-Bone_Walker (0.21875) Capitol_Records , T-Bone_Walker (0.21875) Columbia_Records , T-Bone_Walker (0.23529411764705882) Reprise_Records , T-Bone_Walker (0.2592592592592593) Atlantic_Records , Tommy_Ridgley (0.2857142857142857) Atlantic_Records , Ryan_Cabrera (0.28846153846153844) Atlantic_Records , Stick_McGhee (0.29032258064516125) Atlantic_Records , Stephen_Stills (0.2931034482758621) Atlantic_Records , Woody_Lee ','{#from / * / Cat:Capitol_Records_artists / #to=1001}','{#from / * / Cat:Capitol_Records_artists / #to=1002, #from / * / Cat:Polydor_Records_artists / #to=1, #from / #to=1}','{#from / * / Cat:Capitol_Records_artists / #to=2, #from / * / Cat:#from_artists / #to=2, #from / * / Cat:Polydor_Records_artists / #to=1, #from / #to=1}','{#from / * / Cat:Capitol_Records_artists / #to=3, #from / * / Cat:#from_artists / #to=3, #from / * / Cat:Polydor_Records_artists / #to=1, #from / #to=1}','{#from / * / Cat:Capitol_Records_artists / #to=4, #from / * / Cat:#from_artists / #to=3, #from / * / Cat:Polydor_Records_artists / #to=1, #from / #to=1, #from / * / Cat:Atlantic_Records_artists / #to=1}','{#from / * / Cat:#from_artists / #to=5, #from / * / Cat:Capitol_Records_artists / #to=4, #from / * / Cat:Polydor_Records_artists / #to=1, #from / #to=1, #from / * / Cat:Atlantic_Records_artists / #to=1}','{#from / * / Cat:#from_artists / #to=7, #from / * / Cat:Capitol_Records_artists / #to=4, #from / * / List_of_current_#from_artists / #to=1, #from / * / Cat:Polydor_Records_artists / #to=1, #from / #to=1, #from / * / Cat:Atlantic_Records_artists / #to=1}','{#from / * / Cat:#from_artists / #to=9, #from / * / Cat:Capitol_Records_artists / #to=4, #from / * / List_of_current_#from_artists / #to=2, #from / #to=2, #from / * / Cat:Polydor_Records_artists / #to=1, #from / * / Cat:Atlantic_Records_artists / #to=1}','{#from / * / Cat:#from_artists / #to=11, #from / * / Cat:Capitol_Records_artists / #to=4, #from / * / List_of_current_#from_artists / #to=3, #from / #to=3, #from / * / Cat:Polydor_Records_artists / #to=1, #from / * / Cat:Atlantic_Records_artists / #to=1}','{#from / * / Cat:#from_artists / #to=13, #from / * / Cat:Capitol_Records_artists / #to=4, #from / * / List_of_current_#from_artists / #to=3, #from / #to=3, #from / * / Cat:Polydor_Records_artists / #to=1, #from / * / Cat:Atlantic_Records_artists / #to=1, #from / * / List_of_former_#from_artists / #to=1}',449,'#from / Cat:American_record_labels / Cat:Capitol_Records / Cat:Capitol_Records_artists / #to , #from / Cat:#from / Cat:#from_artists / #to , #from / Cat:Warner_Music_labels / Cat:#from / Cat:#from_artists / #to'),(4,'Curb_Records , Ronnie_McDowell 2179','(0.03125) Curb_Records , Ken_Mellons (0.08823529411764708) Curb_Records , Rodney_Atkins (0.09375) Curb_Records , Shane_McAnally (0.09375) Curb_Records , Steve_Holy (0.09375) Curb_Records , Trini_Triggs (0.125) Curb_Records , David_Kersh (0.125) Curb_Records , Lee_Greenwood (0.125) Curb_Records , Philip_Claypool (0.125) Curb_Records , Star_De_Azlan (0.13333333333333336) Curb_Records , Bill_LaBounty ','{#from / * / List_of_#from_artists / #to=1001}','{#from / * / List_of_#from_artists / #to=1002, #from / #to=1}','{#from / * / List_of_#from_artists / #to=1003, #from / #to=1}','{#from / * / List_of_#from_artists / #to=1004, #from / #to=2}','{#from / * / List_of_#from_artists / #to=1005, #from / #to=2}','{#from / * / List_of_#from_artists / #to=1006, #from / #to=2}','{#from / * / List_of_#from_artists / #to=1007, #from / #to=3}','{#from / * / List_of_#from_artists / #to=1008, #from / #to=3}','{#from / * / List_of_#from_artists / #to=1009, #from / #to=3}','{#from / * / List_of_#from_artists / #to=1010, #from / #to=3}',432,'#from / List_of_#from_artists / #to'),(5,'HIM_International_Music , Calvin_Chen 7769','(0.0357142857142857) HIM_International_Music , Aaron_Yan (0.07142857142857145) HIM_International_Music , Jiro_Wang (0.13333333333333336) HIM_International_Music , Wu_Chun (0.19444444444444442) HIM_International_Music , Selina_Jen (0.2368421052631579) HIM_International_Music , Ella_Chen (0.26315789473684215) HIM_International_Music , Tank_(Taiwanese_singer) (0.2647058823529412) HIM_International_Music , Yoga_Lin (0.2777777777777778) HIM_International_Music , Aska_Yang (0.28260869565217395) HIM_International_Music , Hebe_Tien (0.2894736842105263) HIM_International_Music , Kaira_Gong ','{#from / #to=1001}','{#from / #to=1002}','{#from / #to=1003}','{#from / #to=1004}','{#from / #to=1005}','{#from / #to=1006}','{#from / #to=1007}','{#from / #to=1008}','{#from / #to=1009}','{#from / #to=1010}',436,'#from / #to');
/*!40000 ALTER TABLE `test_sc1Evaluation` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-09-06 16:49:31
