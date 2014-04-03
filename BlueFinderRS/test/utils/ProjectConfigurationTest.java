package utils;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;


public class ProjectConfigurationTest {

    private static Properties prop;

    @BeforeClass
    public static void classSetUp() throws IOException {
        prop = new Properties();
        prop.load(ProjectConfiguration.class.getClassLoader().getResourceAsStream("setup.properties"));
    }

    private boolean testEnvironment() {
        return Boolean.parseBoolean(prop.getProperty("testEnvironment"));
    }

    @Test
    public void testEnhanceTable() throws Exception {
        boolean expected;
        if (testEnvironment())
            expected = false;
        else
            expected = Boolean.parseBoolean(prop.getProperty("enhanceTable"));
        assertEquals(expected, ProjectConfiguration.enhanceTable());
    }

    @Test
    public void testTranslate() throws Exception {
        boolean expected;
        if (testEnvironment())
            expected = false;
        else
            expected = Boolean.parseBoolean(prop.getProperty("translate"));
        assertEquals(expected, ProjectConfiguration.translate());
    }

    @Test
    public void testTestEnvironment() throws Exception {
        assertEquals(testEnvironment(), ProjectConfiguration.testEnvironment());
    }

    @Test
    public void testDbpediaPrefix() throws Exception {
        if (testEnvironment()) {
            ProjectConfiguration.useDefaultProperties();
            String expected = "http://dbpedia.org/resource/";
            assertEquals(expected, ProjectConfiguration.dbpediaPrefix());

            ProjectConfiguration.useProperties1();
            expected = "http://es.dbpedia.org/resource/";
            assertEquals(expected, ProjectConfiguration.dbpediaPrefix());

            ProjectConfiguration.useProperties2();
            expected = "http://fr.dbpedia.org/resource/";
            assertEquals(expected, ProjectConfiguration.dbpediaPrefix());
        } else {
            ProjectConfiguration.useDefaultProperties();
            assertEquals(prop.getProperty("DBPEDIA_PREFIX"), ProjectConfiguration.dbpediaPrefix());

            ProjectConfiguration.useProperties1();
            assertEquals(prop.getProperty("DBPEDIA_PREFIX1"), ProjectConfiguration.dbpediaPrefix());

            ProjectConfiguration.useProperties2();
            assertEquals(prop.getProperty("DBPEDIA_PREFIX2"), ProjectConfiguration.dbpediaPrefix());
        }
    }

    @Test
    public void testBlacklistFilename() throws Exception {
        String expected;
        if (testEnvironment())
            expected = "blacklist_category_default.txt";
        else
            expected = prop.getProperty("BLACKLIST_FILENAME");
        assertEquals(expected, ProjectConfiguration.blacklistFilename());
    }

    @Test
    public void testDbpediaTypeTable() throws Exception {
        String expected;
        if (testEnvironment())
            expected = "dbtypes";
        else
            expected = prop.getProperty("DBPEDIA_TYPE_TABLE");
        assertEquals(expected, ProjectConfiguration.dbpediaTypeTable());
    }

    @Test
    public void testLanguageCode() throws Exception {
        String expected;
        if (testEnvironment())
            expected = "en";
        else
            expected = prop.getProperty("LANGUAGE_CODE");
        assertEquals(expected, ProjectConfiguration.languageCode());
    }

    @Test
    public void testCategoryPrefix() throws Exception {
        String expected;
        if (testEnvironment())
            expected = "Category:";
        else
            expected = prop.getProperty("CATEGORY_PREFIX");
        assertEquals(expected, ProjectConfiguration.categoryPrefix());
    }

    @Test
    public void testUseStarpath() throws Exception {
        assertEquals(Boolean.parseBoolean(prop.getProperty("USE_STARPATH")), ProjectConfiguration.useStarpath());
    }

    @Test
    public void testMultipleDatabases() throws Exception {
        boolean expected;
        if (testEnvironment())
            expected = false;
        else
            expected = Boolean.parseBoolean(prop.getProperty("MULTIPLE_DATABASES"));
        assertEquals(expected, ProjectConfiguration.multipleDatabases());
    }

    @Test
    public void testFromToTable() throws Exception {
        if (testEnvironment()) {
            String expected = "p06_associatedBand_";
            ProjectConfiguration.useProperties1();
            assertEquals(expected + "es", ProjectConfiguration.fromToTable());
            ProjectConfiguration.useProperties2();
            assertEquals(expected + "fr", ProjectConfiguration.fromToTable());
        } else {
            String key = "FROMTO_TABLE";
            ProjectConfiguration.useProperties1();
            assertEquals(prop.getProperty(key + "1"), ProjectConfiguration.fromToTable());
            ProjectConfiguration.useProperties2();
            assertEquals(prop.getProperty(key + "2"), ProjectConfiguration.fromToTable());
        }
    }

    @Test
    public void testDbpediaLanguagePrefix() throws Exception {
        String expected;
        if (testEnvironment())
            expected = "http://dbpedia.org/resource/";
        else
            expected = prop.getProperty("DBPEDIA_LANGUAGE_PREFIX");
        assertEquals(expected, ProjectConfiguration.dbpediaLanguagePrefix());
    }

