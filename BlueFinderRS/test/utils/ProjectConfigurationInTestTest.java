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
        prop.load(ProjectConfigurationReader.class.getClassLoader().getResourceAsStream("setup.properties"));
        
        File file = new File(ProjectConfigurationReader.class.getClassLoader().getResource("setup.properties").toURI());
        FileOutputStream out = new FileOutputStream(file);
                
        originalTestEnvironment = prop.getProperty("testEnvironment");
        prop.setProperty("testEnvironment", "true");
        prop.store(out, null);
        out.close();
    }
    
    @AfterClass
    public static void tearDown() throws URISyntaxException, FileNotFoundException, IOException {
        File file = new File(ProjectConfigurationReader.class.getClassLoader().getResource("setup.properties").toURI());
        FileOutputStream out = new FileOutputStream(file);
        
        prop.setProperty("testEnvironment", originalTestEnvironment);
        prop.store(out, null);
        out.close();
    }

    @Test
    public void testEnhanceTable() throws Exception {
        boolean expected = false;
        assertEquals(expected, ProjectConfigurationReader.enhanceTable());
    }

    @Test
    public void testTranslate() throws Exception {
        boolean expected = false;
        assertEquals(expected, ProjectConfigurationReader.translate());
    }

    @Test
    public void testTestEnvironment() throws Exception {
        Assert.assertTrue(ProjectConfigurationReader.testEnvironment());
    }

    @Test
    public void testDbpediaPrefix() throws Exception {
        ProjectConfigurationReader.useDefaultProperties();
        String expected = "http://dbpedia.org/resource/";
        assertEquals(expected, ProjectConfigurationReader.dbpediaPrefix());

        ProjectConfigurationReader.useProperties1();
        expected = "http://es.dbpedia.org/resource/";
        assertEquals(expected, ProjectConfigurationReader.dbpediaPrefix());

        ProjectConfigurationReader.useProperties2();
        expected = "http://fr.dbpedia.org/resource/";
        assertEquals(expected, ProjectConfigurationReader.dbpediaPrefix());
    }

    @Test
    public void testBlacklistFilename() throws Exception {
        String expected = "blacklist_category_default.txt";
        assertEquals(expected, ProjectConfigurationReader.blacklistFilename());
    }

    @Test
    public void testDbpediaTypeTable() throws Exception {
        String expected = "dbtypes";
        assertEquals(expected, ProjectConfigurationReader.dbpediaTypeTable());
    }

    @Test
    public void testLanguageCode() throws Exception {
        String expected = "en";
        assertEquals(expected, ProjectConfigurationReader.languageCode());
    }

    @Test
    public void testCategoryPrefix() throws Exception {
        String expected = "Category:";
        assertEquals(expected, ProjectConfigurationReader.categoryPrefix());
    }

    @Test
    public void testUseStarpath() throws Exception {
        assertEquals(Boolean.parseBoolean(prop.getProperty("USE_STARPATH")), ProjectConfigurationReader.useStarpath());
    }

    @Test
    public void testMultipleDatabases() throws Exception {
        boolean expected = false;
        assertEquals(expected, ProjectConfigurationReader.multipleDatabases());
    }

    @Test
    public void testFromToTable() throws Exception {
        String expected = "p06_associatedBand_";
        ProjectConfigurationReader.useProperties1();
        assertEquals(expected + "es", ProjectConfigurationReader.fromToTable());
        ProjectConfigurationReader.useProperties2();
        assertEquals(expected + "fr", ProjectConfigurationReader.fromToTable());
    }

    @Test
    public void testDbpediaLanguagePrefix() throws Exception {
        String expected = "http://dbpedia.org/resource/";
        assertEquals(expected, ProjectConfigurationReader.dbpediaLanguagePrefix());
    }

    @Test
    public void testResultDatabase() throws Exception {
        String key = prop.getProperty("testDatabase");
        ProjectConfigurationReader.useDefaultProperties();
        assertEquals(key, ProjectConfigurationReader.testDatabase());

        ProjectConfigurationReader.useProperties1();
        assertEquals(key + "_prop1", ProjectConfigurationReader.testDatabase());

        ProjectConfigurationReader.useProperties2();
        assertEquals(key + "_prop2", ProjectConfigurationReader.testDatabase());
    }

    @Test
    public void testTestDatabase() throws Exception {
        String expected = prop.getProperty("testDatabase");
        ProjectConfigurationReader.useDefaultProperties();
        assertEquals(expected, ProjectConfigurationReader.testDatabase());

        ProjectConfigurationReader.useProperties1();
        assertEquals(expected + "_prop1", ProjectConfigurationReader.testDatabase());

        ProjectConfigurationReader.useProperties2();
        assertEquals(expected + "_prop2", ProjectConfigurationReader.testDatabase());
    }

    @Test
    public void testTestDatabaseUser() throws Exception {
        ProjectConfigurationReader.useDefaultProperties();
        assertEquals(prop.getProperty("testDatabaseUser"), ProjectConfigurationReader.testDatabaseUser());

        ProjectConfigurationReader.useProperties1();
        assertEquals(prop.getProperty("testDatabaseUser"), ProjectConfigurationReader.testDatabaseUser());

        ProjectConfigurationReader.useProperties2();
        assertEquals(prop.getProperty("testDatabaseUser"), ProjectConfigurationReader.testDatabaseUser());
    }

    @Test
    public void testTestDatabasePassword() throws Exception {
        ProjectConfigurationReader.useDefaultProperties();
        assertEquals(prop.getProperty("testDatabasePass"), ProjectConfigurationReader.testDatabasePassword());

        ProjectConfigurationReader.useProperties1();
        assertEquals(prop.getProperty("testDatabasePass"), ProjectConfigurationReader.testDatabasePassword());

        ProjectConfigurationReader.useProperties2();
        assertEquals(prop.getProperty("testDatabasePass"), ProjectConfigurationReader.testDatabasePassword());
    }

    @Test
    public void testResultDatabaseUser() throws Exception {
        String expected = prop.getProperty("testDatabasePass");
        ProjectConfigurationReader.useDefaultProperties();
        assertEquals(expected, ProjectConfigurationReader.testDatabaseUser());

        ProjectConfigurationReader.useProperties1();
        assertEquals(expected, ProjectConfigurationReader.testDatabaseUser());

        ProjectConfigurationReader.useProperties2();
        assertEquals(expected, ProjectConfigurationReader.testDatabaseUser());
    }

    @Test
    public void testResultDatabasePassword() throws Exception {
        String expected = prop.getProperty("testDatabasePass");
        ProjectConfigurationReader.useDefaultProperties();
        assertEquals(expected, ProjectConfigurationReader.testDatabasePassword());

        ProjectConfigurationReader.useProperties1();
        assertEquals(expected, ProjectConfigurationReader.testDatabasePassword());

        ProjectConfigurationReader.useProperties2();
        assertEquals(expected, ProjectConfigurationReader.testDatabasePassword());
    }
}
