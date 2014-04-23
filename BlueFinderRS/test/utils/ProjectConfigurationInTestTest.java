package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import org.junit.AfterClass;
import org.junit.Assert;


public class ProjectConfigurationInTestTest {

    private static Properties prop;
    private static String originalTestEnvironment;

    @BeforeClass
    public static void classSetUp() throws IOException, URISyntaxException {
        prop = new Properties();
        prop.load(ProjectConfiguration.class.getClassLoader().getResourceAsStream("setup.properties"));
        
        File file = new File(ProjectConfiguration.class.getClassLoader().getResource("setup.properties").toURI());
        FileOutputStream out = new FileOutputStream(file);
                
        originalTestEnvironment = prop.getProperty("testEnvironment");
        prop.setProperty("testEnvironment", "true");
        prop.store(out, null);
        out.close();
    }
    
    @AfterClass
    public static void tearDown() throws URISyntaxException, FileNotFoundException, IOException {
        File file = new File(ProjectConfiguration.class.getClassLoader().getResource("setup.properties").toURI());
        FileOutputStream out = new FileOutputStream(file);
        
        prop.setProperty("testEnvironment", originalTestEnvironment);
        prop.store(out, null);
        out.close();
    }

    @Test
    public void testEnhanceTable() throws Exception {
        boolean expected = false;
        assertEquals(expected, ProjectConfiguration.enhanceTable());
    }

    @Test
    public void testTranslate() throws Exception {
        boolean expected = false;
        assertEquals(expected, ProjectConfiguration.translate());
    }

    @Test
    public void testTestEnvironment() throws Exception {
        Assert.assertTrue(ProjectConfiguration.testEnvironment());
    }

    @Test
    public void testDbpediaPrefix() throws Exception {
        ProjectConfiguration.useDefaultProperties();
        String expected = "http://dbpedia.org/resource/";
        assertEquals(expected, ProjectConfiguration.dbpediaPrefix());

        ProjectConfiguration.useProperties1();
        expected = "http://es.dbpedia.org/resource/";
        assertEquals(expected, ProjectConfiguration.dbpediaPrefix());

        ProjectConfiguration.useProperties2();
        expected = "http://fr.dbpedia.org/resource/";
        assertEquals(expected, ProjectConfiguration.dbpediaPrefix());
    }

    @Test
    public void testBlacklistFilename() throws Exception {
        String expected = "blacklist_category_default.txt";
        assertEquals(expected, ProjectConfiguration.blacklistFilename());
    }

    @Test
    public void testDbpediaTypeTable() throws Exception {
        String expected = "dbtypes";
        assertEquals(expected, ProjectConfiguration.dbpediaTypeTable());
    }

    @Test
    public void testLanguageCode() throws Exception {
        String expected = "en";
        assertEquals(expected, ProjectConfiguration.languageCode());
    }

    @Test
    public void testCategoryPrefix() throws Exception {
        String expected = "Category:";
        assertEquals(expected, ProjectConfiguration.categoryPrefix());
    }

    @Test
    public void testUseStarpath() throws Exception {
        assertEquals(Boolean.parseBoolean(prop.getProperty("USE_STARPATH")), ProjectConfiguration.useStarpath());
    }

    @Test
    public void testMultipleDatabases() throws Exception {
        boolean expected = false;
        assertEquals(expected, ProjectConfiguration.multipleDatabases());
    }

    @Test
    public void testFromToTable() throws Exception {
        String expected = "p06_associatedBand_";
        ProjectConfiguration.useProperties1();
        assertEquals(expected + "es", ProjectConfiguration.fromToTable());
        ProjectConfiguration.useProperties2();
        assertEquals(expected + "fr", ProjectConfiguration.fromToTable());
    }

    @Test
    public void testDbpediaLanguagePrefix() throws Exception {
        String expected = "http://dbpedia.org/resource/";
        assertEquals(expected, ProjectConfiguration.dbpediaLanguagePrefix());
    }

    @Test
    public void testResultDatabase() throws Exception {
        String key = prop.getProperty("testDatabase");
        ProjectConfiguration.useDefaultProperties();
        assertEquals(key, ProjectConfiguration.testDatabase());

        ProjectConfiguration.useProperties1();
        assertEquals(key + "_prop1", ProjectConfiguration.testDatabase());

        ProjectConfiguration.useProperties2();
        assertEquals(key + "_prop2", ProjectConfiguration.testDatabase());
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
        String expected = prop.getProperty("testDatabasePass");
        ProjectConfiguration.useDefaultProperties();
        assertEquals(expected, ProjectConfiguration.testDatabaseUser());

        ProjectConfiguration.useProperties1();
        assertEquals(expected, ProjectConfiguration.testDatabaseUser());

        ProjectConfiguration.useProperties2();
        assertEquals(expected, ProjectConfiguration.testDatabaseUser());
    }

    @Test
    public void testResultDatabasePassword() throws Exception {
        String expected = prop.getProperty("testDatabasePass");
        ProjectConfiguration.useDefaultProperties();
        assertEquals(expected, ProjectConfiguration.testDatabasePassword());

        ProjectConfiguration.useProperties1();
        assertEquals(expected, ProjectConfiguration.testDatabasePassword());

        ProjectConfiguration.useProperties2();
        assertEquals(expected, ProjectConfiguration.testDatabasePassword());
    }
}
