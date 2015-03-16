package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlacklistCategory {
	private List<String> blacklist;

    public BlacklistCategory(String blacklistFilename) throws IOException, NullPointerException {
        List<String> tmp = new ArrayList<String>();
        InputStream blackListIS = BlacklistCategory.class.getClassLoader().getResourceAsStream(blacklistFilename);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(blackListIS));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            tmp.add(line);
        }
        bufferedReader.close();
        this.setBlacklist(Collections.unmodifiableList(tmp));
    }

    public BlacklistCategory(List<String> blacklist){
        this.setBlacklist(blacklist);
    }

	public List<String> getBlacklist() {
		return blacklist;
	}

	public void setBlacklist(List<String> blacklist) {
		this.blacklist = blacklist;
	}

}
