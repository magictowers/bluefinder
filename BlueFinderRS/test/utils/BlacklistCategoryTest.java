package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class BlacklistCategoryTest {
	private ProjectSetup projectSetup;
	private BlacklistCategory blacklistCategory;

	@Before
	public void setUp() throws Exception {
		projectSetup = new ProjectSetup();
	}

	@Test
	public void testBlacklistCategoryString() {
		try {
			new BlacklistCategory(projectSetup.getBlacklistFilename());
		} catch (IOException e) {
			e.printStackTrace();
			fail("File not exists: "+projectSetup.getBlacklistFilename());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testBlacklistCategoryWrongString() throws Exception {
		new BlacklistCategory("notAnExistingName.wrong");
	}

	@Test
	public void testBlacklistCategoryListOfString() {
		List<String> ls = Arrays.asList("test1", "tes2", "test3");
		blacklistCategory = new BlacklistCategory(ls);
		assertEquals(ls.toString(),blacklistCategory.getBlacklist().toString());
	}

	@Test
	public void testSetBlacklist() {
		List<String> ls = Arrays.asList("test1", "tes2", "test3");
		List<String> ls2 = Arrays.asList("test1", "tes2", "test3", "test4");
		blacklistCategory = new BlacklistCategory(ls);
		blacklistCategory.setBlacklist(ls2);
		assertEquals(ls2.toString(),blacklistCategory.getBlacklist().toString());
	}

}
