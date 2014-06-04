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


public class ProjectConfigurationNotInTestTest {

    private static Properties prop;
    private static String originalTestEnvironment;

    @BeforeClass
    public static void classSetUp() throws IOException, URISyntaxException {
        prop = new Properties();
        prop.load(ProjectConfigurationReader.class.getClassLoader().getResourceAsStream("setup.properties"));
        
        File file = new File(ProjectConfigurationReader.class.getClassLoader().getResource("setup.properties").toURI());
        FileOutputStream out = new FileOutputStream(file);
                
        originalTestEnvironment = prop.getProperty("testEnvironment");
        prop.setProperty("testEnvironment", "false");
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
        boolean expected = Boolean.parseBoolean(prop.getProperty("enhanceTable"));
        assertEquals(expected, ProjectConfigurationReader.enhanceTable());
    }

    @Test
    public void testTranslate() throws Exception {
        boolean expected = Boolean.parseBoolean(prop.getProperty("TRANSLATE"));
        assertEquals(expected, ProjectConfigurationReader.translate());
    }

    @Test
    public void testTestEnvironment() throws Exception {
        Assert.assertFalse(ProjectConfigurationReader.testEnvironment());
    }

    @Test
    public void testDbpediaPrefix() throws Exception {        
        ProjectConfigurationReader.useDefaultProperties();
        assertEquals(prop.getProperty("DBPEDIA_PREFIX"), ProjectConfigurationReader.dbpediaPrefix());

        ProjectConfigurationReader.useProperties1();
        assertEquals(prop.getProperty("DBPEDIA_PREFIX1"), ProjectConfigurationReader.dbpediaPrefix());

        ProjectConfigurationReader.useProperties2();
        assertEquals(prop.getProperty("DBPEDIA_PREFIX2"), ProjectConfigurationReader.dbpediaPrefix());
    }

    @Test
    public void testBlacklistFilename() throws Exception {
        String expected = prop.getProperty("BLACKLIST_FILENAME");
        assertEquals(expected, ProjectConfigurationReader.blacklistFilename());
    }

    @Test
    public void testDbpediaTypeTable() throws Exception {
        String expected = prop.getProperty("DBPEDIA_TYPE_TABLE");
        assertEquals(expected, ProjectConfigurationReader.dbpediaTypeTable());
    }

    @Test
    public void testLanguageCode() throws Exception {
        String expected = prop.getProperty("LANGUAGE_CODE");
        assertEquals(expected, ProjectConfigurationReader.languageCode());
    }

    @Test
    public void testCategoryPrefix() throws Exception {
        String expected = prop.getProperty("CATEGORY_PREFIX");
        assertEquals(expected, ProjectConfigurationReader.categoryPrefix());
    }

    @Test
    public void testUseStarpath() throws Exception {
        assertEquals(Boolean.parseBoolean(prop.getProperty("USE_STARPATH")), ProjectConfigurationReader.useStarpath());
    }

    @Test
    public void testMultipleDatabases() throws Exception {
        boolean expected = Boolean.parseBoolean(prop.getProperty("MULTIPLE_DATABASES"));
        assertEquals(expected, ProjectConfigurationReader.multipleDatabases());
    }

    @Test
    public void testFromToTable() throws Exception {        
        String key = "FROMTO_TABLE";
        ProjectConfigurationReader.useProperties1();
        assertEquals(prop.getProperty(key + "1"), ProjectConfigurationReader.fromToTable());
        ProjectConfigurationReader.useProperties2();
        assertEquals(prop.getProperty(key + "2"), ProjectConfigurationReader.fromToTable());
    }

    @Test
    public void testDbpediaLanguagePrefix() throws Exception {
        String expected = prop.getProperty("DBPEDIA_LANGUAGE_PREFIX");
        assertEquals(expected, ProjectConfigurationReader.dbpediaLanguagePrefix());
    }

    @Test
    public void testResultDatabase() throws Exception {
        String key = "resultDatabase";
        ProjectConfigurationReader.useDefaultProperties();
        assertEquals(prop.getProperty(key), ProjectConfigurationReader.resultDatabase());

        ProjectConfigurationReader.useProperties1();
        assertEquals(prop.getProperty(key + "1"), ProjectConfigurationReader.resultDatabase());

        ProjectConfigurationReader.useProperties2();
        assertEquals(prop.getProperty(key + "2"), ProjectConfigurationReader.resultDatabase());
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
        String key = "resultDatabaseUser";
        ProjectConfigurationReader.useDefaultProperties();
        assertEquals(prop.getProperty(key), ProjectConfigurationReader.resultDatabaseUser());

        ProjectConfigurationReader.useProperties1();
        assertEquals(prop.getProperty(key + "1"), ProjectConfigurationReader.resultDatabaseUser());

        ProjectConfigurationReader.useProperties2();
        assertEquals(prop.getProperty(key + "2"), ProjectConfigurationReader.resultDatabaseUser());
    }

    @Test
    public void testResultDatabasePassword() throws Exception {
        String key = "resultDatabasePass";
        ProjectConfigurationReader.useDefaultProperties();
        assertEquals(prop.getProperty(key), ProjectConfigurationReader.resultDatabasePassword());

        ProjectConfigurationReader.useProperties1();
        assertEquals(prop.getProperty(key + "1"), ProjectConfigurationReader.resultDatabasePassword());

        ProjectConfigurationReader.useProperties2();
        assertEquals(prop.getProperty(key + "2"), ProjectConfigurationReader.resultDatabasePassword());
    }
}
