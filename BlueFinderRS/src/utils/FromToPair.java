package utils;

public class FromToPair {

	private String separator = " , ";
	private String from = "";
	private String to = "";
	private String fromWildcard = "#from";
	private String toWildcard = "#to";
	
	public FromToPair() {}
	
	public FromToPair(String pair) {
		this.split(pair);
	}
	
	public String getConcatPair() {
		return this.from + this.separator + this.to;
	}

	public void split(String pair) {
		String[] splittedPair = pair.split(this.separator);
		if (splittedPair.length == 2) {
			this.from = splittedPair[0];
			String tmp = splittedPair[1];
			if (tmp.contains(" ")) {
				this.to = tmp.substring(0, tmp.indexOf(" "));
			}
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
		path = path.replace(this.fromWildcard, this.from);
		return path.replace(this.toWildcard, this.to);
	}
	
	public boolean pathHasWildCards(String path) {
		return path.contains(this.fromWildcard) || path.contains(this.toWildcard);
	}
		
	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		if (obj != null && obj instanceof FromToPair) {
			FromToPair objPair = (FromToPair) obj;
			if (objPair.getFrom() == this.getFrom() && objPair.getTo() == this.getTo()) {
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

	public String getFromWildcard() {
		return fromWildcard;
	}

	public void setFromWildcard(String fromWildcard) {
		this.fromWildcard = fromWildcard;
	}

	public String getToWildcard() {
		return toWildcard;
	}

	public void setToWildcard(String toWildcard) {
		this.toWildcard = toWildcard;
	}
	
	public String toString() {
		return this.getConcatPair();
	}
}
