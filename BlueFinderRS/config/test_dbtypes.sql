-- MySQL dump 10.13  Distrib 5.5.32, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: dbtypes_yago_dbpedia
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
-- Table structure for table `dbtypes`
--

DROP TABLE IF EXISTS `dbtypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dbtypes` (
  `resource` blob NOT NULL,
  `type` blob NOT NULL,
  `id` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dbtypes`
--

LOCK TABLES `dbtypes` WRITE;
/*!40000 ALTER TABLE `dbtypes` DISABLE KEYS */;
INSERT INTO `dbtypes` VALUES ('France','<http://dbpedia.org/class/yago/OECDMemberEconomies>',89720),('France','<http://dbpedia.org/class/yago/WorldTradeOrganizationMemberEconomies>',89721),('France','<http://dbpedia.org/class/yago/EuropeanUnionMemberEconomies>',89722),('France','<http://dbpedia.org/class/yago/CountriesBorderingTheAtlanticOcean>',1655294),('France','<http://dbpedia.org/class/yago/CountriesOfTheMediterraneanSea>',1655296),('France','<http://dbpedia.org/class/yago/EuropeanUnionMemberStates>',1655297),('France','<http://dbpedia.org/class/yago/EuropeanCountries>',1655298),('France','<http://dbpedia.org/class/yago/French-speakingCountries>',1655299),('France','<http://dbpedia.org/class/yago/MemberStatesOfTheUnionForTheMediterranean>',1655301),('France','<http://dbpedia.org/class/yago/MemberStatesOfLaFrancophonie>',1655307),('France','<http://dbpedia.org/class/yago/CountriesOfTheIndianOcean>',1655308),('France','<http://dbpedia.org/class/yago/AlpineCountries>',1655309),('France','<http://dbpedia.org/ontology/Country>',10527341),('France','<http://schema.org/Country>',10527346),('France','<http://dbpedia.org/ontology/PopulatedPlace>',10527351),('France','<http://dbpedia.org/ontology/Place>',10527356),('France','<http://schema.org/Place>',10527361),('France','<http://www.w3.org/2002/07/owl#Thing>',10527366),('New_York_City','<http://dbpedia.org/class/yago/CitiesInNewYork>',671182),('New_York_City','<http://dbpedia.org/class/yago/YagoGeoEntity>',671183),('New_York_City','<http://dbpedia.org/class/yago/MetropolitanAreasOfTheUnitedStates>',671184),('New_York_City','<http://dbpedia.org/class/yago/GeoclassPopulatedPlace>',671185),('New_York_City','<http://dbpedia.org/class/yago/PopulatedPlacesEstablishedIn1624>',671186),('New_York_City','<http://dbpedia.org/class/yago/FormerCapitalsOfTheUnitedStates>',671187),('New_York_City','<http://dbpedia.org/class/yago/FormerNationalCapitals>',671188),('New_York_City','<http://dbpedia.org/class/yago/PopulatedPlacesOnTheHuDsonRiver>',671189),('New_York_City','<http://dbpedia.org/class/yago/FormerUnitedStatesStateCapitals>',671190),('New_York_City','<http://dbpedia.org/class/yago/GeoclassPopulatedPlace>',4424100),('New_York_City','<http://dbpedia.org/class/yago/Locations>',4424104),('New_York_City','<http://dbpedia.org/ontology/City>',11293869),('New_York_City','<http://schema.org/City>',11293874),('New_York_City','<http://dbpedia.org/ontology/Settlement>',11293879),('New_York_City','<http://dbpedia.org/ontology/PopulatedPlace>',11293884),('New_York_City','<http://dbpedia.org/ontology/Place>',11293889),('New_York_City','<http://schema.org/Place>',11293894),('New_York_City','<http://www.w3.org/2002/07/owl#Thing>',11293899),('Stephen_Johnson_Field','<http://dbpedia.org/class/yago/UnitedStatesFederalJuDgesAppointedByAbrahamLincoln>',848130),('Stephen_Johnson_Field','<http://dbpedia.org/class/yago/PeopleFromYuBACounty,California>',848131),('Stephen_Johnson_Field','<http://dbpedia.org/class/yago/WilliamsCollegeAlumni>',848132),('Stephen_Johnson_Field','<http://dbpedia.org/class/yago/Person100007846>',848133),('Stephen_Johnson_Field','<http://dbpedia.org/class/yago/PeopleOfCaliforniaInTheAmericanCivilWar>',848134),('Stephen_Johnson_Field','<http://dbpedia.org/class/yago/PeopleFromConnecticut>',848135),('Stephen_Johnson_Field','<http://dbpedia.org/class/yago/ChiefJusticesOfTheCaliforniaSupremeCourt>',848136),('Stephen_Johnson_Field','<http://dbpedia.org/ontology/Judge>',12425372),('Stephen_Johnson_Field','<http://dbpedia.org/ontology/Person>',12425377),('Stephen_Johnson_Field','<http://xmlns.com/foaf/0.1/Person>',12425382),('Stephen_Johnson_Field','<http://www.w3.org/2002/07/owl#Thing>',12425387),('Stephen_Johnson_Field','<http://schema.org/Person>',12425392),('Stephen_Johnson_Field','<http://dbpedia.org/ontology/Agent>',12425397),('Washington,_D.C.','<http://dbpedia.org/class/yago/ProposedStatesAndTerritoriesOfTheUnitedStates>',140059),('Washington,_D.C.','<http://dbpedia.org/class/yago/PopulatedPlacesEstablishedIn1790>',140060),('Washington,_D.C.','<http://dbpedia.org/class/yago/PopulatedPlacesOnThePotomacRiver>',140061),('Washington,_D.C.','<http://dbpedia.org/class/yago/CapitalsInNorthAmerica>',140062),('Washington,_D.C.','<http://dbpedia.org/class/yago/CapitalDistrictsAndTerritories>',140063),('Washington,_D.C.','<http://dbpedia.org/class/yago/PlannedCitiesInTheUnitedStates>',140064),('Washington,_D.C.','<http://dbpedia.org/class/yago/YagoGeoEntity>',140067),('Washington,_D.C.','<http://dbpedia.org/class/yago/StatesAndTerritoriesEstablishedIn1790>',140068),('Washington,_D.C.','<http://dbpedia.org/ontology/Settlement>',8368083),('Washington,_D.C.','<http://dbpedia.org/ontology/PopulatedPlace>',8368085),('Washington,_D.C.','<http://dbpedia.org/ontology/Place>',8368087),('Washington,_D.C.','<http://schema.org/Place>',8368089),('Washington,_D.C.','<http://www.w3.org/2002/07/owl#Thing>',8368091),('William_Kissam_Vanderbilt','<http://dbpedia.org/class/yago/PeopleFromNewport,RhodeIsland>',416029),('William_Kissam_Vanderbilt','<http://dbpedia.org/class/yago/AmericanRailroadExecutives>',416030),('William_Kissam_Vanderbilt','<http://dbpedia.org/class/yago/AmericanPeopleOfDutchDescent>',416031),('William_Kissam_Vanderbilt','<http://dbpedia.org/class/yago/Person100007846>',416032),('William_Kissam_Vanderbilt','<http://dbpedia.org/class/yago/FrenchRacehorseOwnersAndBreeders>',416033),('William_Kissam_Vanderbilt','<http://dbpedia.org/class/yago/AmericanRailwayEntrepreneurs>',416034),('William_Kissam_Vanderbilt','<http://dbpedia.org/class/yago/AmericanRacehorseOwnersAndBreeders>',416035),('William_Kissam_Vanderbilt_II','<http://dbpedia.org/class/yago/AmericanYachtRacers>',1091152),('William_Kissam_Vanderbilt_II','<http://dbpedia.org/class/yago/Person100007846>',1091153),('William_Kissam_Vanderbilt_II','<http://dbpedia.org/class/yago/St.Mark\'sSchoolAlumni>',1091154),('William_Kissam_Vanderbilt_II','<http://dbpedia.org/class/yago/AmericanRacecarDrivers>',1091155),('William_Kissam_Vanderbilt_II','<http://dbpedia.org/class/yago/HarvardUniversityAlumni>',1091156),('William_Kissam_Vanderbilt_II','<http://dbpedia.org/class/yago/AmericanRailroadExecutivesOfThe20thCentury>',1091157),('William_Kissam_Vanderbilt','<http://dbpedia.org/ontology/Person>',9987180),('William_Kissam_Vanderbilt','<http://xmlns.com/foaf/0.1/Person>',9987185),('William_Kissam_Vanderbilt','<http://www.w3.org/2002/07/owl#Thing>',9987192),('William_Kissam_Vanderbilt','<http://schema.org/Person>',9987197),('William_Kissam_Vanderbilt','<http://dbpedia.org/ontology/Agent>',9987202),('William_Kissam_Vanderbilt_II','<http://dbpedia.org/ontology/Person>',13749137),('William_Kissam_Vanderbilt_II','<http://xmlns.com/foaf/0.1/Person>',13749141),('William_Kissam_Vanderbilt_II','<http://www.w3.org/2002/07/owl#Thing>',13749145),('William_Kissam_Vanderbilt_II','<http://schema.org/Person>',13749149),('William_Kissam_Vanderbilt_II','<http://dbpedia.org/ontology/Agent>',13749153);
/*!40000 ALTER TABLE `dbtypes` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-10-16  9:45:30
