package net.scholnick.lbdb.domain;

public abstract class BasicObject {
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isNew() {
		return getId() == null;
	}
}
