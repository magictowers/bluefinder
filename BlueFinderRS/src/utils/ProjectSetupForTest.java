
package utils;

/**
 *
 * @author mkaminose
 */
@Deprecated
public class ProjectSetupForTest extends ProjectSetup {

    public ProjectSetupForTest() {
        
        setCategoryPrefix("Category:");
        setLanguageCode("en");
        setDbpediaTransPrefix("http://dbpedia.org/resource/");
        setPathStrategy("unstarred");
        setDbpediaPrefix("http://dbpedia.org/resource/");
        setBlacklistFilename("blacklist_category_default.txt");
        setTranslate(false);
        setCreateEnhancedTable(false);
    }
    
}
