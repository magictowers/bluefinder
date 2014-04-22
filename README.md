bluefinder
==========

BlueFinder recommender system project

Ejecutar BlueFinder
-------------------
Posibles configuraciones para el setup.properties:
* Se corre el algoritmo en modo test, configurando la propiedad *testEnvironment*. También se deben setear las propiedades de la base de datos de test, el usuario y la contraseña.
* La propiedad *USE_STARPATH* se usa para indicar si los paths generados serán simplificados o no. Por ejemplo, si éste está en `true`, un path resultante sería "#from / Cat:#from / Cat:People_from_#from / #to", en cambio si está en cualquier otro valor, el path equivalente al anterior sería "#from / Cat:#from / Cat:People_from_#from / #to".
* La propiedad *DBPEDIA_PREFIX* es usado a la hora de generar los caminos para los recursos.
* *BLACKLIST_FILENAME* es el nombre del archivo que va a contener una lista de los prefijos de las páginas no deseadas de Wikipedia.
* La propiedad *TRANSLATE*
* La propiedad *CREATE_ENHANCED_TABLE* es usada a la hora de generar recomendaciones. La primera vez se tendrá que configurar en `true`, y la ejecución tardará más. En las ejecuciones siguientes que se realicen sobre el mismo conjunto de datos de la base de datos ya se puede configurar en `false`.
* Las propiedades relacionadas con la base de datos 1 y 2 se usan sólo para cuando se realiza la comparación de dos resultados, con el EvaluationComparator. Además de las opciones básicas para setear la base de datos, el usuario y contraseña, también se deben configurar la tabla desde donde se recuperan las tuplas (*FROMTO_TABLE<X>*), y el prefijo de DBpedia que estas tendrán (*DBPEDIA_PREFIX<x>*).
