package net.scholnick.lbdb.domain;


public enum BookType implements Comparable<BookType> {
	FICTION {
		public Integer getId() { return 1; }
		public String toString() { return "Fiction"; }
	},
	NON_FICTION {
		public Integer getId() { return 2; }
		public String toString() { return "Non-Fiction"; }
	},
	TECHNICAL {
		public Integer getId() { return 3; }
		public String toString() { return "Technical"; }
	}
	;
	
	public abstract Integer getId();
	
	public static BookType from(Integer id) {
		if (id == null) return null;
		
		for (BookType t: BookType.values()) {
			if (t.getId().equals(id)) {
				return t;
			}
		}
		
		return null;
	}
}
