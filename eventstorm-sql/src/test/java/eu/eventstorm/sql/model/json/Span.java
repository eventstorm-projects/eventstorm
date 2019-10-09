package eu.eventstorm.sql.model.json;

import eu.eventstorm.sql.type.Json;

public final class Span {

	private int id;
	private Json content;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Json getContent() {
		return content;
	}
	public void setContent(Json content) {
		this.content = content;
	}
	
}
