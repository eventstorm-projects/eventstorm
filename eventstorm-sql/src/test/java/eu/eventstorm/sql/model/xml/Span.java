package eu.eventstorm.sql.model.xml;

import eu.eventstorm.sql.type.Xml;

public final class Span {

	private int id;
	private Xml content;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Xml getContent() {
		return content;
	}
	public void setContent(Xml content) {
		this.content = content;
	}
	
}