    @Test
    public void testResultDatabase() throws Exception {
        String key;
        if (testEnvironment()) {
            key = prop.getProperty("testDatabase");
            ProjectConfiguration.useDefaultProperties();
            assertEquals(key, ProjectConfiguration.testDatabase());

            ProjectConfiguration.useProperties1();
            assertEquals(key + "_prop1", ProjectConfiguration.testDatabase());

            ProjectConfiguration.useProperties2();
            assertEquals(key + "_prop2", ProjectConfiguration.testDatabase());
        } else {
            key = "resultDatabase";
            ProjectConfiguration.useDefaultProperties();
            assertEquals(prop.getProperty(key), ProjectConfiguration.resultDatabase());

            ProjectConfiguration.useProperties1();
            assertEquals(prop.getProperty(key + "1"), ProjectConfiguration.resultDatabase());

            ProjectConfiguration.useProperties2();
            assertEquals(prop.getProperty(key + "2"), ProjectConfiguration.resultDatabase());
        }
    }

    @Test
    public void testTestDatabase() throws Exception {
        String expected = prop.getProperty("testDatabase");
        ProjectConfiguration.useDefaultProperties();
        assertEquals(expected, ProjectConfiguration.testDatabase());

        ProjectConfiguration.useProperties1();
        assertEquals(expected + "_prop1", ProjectConfiguration.testDatabase());

        ProjectConfiguration.useProperties2();
        assertEquals(expected + "_prop2", ProjectConfiguration.testDatabase());
    }

    @Test
    public void testTestDatabaseUser() throws Exception {
        ProjectConfiguration.useDefaultProperties();
        assertEquals(prop.getProperty("testDatabaseUser"), ProjectConfiguration.testDatabaseUser());

        ProjectConfiguration.useProperties1();
        assertEquals(prop.getProperty("testDatabaseUser"), ProjectConfiguration.testDatabaseUser());

        ProjectConfiguration.useProperties2();
        assertEquals(prop.getProperty("testDatabaseUser"), ProjectConfiguration.testDatabaseUser());
    }

    @Test
    public void testTestDatabasePassword() throws Exception {
        ProjectConfiguration.useDefaultProperties();
        assertEquals(prop.getProperty("testDatabasePass"), ProjectConfiguration.testDatabasePassword());

        ProjectConfiguration.useProperties1();
        assertEquals(prop.getProperty("testDatabasePass"), ProjectConfiguration.testDatabasePassword());

        ProjectConfiguration.useProperties2();
        assertEquals(prop.getProperty("testDatabasePass"), ProjectConfiguration.testDatabasePassword());
    }

    @Test
    public void testResultDatabaseUser() throws Exception {
        if (testEnvironment()) {
            String expected = prop.getProperty("testDatabasePass");
            ProjectConfiguration.useDefaultProperties();
            assertEquals(expected, ProjectConfiguration.testDatabaseUser());

            ProjectConfiguration.useProperties1();
            assertEquals(expected, ProjectConfiguration.testDatabaseUser());

            ProjectConfiguration.useProperties2();
            assertEquals(expected, ProjectConfiguration.testDatabaseUser());
        } else {
            String key = "resultDatabaseUser";
            ProjectConfiguration.useDefaultProperties();
            assertEquals(prop.getProperty(key), ProjectConfiguration.resultDatabaseUser());

            ProjectConfiguration.useProperties1();
            assertEquals(prop.getProperty(key + "1"), ProjectConfiguration.resultDatabaseUser());

            ProjectConfiguration.useProperties2();
            assertEquals(prop.getProperty(key + "2"), ProjectConfiguration.resultDatabaseUser());
        }
    }

    @Test
    public void testResultDatabasePassword() throws Exception {
        if (testEnvironment()) {
            String expected = prop.getProperty("testDatabasePass");
            ProjectConfiguration.useDefaultProperties();
            assertEquals(expected, ProjectConfiguration.testDatabasePassword());

            ProjectConfiguration.useProperties1();
            assertEquals(expected, ProjectConfiguration.testDatabasePassword());

            ProjectConfiguration.useProperties2();
            assertEquals(expected, ProjectConfiguration.testDatabasePassword());
        } else {
            String key = "resultDatabasePass";
            ProjectConfiguration.useDefaultProperties();
            assertEquals(prop.getProperty(key), ProjectConfiguration.resultDatabasePassword());

            ProjectConfiguration.useProperties1();
            assertEquals(prop.getProperty(key + "1"), ProjectConfiguration.resultDatabasePassword());

            ProjectConfiguration.useProperties2();
            assertEquals(prop.getProperty(key + "2"), ProjectConfiguration.resultDatabasePassword());
        }
    }

    @Test
    public void testLanguage() throws Exception {
        String expected;
        if (testEnvironment())
            expected = "http://dbpedia.org/resource/";
        else
            expected = prop.getProperty("LANGUAGE");
        assertEquals(expected, ProjectConfiguration.language(""));
    }
}
