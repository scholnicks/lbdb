package net.scholnick.lbdb.domain;


public enum Media implements Comparable<Media> {
	BOOK {
		public Integer getId()   { return 1; }
		public String toString() { return "Book"; }
	},
	KINDLE {
		public Integer getId()   { return 2; }
		public String toString() { return "Kindle"; }
	},
	NOOK {
		public Integer getId()   { return 3; }
		public String toString() { return "Nook"; }
	}
	;
	
	public abstract Integer getId();
	
	public static Media from(Integer id) {
		if (id == null) {
			return null;
		}
		
		for (Media m: Media.values()) {
			if (m.getId().equals(id)) {
				return m;
			}
		}
		
		return null;
	}
}
