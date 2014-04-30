bluefinder
==========

BlueFinder recommender system project

Ejecutar BlueFinder
-------------------
Possible configurations for the setup.properties:
* The algorithm can be run in test mode if the property *testEnvironment* is configured to `true`. Also the database for test mode must be configured, and its username and password.
* The property *USE_STARPATH* is used to indicate if the generated paths will be simplified or not.For example, if it's `true`, the generated path will be "#from / Cat:#from / Cat:People_from_#from / #to", while in any other case will be "#from / * / Cat:People_from_#from / #to".
* The property *DBPEDIA_PREFIX* is used when the paths for the resources are generated. It must match with the DBpedia prefixes of the tuples that were retrieved.
* *BLACKLIST_FILENAME* is the name of the text file that contains all the prefixes of the undesired pages of Wikipedia.
* The property *TRANSLATE* indicates if the results will be translated or considered in English. The translation language depends on the chosen Wikipedia database.
* The property *CREATE_ENHANCED_TABLE* is used to generate recommendations. The first time should be configured to `true`, and the execution will take more time. In the following executions that runs using the same samples, the property can be set to `false`.
* The properties related to the database 1 and 2 will only be used when a comparison between two results is done, with the EvaluationComparator. Other properties to be configured are *FROMTO_TABLE<X>* that indicates the database table from which the tuples are retrieved, and *DBPEDIA_PREFIX<X>*, that will contain the corresponding prefix of the elements in the tuple.
