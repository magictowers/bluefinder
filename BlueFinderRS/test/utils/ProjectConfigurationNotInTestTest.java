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
        prop.load(ProjectConfiguration.class.getClassLoader().getResourceAsStream("setup.properties"));
        
        File file = new File(ProjectConfiguration.class.getClassLoader().getResource("setup.properties").toURI());
        FileOutputStream out = new FileOutputStream(file);
                
        originalTestEnvironment = prop.getProperty("testEnvironment");
        prop.setProperty("testEnvironment", "false");
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
        boolean expected = Boolean.parseBoolean(prop.getProperty("enhanceTable"));
        assertEquals(expected, ProjectConfiguration.enhanceTable());
    }

    @Test
    public void testTranslate() throws Exception {
        boolean expected = Boolean.parseBoolean(prop.getProperty("TRANSLATE"));
        assertEquals(expected, ProjectConfiguration.translate());
    }

    @Test
    public void testTestEnvironment() throws Exception {
        Assert.assertFalse(ProjectConfiguration.testEnvironment());
    }

    @Test
    public void testDbpediaPrefix() throws Exception {        
        ProjectConfiguration.useDefaultProperties();
        assertEquals(prop.getProperty("DBPEDIA_PREFIX"), ProjectConfiguration.dbpediaPrefix());

        ProjectConfiguration.useProperties1();
        assertEquals(prop.getProperty("DBPEDIA_PREFIX1"), ProjectConfiguration.dbpediaPrefix());

        ProjectConfiguration.useProperties2();
        assertEquals(prop.getProperty("DBPEDIA_PREFIX2"), ProjectConfiguration.dbpediaPrefix());
    }

    @Test
    public void testBlacklistFilename() throws Exception {
        String expected = prop.getProperty("BLACKLIST_FILENAME");
        assertEquals(expected, ProjectConfiguration.blacklistFilename());
    }

    @Test
    public void testDbpediaTypeTable() throws Exception {
        String expected = prop.getProperty("DBPEDIA_TYPE_TABLE");
        assertEquals(expected, ProjectConfiguration.dbpediaTypeTable());
    }

    @Test
    public void testLanguageCode() throws Exception {
        String expected = prop.getProperty("LANGUAGE_CODE");
        assertEquals(expected, ProjectConfiguration.languageCode());
    }

    @Test
    public void testCategoryPrefix() throws Exception {
        String expected = prop.getProperty("CATEGORY_PREFIX");
        assertEquals(expected, ProjectConfiguration.categoryPrefix());
    }

    @Test
    public void testUseStarpath() throws Exception {
        assertEquals(Boolean.parseBoolean(prop.getProperty("USE_STARPATH")), ProjectConfiguration.useStarpath());
    }

    @Test
    public void testMultipleDatabases() throws Exception {
        boolean expected = Boolean.parseBoolean(prop.getProperty("MULTIPLE_DATABASES"));
        assertEquals(expected, ProjectConfiguration.multipleDatabases());
    }

    @Test
    public void testFromToTable() throws Exception {        
        String key = "FROMTO_TABLE";
        ProjectConfiguration.useProperties1();
        assertEquals(prop.getProperty(key + "1"), ProjectConfiguration.fromToTable());
        ProjectConfiguration.useProperties2();
        assertEquals(prop.getProperty(key + "2"), ProjectConfiguration.fromToTable());
    }

    @Test
    public void testDbpediaLanguagePrefix() throws Exception {
        String expected = prop.getProperty("DBPEDIA_LANGUAGE_PREFIX");
        assertEquals(expected, ProjectConfiguration.dbpediaLanguagePrefix());
    }

    @Test
    public void testResultDatabase() throws Exception {
        String key = "resultDatabase";
        ProjectConfiguration.useDefaultProperties();
        assertEquals(prop.getProperty(key), ProjectConfiguration.resultDatabase());

        ProjectConfiguration.useProperties1();
        assertEquals(prop.getProperty(key + "1"), ProjectConfiguration.resultDatabase());

        ProjectConfiguration.useProperties2();
        assertEquals(prop.getProperty(key + "2"), ProjectConfiguration.resultDatabase());
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
        String key = "resultDatabaseUser";
        ProjectConfiguration.useDefaultProperties();
        assertEquals(prop.getProperty(key), ProjectConfiguration.resultDatabaseUser());

        ProjectConfiguration.useProperties1();
        assertEquals(prop.getProperty(key + "1"), ProjectConfiguration.resultDatabaseUser());

        ProjectConfiguration.useProperties2();
        assertEquals(prop.getProperty(key + "2"), ProjectConfiguration.resultDatabaseUser());
    }

    @Test
    public void testResultDatabasePassword() throws Exception {
        String key = "resultDatabasePass";
        ProjectConfiguration.useDefaultProperties();
        assertEquals(prop.getProperty(key), ProjectConfiguration.resultDatabasePassword());

        ProjectConfiguration.useProperties1();
        assertEquals(prop.getProperty(key + "1"), ProjectConfiguration.resultDatabasePassword());

        ProjectConfiguration.useProperties2();
        assertEquals(prop.getProperty(key + "2"), ProjectConfiguration.resultDatabasePassword());
    }
}
