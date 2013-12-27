package utils;

public class FromToPair {

	public static String SEPARATOR = " , ";
	private String separator;
	private String from = "";
	private String to = "";
    private String language = "en";
	public static String FROM_WILDCARD = "#from";
	public static String TO_WILDCARD = "#to";
	
	public FromToPair() {
		this.separator = SEPARATOR;
	}
	
	public FromToPair(String pair) {
		this();
		this.setPair(pair);
	}
	
	public FromToPair(String pair, String separator) {
		this.separator = separator;
		this.setPair(pair);
	}
    
    public FromToPair(String from, String to, String language) {
        super();
        this.from = from;
        this.to = to;
        this.language = language;
    }
	
	public String getConcatPair() {
		return this.from + this.separator + this.to;
	}
	
	public static String concatPair(String from, String to) {
		return from + SEPARATOR + to;
	}
	
	public static FromToPair splitPair(String strPair) {
		FromToPair pair = new FromToPair(strPair);
		return pair;
	}

	public void setPair(String pair) {
		String[] splittedPair = pair.split(this.separator);
		if (splittedPair.length == 2) {
			this.from = splittedPair[0];
			String tmp = splittedPair[1];
			if (tmp.contains(" ")) {
				tmp = tmp.substring(0, tmp.indexOf(" "));
			}
			this.to = tmp;
		}
	}
	
	/**
	 * Takes a path and replaces {@link #fromWildcard} and {@link #toWildcard} 
	 * with its corresponding values.
	 * 
	 * @param path
	 * @return replaced path
	 */
	public String generateFullPath(String path) {
		path = path.replace(FROM_WILDCARD, this.from);
		return path.replace(TO_WILDCARD, this.to);
	}
	
	public boolean pathHasWildCards(String path) {
		return path.contains(FROM_WILDCARD) || path.contains(TO_WILDCARD);
	}
		
	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		if (obj != null && obj instanceof FromToPair) {
			FromToPair objPair = (FromToPair) obj;
			if (objPair.getFrom().equals(this.getFrom()) && objPair.getTo().equals(this.getTo())) {
				equals = true;
			}
		}
		return equals;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
    @Override
	public String toString() {
		return this.getConcatPair();
	}

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }
}
